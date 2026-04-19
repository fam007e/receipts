package com.fam007e.receipts.ui.screens.capture

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.fam007e.receipts.data.preferences.UserPreferences
import com.fam007e.receipts.domain.model.Person
import com.fam007e.receipts.domain.model.Receipt
import com.fam007e.receipts.domain.repository.PersonRepository
import com.fam007e.receipts.domain.repository.ReceiptRepository
import com.fam007e.receipts.domain.usecase.AchievementEvaluator
import com.fam007e.receipts.worker.AutoAvatarManager
import com.fam007e.receipts.worker.EmailThresholdWorker
import com.fam007e.receipts.data.db.dao.ReceiptDao
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class CaptureViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val personRepository: PersonRepository,
    private val receiptRepository: ReceiptRepository,
    private val receiptDao: ReceiptDao,
    private val achievementEvaluator: AchievementEvaluator,
    private val autoAvatarManager: AutoAvatarManager,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val workManager = WorkManager.getInstance(context)

    val persons: StateFlow<List<Person>> = personRepository.getAllPersons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveReceipt(
        personId: Long,
        categoryId: Long?,
        mediaPath: String,
        mediaType: String,
        isPositive: Boolean,
        note: String = ""
    ) {
        viewModelScope.launch {
            val receipt = Receipt(
                personId = personId,
                categoryId = categoryId,
                mediaPath = mediaPath,
                mediaType = mediaType,
                isPositive = isPositive,
                note = note,
                thumbnailPath = if (mediaType == "video") generateThumbnail(mediaPath) else null,
                timestamp = System.currentTimeMillis()
            )
            receiptRepository.insertReceipt(receipt)

            // Side Effects
            autoAvatarManager.refreshAutoAvatar(personId)
            
            // Moral mechanic: Earn 1 Loot Credit for every 5 positive receipts
            if (isPositive) {
                val totalPositives = receiptDao.getPositiveCount(personId)
                if (totalPositives > 0 && totalPositives % 5 == 0) {
                    userPreferences.addLootCredits(1)
                }
            }

            if (categoryId != null) {
                val emailEnabled = userPreferences.emailEnabled.first()
                if (emailEnabled) {
                    val count = receiptRepository.getCategoryCount(personId, categoryId)
                    val category = receiptRepository.getCategoryById(categoryId)
                    if (category != null && count >= category.threshold) {
                        val workRequest = OneTimeWorkRequestBuilder<EmailThresholdWorker>()
                            .setInputData(workDataOf(
                                "person_id" to personId,
                                "category_id" to categoryId
                            ))
                            .build()
                        workManager.enqueue(workRequest)
                    }
                }
            }

            // Evaluate achievements
            achievementEvaluator.evaluate(personId)
        }
    }

    fun saveToInbox(mediaPath: String, mediaType: String) {
        viewModelScope.launch {
            val inboxPerson = personRepository.getAllPersons().stateIn(viewModelScope).value
                .find { it.name == "Inbox" }
            
            val personId = inboxPerson?.id ?: personRepository.insertPerson(
                Person(
                    name = "Inbox",
                    relationship = "Self",
                    avatarPath = null,
                    autoAvatarCategoryId = null,
                    isMe = false,
                    mode = "receipts"
                )
            )

            saveReceipt(
                personId = personId,
                categoryId = null,
                mediaPath = mediaPath,
                mediaType = mediaType,
                isPositive = false,
                note = "Saved to Inbox"
            )
        }
    }

    private suspend fun generateThumbnail(videoPath: String): String? = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        try {
            val uri = Uri.parse(videoPath)
            retriever.setDataSource(context, uri)
            val bitmap = retriever.getFrameAtTime(1000000) // 1 second in
            if (bitmap != null) {
                val thumbFile = File(context.cacheDir, "thumb_${System.currentTimeMillis()}.jpg")
                FileOutputStream(thumbFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
                }
                thumbFile.absolutePath
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }
}

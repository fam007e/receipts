package com.fam007e.receipts.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fam007e.receipts.domain.repository.ReceiptRepository
import com.fam007e.receipts.domain.repository.PersonRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class EmailThresholdWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val receiptRepository: ReceiptRepository,
    private val personRepository: PersonRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val personId = inputData.getLong("person_id", -1)
        val categoryId = inputData.getLong("category_id", -1)
        
        if (personId == -1L || categoryId == -1L) return Result.failure()

        val person = personRepository.getPersonById(personId) ?: return Result.failure()
        val count = receiptRepository.getCategoryCount(personId, categoryId)
        val receipts = receiptRepository.getRecentsInCategory(personId, categoryId, 10)
        
        val emailBody = buildString {
            appendLine("--- SIMULATED CONSTRUCTIVE EMAIL BLAST ---")
            appendLine("To: ${person.name} (simulated)")
            appendLine("Subject: It's happened another $count times.")
            appendLine()
            appendLine("This is an automated coaching message from Receipts™.")
            appendLine("The category '${categoryId}' (ID) has reached $count instances.")
            appendLine()
            appendLine("Recent Evidence:")
            receipts.forEach { r ->
                appendLine("- ${java.util.Date(r.timestamp)}: ${r.note.ifEmpty { "No note" }}")
            }
            appendLine()
            appendLine("-------------------------------------------")
            appendLine("Are you tired of receiving emails like this? 🙂")
            appendLine("Family law resources are available in your area.")
            appendLine("[Find an attorney near you] → https://receipts.app/legal")
            appendLine("-------------------------------------------")
        }
        
        android.util.Log.d("EmailWorker", emailBody)

        return Result.success()
    }
}

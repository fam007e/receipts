package com.fam007e.receipts.worker

import com.fam007e.receipts.data.db.dao.PersonDao
import com.fam007e.receipts.data.db.dao.ReceiptDao
import javax.inject.Inject

class AutoAvatarManager @Inject constructor(
    private val receiptDao: ReceiptDao,
    private val personDao: PersonDao
) {
    /**
     * Gets the most recent photo from the person's top receipt category.
     * Sets it as their avatar thumbnail.
     */
    suspend fun refreshAutoAvatar(personId: Long) {
        val topCategoryId = receiptDao.getTopCategoryId(personId) ?: return
        val latestReceipt = receiptDao.getLatestInCategory(personId, topCategoryId) ?: return

        val avatarPath = when (latestReceipt.mediaType) {
            "photo" -> latestReceipt.mediaPath
            "video" -> latestReceipt.thumbnailPath ?: return
            else    -> return
        }

        personDao.updateAutoAvatar(personId, avatarPath, topCategoryId)
    }
}

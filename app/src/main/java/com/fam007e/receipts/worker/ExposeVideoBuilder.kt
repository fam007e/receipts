package com.fam007e.receipts.worker

import android.content.Context
import com.antonkarpenko.ffmpegkit.FFmpegKit
import com.fam007e.receipts.data.db.entities.ReceiptEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class ExposeVideoBuilder @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Builds a shame compilation video:
     * - Slideshow of top-category receipt photos
     * - Text overlay: category name
     * - Final frame: 😬
     */
    suspend fun buildExposeVideo(
        categoryName: String,
        receipts: List<com.fam007e.receipts.domain.model.Receipt>
    ): String? = withContext(Dispatchers.IO) {

        val outputPath = "${context.filesDir}/expose_${System.currentTimeMillis()}.mp4"
        
        // Filter for photo receipts
        val photos = receipts.filter { it.mediaType == "photo" }
        if (photos.isEmpty()) return@withContext null

        val concatFile = File(context.cacheDir, "concat_list.txt")
        val photoList = photos.joinToString("\n") { r ->
            "file '${r.mediaPath}'\nduration 2.5"
        }
        concatFile.writeText(photoList)

        // Basic FFmpeg command to create a slideshow
        val cmd = buildString {
            append("-f concat -safe 0 -i ${concatFile.absolutePath} ")
            append("-vf \"scale=1080:1920:force_original_aspect_ratio=decrease,")
            append("pad=1080:1920:(ow-iw)/2:(oh-ih)/2,")
            append("drawtext=text='$categoryName':fontsize=48:fontcolor=white:")
            append("x=(w-text_w)/2:y=h-100:shadowcolor=black:shadowx=2:shadowy=2\" ")
            append("-c:v libx264 -pix_fmt yuv420p -r 30 $outputPath")
        }

        val session = FFmpegKit.execute(cmd)
        if (session.returnCode.isValueSuccess) {
            outputPath
        } else {
            null
        }
    }
}

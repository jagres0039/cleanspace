package com.cleanspace.app.core.work

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.cleanspace.app.MainActivity
import com.cleanspace.app.R
import com.cleanspace.app.core.util.formatBytes

/** Posts the occasional “you can free up X” reminder from the background scan. */
object ScanNotifier {
    const val CHANNEL_ID = "cleanspace_scan"
    private const val NOTIF_ID = 4201

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Pengingat pembersihan",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Notifikasi saat ada ruang penyimpanan yang bisa dibersihkan"
        }
        val mgr = context.getSystemService(NotificationManager::class.java)
        mgr?.createNotificationChannel(channel)
    }

    fun showReclaimable(context: Context, bytes: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }
        ensureChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Bisa dibersihin nih ✨")
            .setContentText("Ada ${formatBytes(bytes)} yang bisa kamu kosongkan. Tap buat bersih-bersih.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()

        runCatching { NotificationManagerCompat.from(context).notify(NOTIF_ID, notif) }
    }
}

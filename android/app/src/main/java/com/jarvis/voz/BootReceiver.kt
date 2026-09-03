package com.jarvis.voz

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Volta a escutar sozinho depois que o celular reinicia. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        if (!Memoria(ctx).iniciarNoBoot) return
        val i = Intent(ctx, JarvisService::class.java).setAction(JarvisService.ACAO_INICIAR)
        ctx.startForegroundService(i)
    }
}

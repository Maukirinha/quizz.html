package com.jarvis.voz

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.AlarmClock
import android.provider.Settings

/** Controle real dos recursos do celular. */
class Aparelho(private val ctx: Context) {

    private fun nova(i: Intent) = i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    /* ---------------- bateria e rede ---------------- */

    fun bateriaPct(): Int {
        val bm = ctx.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    fun carregando(): Boolean {
        val bm = ctx.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return bm.isCharging
    }

    fun rede(): String {
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val cap = cm.getNetworkCapabilities(cm.activeNetwork) ?: return "sem conexão"
        return when {
            cap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
            cap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "dados móveis"
            cap.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "cabo"
            else -> "conexão desconhecida"
        }
    }

    /* ---------------- lanterna ---------------- */

    private var idCamera: String? = null

    fun lanterna(ligar: Boolean): Boolean = try {
        val cm = ctx.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        if (idCamera == null) {
            idCamera = cm.cameraIdList.firstOrNull { id ->
                cm.getCameraCharacteristics(id)
                    .get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
        }
        idCamera?.let { cm.setTorchMode(it, ligar); true } ?: false
    } catch (e: Exception) { false }

    /* ---------------- volume ---------------- */

    fun volume(pct: Int): Int {
        val am = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val alvo = (max * pct.coerceIn(0, 100) / 100.0).toInt()
        am.setStreamVolume(AudioManager.STREAM_MUSIC, alvo, 0)
        return pct.coerceIn(0, 100)
    }

    fun volumeAtual(): Int {
        val am = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        return am.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / max
    }

    fun silencioso(ligar: Boolean) {
        val am = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.ringerMode = if (ligar) AudioManager.RINGER_MODE_VIBRATE else AudioManager.RINGER_MODE_NORMAL
    }

    /* ---------------- brilho (exige permissão especial) ---------------- */

    fun podeMexerNoBrilho(): Boolean = Settings.System.canWrite(ctx)

    fun pedirPermissaoBrilho() {
        nova(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
            android.net.Uri.parse("package:${ctx.packageName}"))).let { ctx.startActivity(it) }
    }

    fun brilho(pct: Int): Boolean {
        if (!podeMexerNoBrilho()) return false
        val v = (255 * pct.coerceIn(1, 100) / 100.0).toInt()
        Settings.System.putInt(ctx.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
        Settings.System.putInt(ctx.contentResolver, Settings.System.SCREEN_BRIGHTNESS, v)
        return true
    }

    fun brilhoAtual(): Int = try {
        Settings.System.getInt(ctx.contentResolver, Settings.System.SCREEN_BRIGHTNESS) * 100 / 255
    } catch (e: Exception) { 50 }

    /* ---------------- painéis do sistema ---------------- */

    fun painelWifi() = ctx.startActivity(nova(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            Intent(Settings.Panel.ACTION_WIFI) else Intent(Settings.ACTION_WIFI_SETTINGS)))

    fun painelBluetooth() = ctx.startActivity(nova(Intent(Settings.ACTION_BLUETOOTH_SETTINGS)))
    fun painelDados() = ctx.startActivity(nova(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS)))
    fun painelAviao() = ctx.startActivity(nova(Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)))
    fun configuracoes() = ctx.startActivity(nova(Intent(Settings.ACTION_SETTINGS)))

    /** Não perturbe. Depende de autorização de acesso às notificações. */
    fun naoPerturbe(ligar: Boolean): Boolean {
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!nm.isNotificationPolicyAccessGranted) {
            ctx.startActivity(nova(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)))
            return false
        }
        nm.setInterruptionFilter(
            if (ligar) NotificationManager.INTERRUPTION_FILTER_NONE
            else NotificationManager.INTERRUPTION_FILTER_ALL)
        return true
    }

    /* ---------------- relógio do sistema ---------------- */

    /** Cria um alarme de verdade no app de relógio do celular. */
    fun alarme(hora: Int, minuto: Int, rotulo: String) {
        ctx.startActivity(nova(Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, hora)
            putExtra(AlarmClock.EXTRA_MINUTES, minuto)
            putExtra(AlarmClock.EXTRA_MESSAGE, rotulo)
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
        }))
    }

    fun temporizador(segundos: Int, rotulo: String) {
        ctx.startActivity(nova(Intent(AlarmClock.ACTION_SET_TIMER).apply {
            putExtra(AlarmClock.EXTRA_LENGTH, segundos)
            putExtra(AlarmClock.EXTRA_MESSAGE, rotulo)
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
        }))
    }

    /* ---------------- vibração ---------------- */

    @Suppress("DEPRECATION")
    fun vibrar(ms: Long) {
        val v = ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
        else v.vibrate(ms)
    }
}

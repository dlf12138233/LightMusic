package com.brins.lightmusic.player

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.brins.lightmusic.R
import com.brins.lightmusic.model.Music
import com.brins.lightmusic.model.loaclmusic.PlayList
import java.lang.Exception
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.getSystemService
import com.brins.lightmusic.player.broadcast.HeadsetButtonReceiver


class PlayBackService : Service(), IPlayback, HeadsetButtonReceiver.onHeadsetListener {



    companion object {
        @JvmStatic
        private val CHANNEL_ID = "com.brins.lightmusic.notification.channel"
        private val ACTION_PLAY_TOGGLE = "com.brins.lightmusic.ACTION.PLAY_TOGGLE"
        private val ACTION_PLAY_LAST = "com.brins.lightmusic.ACTION.PLAY_LAST"
        private val ACTION_PLAY_NEXT = "com.brins.lightmusic.ACTION.PLAY_NEXT"
        private val ACTION_STOP_SERVICE = "com.brins.lightmusic.ACTION.STOP_SERVICE"
        private val NOTIFICATION_ID = 1
        var mIsServiceBound: Boolean = false
    }

    private val mPlayer: Player by lazy { Player.getInstance() }
    private val mBinder = LocalBinder()
    private lateinit var mNotificationManager: NotificationManager


    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    inner class LocalBinder : Binder() {
        val service: PlayBackService
            get() = this@PlayBackService
    }

    override fun onCreate() {
        super.onCreate()
        HeadsetButtonReceiver(this)
        mIsServiceBound = true
        MediaSessionManager(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        cancelNotification()
    }

    override fun stopService(name: Intent): Boolean {
        stopForeground(true)
        return super.stopService(name)
    }

    override fun onDestroy() {
        releasePlayer()
        try {
            val intent = Intent(applicationContext, PlayBackService::class.java)
            startService(intent)
        } catch (e: Exception) {

        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance)
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            builder.setSmallIcon(R.drawable.ic_music_note_white_48dp)
                .setContentTitle("轻籁")
                .setContentText("运行中")
            mNotificationManager = getSystemService(NotificationManager::class.java)
            mNotificationManager.createNotificationChannel(channel)
            startForeground(NOTIFICATION_ID, builder.build())

        }
    }

    private fun cancelNotification() {
        mNotificationManager.cancelAll() //从状态栏中移除通知
    }

    override fun playOrPause() {
        pause()
    }

    override fun playPreviousSong() {
        playLast()
    }

    override fun playNextSong() {
        playNext()
    }

    fun playMusic(){
        if (getPlayList().getNumOfSongs() == 0){
            return
        }
        val playList = getPlayList()
        playList.setPlayMode(PlayMode.getDefault())
        val song = playList.getCurrentSong()

    }



    //IPlayback
    override fun setPlayList(list: PlayList) {
        mPlayer.setPlayList(list)
    }

    override fun getPlayList(): PlayList {
        return mPlayer.getPlayList()
    }

    override fun play(): Boolean {
        return mPlayer.play()
    }

    override fun play(startIndex: Int): Boolean {
        return mPlayer.play(startIndex)
    }

    override fun play(song: Music): Boolean {
        return mPlayer.play(song)
    }

    override fun playLast(): Boolean {
        return mPlayer.playLast()
    }

    override fun playNext(): Boolean {
        return mPlayer.playNext()
    }

    override fun pause(): Boolean {
        return mPlayer.pause()
    }

    override fun isPlaying(): Boolean {
        return mPlayer.isPlaying()
    }

    override fun getProgress(): Int {
        return mPlayer.getProgress()
    }

    override fun getPlayingSong(): Music? {
        return mPlayer.getPlayingSong()
    }

    override fun seekTo(progress: Int): Boolean {
        return mPlayer.seekTo(progress)
    }

    override fun setPlayMode(playMode: PlayMode) {

        mPlayer.setPlayMode(playMode)
    }

    override fun registerCallback(callback: IPlayback.Callback) {
        mPlayer.registerCallback(callback)
    }

    override fun unregisterCallback(callback: IPlayback.Callback) {
        mPlayer.unregisterCallback(callback)
    }

    override fun removeCallbacks() {
        mPlayer.removeCallbacks()
    }

    override fun releasePlayer() {
        mPlayer.releasePlayer()
        super.onDestroy()
    }

    override fun stop() {
        mPlayer.stop()
    }

}

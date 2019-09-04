package riflocike.co.kr.serviceexample

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class MyService : Service() {
    var mThread: Thread? = null
    var mCount: Int = 0

    // MyService의 레퍼런스를 반환하는 Binder 객체
    private var mBinder = MyBinder()

    // Service와 Activity를 연결
    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d("onUnbind ", "onUnbind: ")
        return super.onUnbind(intent)
    }

    inner class MyBinder : Binder() {
        val service: MyService
            get() = this@MyService
    }

    fun getCount(): Int {
        return mCount
    }

    // startService를 실행하면 실행됨
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 백그라운드에서 하는 처리 넣어주면 됨
        if (intent?.action == "startForeground") {
            startForegroundService()
        } else if (mThread == null) {
            mThread = object : Thread("My Thread") {
                override fun run() {
                    for (i in 0..10) {
                        try {
                            mCount++
                            sleep(1000)
                        } catch (e: InterruptedException) {
                            break
                        }
                        Log.d("My Thread", "서비스 동작 중 $mCount")
                    }
                }
            }
            mThread?.start()
        }

        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("onCreate", "onCreate: ")
    }

    fun startForegroundService() {
        val builder = NotificationCompat.Builder(this, "default")
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentTitle("포그라운드 서비스")
        builder.setContentText("포그라운드 서비스 실행 중")
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        builder.setContentIntent(pendingIntent)

        // 오레오에서는 알림 채널을 매니저에 생성해야 한다
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(
                NotificationChannel(
                    "default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }

        // 포그라운드로 시작 (id는 0 이상)
        startForeground(1, builder.build())
    }

    override fun onDestroy() {
        Log.d("onDestroy", "서비스 종료")

        mThread?.interrupt()
        mThread = null
        mCount = 0

        super.onDestroy()
    }
}

package riflocike.co.kr.serviceexample

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private lateinit var mService: MyService
    private var mBound: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onStartService(view: View) {
        val intent = Intent(this, MyService::class.java)
        if (mBound == false) {
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        }

        startService(intent)
    }

    fun onStopService(view: View) {
        val intent = Intent(this, MyService::class.java)
        if (mBound == true) {
            unbindService(mConnection)
            mBound = false
        }

        stopService(intent)

    }

    fun onStartIntentService(view: View) {
        val intent = Intent(this@MainActivity, MyIntentService::class.java)
        startService(intent)
    }

    fun onStartForegroundService(view: View) {
        val intent = Intent(this@MainActivity, MyService::class.java)
        intent.action = "startForeground"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else
            startService(intent)
    }

    fun getCountValue(view: View) {
        if (mBound == true)
            Toast.makeText(this, "${mService.getCount()}", Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()

        // 서비스를 자동 생성해주고 bind까지 해줌
        var intent = Intent(this, MyService::class.java)
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()

        if (mBound == true) {
            unbindService(mConnection)
            mBound = false
        }
    }

    private var mConnection = object : ServiceConnection {
        // 예키지 않은 kill 이 되었을 때
        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
        }

        // MyBinder와 연결될 것이며 IBinder 타입으로 넘어오는 것을 캐스팅하여 사용
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MyService.MyBinder
            mService = binder.service
            mBound = true
        }
    }
}

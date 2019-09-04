package riflocike.co.kr.serviceexample

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.util.Log

class MyIntentService : IntentService("MyIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        for (i in 0..10) {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                break
            }
            Log.d("My Thread", "인텐트 서비스 동작 중 $i")
        }

    }
}

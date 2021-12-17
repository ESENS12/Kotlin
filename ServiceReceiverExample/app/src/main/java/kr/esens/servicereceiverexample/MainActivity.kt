package kr.esens.servicereceiverexample

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.PermissionChecker.checkSelfPermission
import kr.esens.servicereceiverexample.ui.theme.ServiceReceiverExampleTheme
import java.io.IOException

class MainActivity : AppCompatActivity() {
    lateinit var mContext : Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this.applicationContext
        requestPermission()

        var callListener = EndCallListener()
        var mTM: TelephonyManager =
            this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager;

        mTM.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);


        setContent {
            ServiceReceiverExampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }

    fun requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionResultACCESS_READ_PHONE: Int = checkSelfPermission(mContext!!, Manifest.permission.READ_PHONE_STATE)
            if (permissionResultACCESS_READ_PHONE == PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), 1000)
            } else {

            }
        } else {
            try {

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}


private class EndCallListener : PhoneStateListener() {
    override fun onCallStateChanged(state: Int, incomingNumber: String) {
        Log.e("TAG","onCallStateChanged!! : $state" )
        if (TelephonyManager.CALL_STATE_RINGING == state) {
            Log.e("LOG" + "Listener", "RINGING, number: $incomingNumber")
        }
        if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
            Log.e("LOG" + "Listener", "OFFHOOK, number: $incomingNumber")
        }
        if (TelephonyManager.CALL_STATE_IDLE == state) {
            Log.e("LOG" + "Listener", "IDLE, number: $incomingNumber")
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ServiceReceiverExampleTheme {
        Greeting("Android")
    }
}
package kr.esens.checknetworkusageexample

import android.Manifest
import android.app.AppOpsManager
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Process
import android.os.RemoteException
import android.telephony.TelephonyManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        checkPermission()

//        setupPermissions();


//        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
//        startActivity(intent)



        val networkStatsManager = applicationContext.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

        val bucket: NetworkStats.Bucket
        bucket = try {
            networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI,
                    "",
                    0,
                    System.currentTimeMillis())
        } catch (e: RemoteException) {
            Log.e("","exception : " + e.toString());
            //            return -1
        } as NetworkStats.Bucket
        Log.e("","getRxBytes : ${bucket.rxBytes}" )
        Log.e("","getTxBytes : ${bucket.txBytes}" )


        var networkStats: NetworkStats? = null
        networkStats = try {
            networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(this, ConnectivityManager.TYPE_MOBILE),
                    0,
                    System.currentTimeMillis(),
                    18317)
        } catch (e: RemoteException) {
            e.printStackTrace()
    //            return -1
        } as NetworkStats?
        val bucket2 = NetworkStats.Bucket()
        networkStats!!.getNextBucket(bucket2)
    }

    private fun getSubscriberId(context: Context, networkType: Int): String? {
        if (ConnectivityManager.TYPE_MOBILE == networkType) {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm.subscriberId
        }
        return ""
    }

    fun checkPermission() : Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(), packageName)
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return true
        }
        return false
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.PACKAGE_USAGE_STATS, Manifest.permission.READ_PHONE_STATE),
                100)
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {

        when(requestCode){
            100 ->{
//                if(grantResults.isNotEmpty()
//                            && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.e("","permission granted!")

                }else{
                    Log.e("","permission ok")
                }
                return
            }
        }
    }




}
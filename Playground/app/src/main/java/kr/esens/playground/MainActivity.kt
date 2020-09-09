package kr.esens.playground

import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {
    companion object {
        val TAG = "ESENS"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        /*
            val actionBar = supportActionBar
            if(actionBar != null) actionBar.setDisplayShowTitleEnabled(false)
            위와같은 기존 java와 비슷한 코드를
            아래 단 한줄로 변환 가능
        */
//        supportActionBar?.setDisplayShowTitleEnabled(false)
        //static -> companion object
//        Log.e("MainActivity ", SecondFragment.TAG);

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }


//        loopTest()

        //함수를 파라메터로 넘길때는 :: 사용
        고차함수(::someFunction)

        //단순한 함수는 굳이 함수를 만들지말고 람다식으로
        val lambda : (String) -> Unit = { str -> Log.e(TAG,"$str 람다함수") }

        // 축약형 = 변수명, {}, 함수타입 , 파라메터 -> 함수 내용
        val c = {str: String -> Log.e(TAG,"$str 축약형 람다함수")}

//        고차함수(lambda)
        UsingGeneric(A()).doShouting();
        UsingGeneric(B()).doShouting();
        UsingGeneric(C()).doShouting();
        doShouting(B());

    }

    fun loopTest(){
        loop_TEST@for (i in 1..10){
            for (j in 1..10){
                if(i == 1 && j == 3 ) break@loop_TEST
                    Log.e(TAG," i = $i , j = $j")
            }
        }
    }

    fun someFunction(str: String){
        Log.e(TAG,"$str 함수 someFunction")
    }

    // 한글 가능, 고차함수 테스트
    fun 고차함수 (function: (String) -> Unit) {
        function("b가 호출한")
    }


    open class A {
        open fun shout(){
            Log.e(TAG,"A가 소리칩니다")
        }
        fun ff(){
            Log.e(TAG,"shout")
        }
    }

    class B : A() {
        override fun shout(){
            Log.e(TAG,"B가 소리칩니다")
        }
    }

    class C: A(){
        override fun shout(){
            Log.e(TAG,"C가 소리칩니다")
        }
    }

    class UsingGeneric<T: A> (val t: T){
        fun doShouting(){
            t.shout()
        }
    }

    fun <T:A> doShouting(t: T){
        t.shout()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings, R.id.action_favorite -> true
            else -> false
        }
    }
}
package kr.esens.coroutine_example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis


class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        main()
//        scopeTest()
//        suspended_main();
//        cancel_main()
//        cancle_and_join_main();

//        cancel_and_join_main_vol2();

//        non_cancleable_block();
//        timeout_block();
//        timeout_withoutOrNull();

        basic_sequential();
    }


    //GlobalScope.launch 가 아닌 . GlobalScope 내부의 child 로 scope를 새로 생성해서 사용 하도록

    fun main() = runBlocking { // this: CoroutineScope
        launch { // launch a new coroutine in the scope of runBlocking
            delay(1000L)
            Log.d(TAG,"World!")
        }
        Log.d(TAG,"Hello,")
    }



    fun scopeTest() = runBlocking { // this: CoroutineScope
        launch {
            delay(200L)
            Log.d(TAG,"Task from runBlocking")
        }

        coroutineScope { // Creates a coroutine scope
            launch {
                delay(500L)
                Log.d(TAG,"Task from nested launch")
            }

            delay(100L)
            Log.d(TAG,"Task from coroutine scope") // This line will be printed before the nested launch
        }

        Log.d(TAG,"Coroutine scope is over") // This line is not printed until the nested launch completes
    }



    fun suspended_main() = runBlocking {
        launch { doWorld() }
        Log.d(TAG,"Hello,")
    }

    // this is your first suspending function
    suspend fun doWorld() {
        delay(1000L)
        Log.d(TAG,"World!")
    }


    fun cancel_main() = runBlocking {

        val job = launch {
            repeat(1000) { i ->
                Log.d(TAG,"job: I'm sleeping $i ...")
                delay(500L)
            }
        }
        delay(1300L) // delay a bit
        Log.d(TAG,"main: I'm tired of waiting!")
        job.cancel() // cancels the job
        job.join() // waits for job's completion
        Log.d(TAG,"main: Now I can quit.")

    }

    /**
     *      Coroutine cancellation is cooperative.
     *      코루틴 내의 작업이 끝나기 전까지 기다림.
     *
     * */
    fun cancle_and_join_main() = runBlocking {

        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0
            while (i<5) { // computation loop, just wastes CPU
                // print a message twice a second
                if (System.currentTimeMillis() >= nextPrintTime) {
                    Log.d(TAG,"job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500L
                }
            }
        }
        delay(1300L) // delay a bit
        Log.d(TAG,"main: I'm tired of waiting!")

        job.cancelAndJoin() // cancels the job and waits for its completion
        Log.d(TAG,"main: Now I can quit.")

    }


    /**
     *      작업 중단 예제 , 
     *
     * */

    fun cancel_and_join_main_vol2() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            try {
                var nextPrintTime = startTime
                var i = 0
                while (isActive) { // cancellable computation loop
                    // print a message twice a second
                    if (System.currentTimeMillis() >= nextPrintTime) {
                        Log.d(TAG, "job: I'm sleeping ${i++} ...")
                        nextPrintTime += 500L
                    }
                }
            } finally {
                Log.d(TAG,"job: i'm running finally");
            }
        }
        delay(1300L) // delay a bit
        Log.d(TAG,"main: I'm tired of waiting!")
        job.cancelAndJoin() // cancels the job and waits for its completion
        Log.d(TAG,"main: Now I can quit.")

    }
    
    fun non_cancleable_block() = runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    Log.d(TAG,"job: I'm sleeping $i ...")
                    delay(500L)
                }
            } finally {
                withContext(NonCancellable) {
                    Log.d(TAG,"job: I'm running finally")
                    delay(1000L)
                    Log.d(TAG,"job: And I've just delayed for 1 sec because I'm non-cancellable")
                    repeat(5){
                        i ->
                        Log.d(TAG,"job: I'm still alive bitch! $i");
                        delay(200L);
                    }
                }
            }
        }
        delay(1300L) // delay a bit
        Log.d(TAG,"main: I'm tired of waiting!")
        job.cancelAndJoin() // cancels the job and waits for its completion
        Log.d(TAG,"main: Now I can quit.")
    }


    /**
     *  TimeoutCancellationException 발생.
     * */
    fun timeout_block() = runBlocking {

        withTimeout(1300L) {
            repeat(1000) { i ->
                Log.d(TAG,"I'm sleeping $i ...")
                delay(500L)
            }
        }

    }

    fun timeout_withoutOrNull() = runBlocking {

        val result = withTimeoutOrNull(1300L) {
            repeat(2) { i ->
                Log.d(TAG,"I'm sleeping $i ...")
                delay(500L)
            }
            "Done" // will get cancelled before it produces this result
        }

        //중간에 종료가 된다면 (timeout) result 는 null, 그게 아니면 Done 이 출력된다
        Log.d(TAG,"Result is $result")

    }


    suspend fun doSomethingUsefulOne(): Int{
        delay(3000L)
        return 13
    }

    suspend fun doSomethingUsefulTwo(): Int{
        delay(4000L)
        return 29
    }

    fun basic_sequential() = runBlocking {
        Log.e(TAG,"start --")
        val time = measureTimeMillis {
            // one + two 만큼 실행
            val one = doSomethingUsefulOne()
            val two = doSomethingUsefulTwo()
            Log.d(TAG,"The answer is ${one + two}")
        }

        Log.e(TAG,"Completed in $time ms")
        Log.e(TAG,"start ---async");
        // 동시 실행하므로 one, two 중 오래 걸리는 시간만큼
        val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
        val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
        // some computation
        one.start() // start the first one
        two.start() // start the second one
        Log.e(TAG,"The answer is ${one.await() + two.await()}")

    }


}
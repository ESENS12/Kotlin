package kr.esens.handliemultitouchevent

import android.graphics.Color
import android.graphics.Rect
import android.graphics.RectF

import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.MotionEvent.INVALID_POINTER_ID
import android.view.MotionEvent.actionToString
import android.view.ScaleGestureDetector
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.view.MotionEventCompat
import kr.esens.handliemultitouchevent.ui.theme.HandlieMultiTouchEventTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    public val TAG = "MainActivity.kt";
    var mActivePointerId = INVALID_POINTER_ID
    private var mScaleDetector: ScaleGestureDetector? = null
    var gesturedetector: GestureDetector? = null
    var mLastTouchX : Float = 0F
    var mLastTouchY : Float = 0F

    private val mCurrentViewport = RectF(0F, 0F, 100F, 100F)
    private val mContentRect: Rect? = null

    var mPosX : Float = 0F
    var mPosY : Float = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HandlieMultiTouchEventTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
        gesturedetector = GestureDetector(this, mGestureListener)

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        val action = ev!!.action
        gesturedetector!!.onTouchEvent(ev)

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                ev.actionIndex.also { pointerIndex ->
                    // Remember where we started (for dragging)

                    mLastTouchX = ev.getX(pointerIndex)
                    mLastTouchY = ev.getY(pointerIndex)
                }

                // Save the ID of this pointer (for dragging)
                mActivePointerId = ev.getPointerId(0)
            }

            MotionEvent.ACTION_MOVE -> {
                // Find the index of the active pointer and fetch its position
                val (x: Float, y: Float) =
                    ev.findPointerIndex(mActivePointerId).let { pointerIndex ->
                        // Calculate the distance moved
                        ev.getX(pointerIndex) to ev.getY(pointerIndex)
                    }

                mPosX += x - mLastTouchX
                mPosY += y - mLastTouchY

                // Remember this touch position for the next move event
                mLastTouchX = x
                mLastTouchY = y
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mActivePointerId = INVALID_POINTER_ID
                mPosX = 0F;
                mPosY = 0F;
            }

            MotionEvent.ACTION_POINTER_UP -> {

                ev.actionIndex.also { pointerIndex ->
                    ev.getPointerId(pointerIndex)
                        .takeIf { it == mActivePointerId }
                        ?.run {
                            // This was our active pointer going up. Choose a new
                            // active pointer and adjust accordingly.
                            val newPointerIndex = if (pointerIndex == 0) 1 else 0
                            mLastTouchX = ev.getX(newPointerIndex)
                            mLastTouchY = ev.getY(newPointerIndex)
                            mActivePointerId = ev.getPointerId(newPointerIndex)
                        }
                }
            }
        }
//        return false
        return super.dispatchTouchEvent(ev)
    }

    private val mGestureListener = object : GestureDetector.SimpleOnGestureListener() {

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            // Scrolling uses math based on the viewport (as opposed to math using pixels).
            Log.e(TAG,"터치 변위량 x :  ${mPosX} , y : ${mPosY}")
            mContentRect?.apply {
                // Pixel offset is the offset in screen pixels, while viewport offset is the
                // offset within the current viewport.
                val viewportOffsetX = distanceX * mCurrentViewport.width() / width()
                val viewportOffsetY = -distanceY * mCurrentViewport.height() / height()

                // Updates the viewport, refreshes the display.
                setViewportBottomLeft(
                    mCurrentViewport.left + viewportOffsetX,
                    mCurrentViewport.bottom + viewportOffsetY
                )
            }

            return true
        }
    }

    private fun setViewportBottomLeft(fl: Float, fl1: Float) {
        Log.e(TAG,"fl : ${fl} , fl1 : ${fl1}")
    }


}


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HandlieMultiTouchEventTheme {
        Greeting("Android")
    }
}
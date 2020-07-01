package kr.esens.playground

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SecondFragment : Fragment() {

    /*
    *
    *   lateinit 특징 (메모리 낭비를 막기 위해 lazy loading 처럼 필요할 때 초기화 하는것)
    *  var(mutable)에서만 사용이 가능하다
        var이기 때문에 언제든 초기화를 변경할 수 있다.
        null을 통한 초기화를 할 수 없다.
        초기화를 하기 전에는 변수에 접근할 수 없다.
        lateinit property subject has not been initialized
        변수에 대한 setter/getter properties 정의가 불가능하다.
        lateinit은 모든 변수가 가능한 건 아니고, primitive type에서는 활용이 불가능하다(Int, Double 등)

    *
    * */
    private lateinit var tvLateInit: TextView

    //private static final String TAG = "MyActivity"; 를 아래 처럼 간략화(auto casting type)
    private val TAG = "SecondFragment"

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // var 는 mutable , val 은 immutable이라고 알고있는데, lateinit은 var 타입으로(심지어 var로 명시해줌)) 언제든지 초기화를 변경할 수 있다고 하는데..
        // code intelligence에서는 final로 안내하는 이유가..?

        //not nullable object assign
        tvLateInit = view.findViewById(R.id.tv_late_init)!!
        Log.d(TAG,"tvLateInit.text : " + tvLateInit.text);

        //must not be null exception 발생,
//        tvLateInit = view.findViewById(R.id.textview_first);
//        Log.d(TAG,"tvLateInit.text : " + tvLateInit.text);

        view.findViewById<Button>(R.id.button_second).setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }
}
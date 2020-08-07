package kr.esens.searchnblogwithoutads

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.esens.searchnblogwithoutads.MainActivity.Companion.TAG

class BlogImageAdapter(val mContext: Context, val mData: ArrayList<String>) :
    RecyclerView.Adapter<BlogImageAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.blog_listitem_img, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mData,position, mContext)
    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        val iv_thumbnail = itemView?.findViewById<ImageView>(R.id.iv_thumbnail)
        val tv_index = itemView?.findViewById<TextView>(R.id.tv_index)

        fun bind (list: ArrayList<String>, position: Int,context: Context) {
            Log.e(TAG,"blogImageAdapter holder.bind called!");
            if (iv_thumbnail != null) {
                Glide.with(context)
                    .load(list[position])
                    //                    .override(이미지 사이즈) //만약 리소스 조정이 필요하다면, 캐시낭비를 막는데 효과적임
                    //                .placeholder(R.drawable.loading)
                    //                .error(R.drawable.image_not_found)
                    .into(iv_thumbnail)
            }
            tv_index?.text = "$position"

        }
    }
}
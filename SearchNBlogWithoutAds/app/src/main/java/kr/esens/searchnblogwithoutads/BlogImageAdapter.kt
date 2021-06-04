package kr.esens.searchnblogwithoutads

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class BlogImageAdapter(val mContext: Context, val mData: ArrayList<String>) :

    RecyclerView.Adapter<BlogImageAdapter.Holder>() {
    private var lastPosition = -1;
    private var TAG = "BlogImageAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.blog_listitem_img, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mData, position, mContext)
    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {


        val iv_thumbnail = itemView?.findViewById<ImageView>(R.id.iv_thumbnail)

        fun bind(list: ArrayList<String>, position: Int, context: Context) {

            if (iv_thumbnail != null) {
                Glide.with(context)
                    .load(list[position])
                    .transform(CenterCrop(), RoundedCorners(45))
//                                        .override(150,150) //만약 리소스 조정이 필요하다면, 캐시낭비를 막는데 효과적임
                    //                .placeholder(R.drawable.loading)
                    //                .error(R.drawable.image_not_found)
                    .into(iv_thumbnail)
            }
        }

    }
}
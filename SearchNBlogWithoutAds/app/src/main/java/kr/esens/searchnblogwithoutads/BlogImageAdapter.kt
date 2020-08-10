package kr.esens.searchnblogwithoutads

import android.content.Context
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.blog_listitem_img, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mData,position, mContext)
//        setAnimation(holder.itemView, position);
    }

    //todo animation은 snap 처리 후 적용
//    private fun setAnimation(viewToAnimate: View, position: Int) {
//        // If the bound view wasn't previously displayed on screen, it's animated
//        if (position > lastPosition) {
//            val animation =
//                AnimationUtils.loadAnimation(mContext, R.anim.item_animation_horizontal)
//            viewToAnimate.startAnimation(animation)
//            lastPosition = position
//        }
//    }

//    override fun onViewDetachedFromWindow(holder: Holder) {
//        holder.clearAnimation()
//    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {


        val iv_thumbnail = itemView?.findViewById<ImageView>(R.id.iv_thumbnail)
//        val tv_index = itemView?.findViewById<TextView>(R.id.tv_index)

        fun bind (list: ArrayList<String>, position: Int,context: Context) {
            if (iv_thumbnail != null) {
//                var requestOptions = RequestOptions()
//                requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
                Glide.with(context)
                    .load(list[position])
                    .transform(CenterCrop(), RoundedCorners(45))
                    //                    .override(150,150) //만약 리소스 조정이 필요하다면, 캐시낭비를 막는데 효과적임
                    //                .placeholder(R.drawable.loading)
                    //                .error(R.drawable.image_not_found)
                    .into(iv_thumbnail)
            }
//            tv_index?.text = "$position"

        }

//        fun clearAnimation() {
//            itemView.clearAnimation()
//        }
    }
}
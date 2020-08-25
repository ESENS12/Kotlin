package kr.esens.searchnblogwithoutads

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper


/*

   TODO 1. 배너 로고가 이미지view에서는 보이지 않는 경우가 있음.

*/

class BlogAdapter(val mContext: Context, val mData: ArrayList<BlogItem>) :
    RecyclerView.Adapter<BlogAdapter.Holder>() {

    interface OnItemClickListener{
        fun onItemClick(v:View, position:Int);
    }

    private var blogAdapter: BlogImageAdapter? = null;
    private var onItemClickListener : OnItemClickListener? = null;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.blog_listitem, parent, false)

        return Holder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.onItemClickListener = listener;
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.bind(mData, position, mContext)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        var recyclerView = itemView.findViewById<RecyclerView>(R.id.recycler_view_parent);
        var tv_title = itemView.findViewById<TextView>(R.id.tv_title)
        var tv_fake = itemView.findViewById<TextView>(R.id.tv_fake);
        var sanpHelper = LinearSnapHelper();
//        val iv_thumbnail = itemView?.findViewById<ImageView>(R.id.iv_thumbnail)
//        val tv_index = itemView?.findViewById<TextView>(R.id.tv_index)

        fun bind (
            list: ArrayList<BlogItem>, position: Int,
            context: Context) {

            blogAdapter = list[position].BlogImages?.let {BlogImageAdapter(context, it)};
            val mLinearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);

            tv_title.setOnClickListener {
                if(adapterPosition != RecyclerView.NO_POSITION){
                    if(onItemClickListener != null){
                        onItemClickListener!!.onItemClick(it,adapterPosition)
                    }
                }
            }

            recyclerView.apply {
                this.adapter = blogAdapter
                this.layoutManager = mLinearLayoutManager
//                this.addItemDecoration(
//                    DividerItemDecoration(
//                        context,
//                        LinearLayoutManager.HORIZONTAL
//                    )
//                )
                this.isNestedScrollingEnabled = true
                this.setHasFixedSize(true)
                this.addItemDecoration(CirclePagerIndicatorDecoration())
            }
            sanpHelper.attachToRecyclerView(recyclerView);

            if(mData[position].bIsFakeBlog){
                tv_fake.visibility = View.VISIBLE
            }else{
                tv_fake.visibility = View.GONE
            }

            tv_title.text = list[position].PostTitle;
            recyclerView?.adapter?.notifyDataSetChanged();
//            if (iv_thumbnail != null) {
//                Glide.with(context)
//                    .load(list[position])
//                    //                    .override(이미지 사이즈) //만약 리소스 조정이 필요하다면, 캐시낭비를 막는데 효과적임
//                    //                .placeholder(R.drawable.loading)
//                    //                .error(R.drawable.image_not_found)
//                    .into(iv_thumbnail)
//            }
//            tv_index?.text = "$position"
        }
    }

}


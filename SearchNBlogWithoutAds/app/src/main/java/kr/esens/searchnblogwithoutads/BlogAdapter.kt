package kr.esens.searchnblogwithoutads

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.esens.searchnblogwithoutads.MainActivity.Companion.TAG

class BlogAdapter(val mContext: Context, val mData: ArrayList<BlogItem>) :

    RecyclerView.Adapter<BlogAdapter.Holder>() {

    private var blogAdapter: BlogImageAdapter? = null;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.blog_listitem, parent, false)

        return Holder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.bind(mData, position, mContext)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recyclerView = itemView.findViewById<RecyclerView>(R.id.recycler_view_parent);
//        val iv_thumbnail = itemView?.findViewById<ImageView>(R.id.iv_thumbnail)
//        val tv_index = itemView?.findViewById<TextView>(R.id.tv_index)

        fun bind (
            list: ArrayList<BlogItem>, position: Int,
            context: Context) {

            blogAdapter = list.get(position).BlogImages?.let {BlogImageAdapter(context, it)};
            val mLinearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
            recyclerView.apply {
                this.adapter = blogAdapter
                this.layoutManager = mLinearLayoutManager
                this.addItemDecoration(
                    DividerItemDecoration(
                        context,
                        LinearLayoutManager.HORIZONTAL
                    )
                )
                this.isNestedScrollingEnabled = true
                this.setHasFixedSize(true)
            }
            recyclerView?.adapter?.notifyDataSetChanged();
            Log.e(TAG,"BlogAdapter Holder.bind called!");
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
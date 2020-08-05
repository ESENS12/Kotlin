package kr.esens.searchnblogwithoutads;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder> {
    private String TAG = "BlogAdapter";
    private ArrayList<String> mData = null ;
    private Context mContext = null;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_thumbnail;
        TextView tv_index;

        ViewHolder(View itemView) {
            super(itemView) ;
            iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail) ;
            tv_index = itemView.findViewById(R.id.tv_index);
        }
    }

    BlogAdapter(ArrayList<String> list) {
        mData = list ;
        Log.e(TAG,"adapter construction!");
    }

    @Override
    public BlogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.blog_listitem, parent, false) ;
        BlogAdapter.ViewHolder vh = new BlogAdapter.ViewHolder(view) ;
        return vh ;
    }

    @Override
    public void onBindViewHolder(BlogAdapter.ViewHolder holder, int position) {

        Glide.with(mContext).load(mData.get(position))
//                    .override(이미지 사이즈) //만약 리소스 조정이 필요하다면, 캐시낭비를 막는데 효과적임
//                .placeholder(R.drawable.loading)
//                .error(R.drawable.image_not_found)
                .into(holder.iv_thumbnail);
        holder.tv_index.setText(position+"");
    }

    @Override
    public int getItemCount() {
        return mData.size() ;
    }
}


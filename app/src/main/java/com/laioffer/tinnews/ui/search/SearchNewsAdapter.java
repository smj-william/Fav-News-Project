package com.laioffer.tinnews.ui.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.laioffer.tinnews.R;
import com.laioffer.tinnews.databinding.SearchNewsItemBinding;
import com.laioffer.tinnews.model.Article;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchNewsAdapter extends RecyclerView.Adapter<SearchNewsAdapter.SearchNewsViewHolder> {

    // 1. Supporting data:

    //显示的东西，某个位置对应某个article，所以用list
    private List<Article> articles = new ArrayList<>();

    public void setArticles(List<Article> newsList) {
        articles.clear(); //每set一次的时候先清空
        articles.addAll(newsList); //放进来news
        notifyDataSetChanged(); //通知有新数据，但是这不是最有效的方法
    }

    interface ItemCallback {
        void onOpenDetails(Article article);
    }

    private ItemCallback itemCallback;

    public void setItemCallback(ItemCallback itemCallback) {
        this.itemCallback = itemCallback;
    }

    @NonNull
    @Override
    public SearchNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //得到一个inflater， 然后inflate

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_news_item, parent, false);
        return new SearchNewsViewHolder(view); //把item view裹在view holder里

    }

    @Override
    public void onBindViewHolder(@NonNull SearchNewsViewHolder holder, int position) {
        //滑动界面的时候， 需要让holder update一下
        Article article = articles.get(position);

        //holder.favoriteImageView.setImageResource(R.drawable.ic_favorite_24dp);

        holder.itemTitleTextView.setText(article.title);

        //image lib 使得不点进去的时候省流
        if(article.urlToImage != null && !article.urlToImage.isEmpty()){
            Picasso.get().load(article.urlToImage).resize(200, 200).into(holder.itemImageView);
            holder.itemView.setOnClickListener(v -> itemCallback.onOpenDetails(article));
        }

    }

    @Override
    public int getItemCount() {
        return articles.size(); //有多少article就有多少个
    }

    // 2. SearchNewsViewHolder:
    public static class SearchNewsViewHolder extends RecyclerView.ViewHolder {

        //为什么需要view holder？
        //需要把要用到的view拿出来， 然后easy to access
        //且不需要每次都search一次再update
        ImageView itemImageView;
        TextView itemTitleTextView;

        public SearchNewsViewHolder(@NonNull View itemView) {
            super(itemView);
            SearchNewsItemBinding binding = SearchNewsItemBinding.bind(itemView);
            itemImageView = binding.searchItemImage;
            itemTitleTextView = binding.searchItemTitle;
        }
    }
}

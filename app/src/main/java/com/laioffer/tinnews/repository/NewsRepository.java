package com.laioffer.tinnews.repository;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.laioffer.tinnews.TinNewsApplication;
import com.laioffer.tinnews.database.TinNewsDatabase;
import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.model.NewsResponse;
import com.laioffer.tinnews.network.NewsAPI;
import com.laioffer.tinnews.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsRepository {
    //这个class就拿data的
    private final NewsAPI newsApi;
    private final TinNewsDatabase database;

    public NewsRepository() {

        newsApi = RetrofitClient.newInstance().create(NewsAPI.class);
        database = TinNewsApplication.getDatabase();

    }

    // 我們是把原本寫在main activity的networking 寫到 newsRepository
    //LiveData 是个data model的容器，我们这儿是NewsResponse的容器，只要NewsResponse有改变，那就直播出去
    public LiveData<NewsResponse> getTopHeadlines(String country) {
        //
        MutableLiveData<NewsResponse> topHeadlinesLiveData = new MutableLiveData<>();

        //networking
        newsApi.getTopHeadlines(country)
                .enqueue(new Callback<NewsResponse>() { //enqueue 立刻return，所以main thread能free出来不会block线程，然后再有call back进去检查
                   @Override
                    public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                        if (response.isSuccessful()) {
                            topHeadlinesLiveData.setValue(response.body());//更新news，就直播出去了
                        } else {
                            topHeadlinesLiveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<NewsResponse> call, Throwable t) {
                        topHeadlinesLiveData.setValue(null);
                    }
                });
        return topHeadlinesLiveData;
    }

    public LiveData<NewsResponse> searchNews(String query) {

        MutableLiveData<NewsResponse> everyThingLiveData = new MutableLiveData<>();
        newsApi.getEverything(query, 40)
                .enqueue(
                        new Callback<NewsResponse>() {
                            @Override
                            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                                if (response.isSuccessful()) {
                                    everyThingLiveData.setValue(response.body());
                                } else {
                                    everyThingLiveData.setValue(null);
                                }
                            }

                            @Override
                            public void onFailure(Call<NewsResponse> call, Throwable t) {
                                everyThingLiveData.setValue(null);
                            }
                        });
        return everyThingLiveData;
    }

    public LiveData<List<Article>> getAllSavedArticles(){
        return database.articleDao().getAllArticles();//在main thread， 但是live data可以把其他thread挪回主线程

    }

    public void deleteSavedArticle(Article article) {
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                database.articleDao().deleteArticle(article);
//            }
//        });

        //上面可以简写成下面：
        AsyncTask.execute(() -> database.articleDao().deleteArticle(article));
    }


    public LiveData<Boolean> favoriteArticle(Article article) {
        //担心出现ANR（app not respond, 所以用多线程，把计算放到background thread）
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();//main thread
        new FavoriteAsyncTask(database, resultLiveData).execute(article); //main --> background thread
        return resultLiveData;
    }

    private static class FavoriteAsyncTask extends AsyncTask<Article, Void, Boolean> {
        //google 写的用background 进行计算的 thread

        private final TinNewsDatabase database;
        private final MutableLiveData<Boolean> liveData;

        private FavoriteAsyncTask(TinNewsDatabase database, MutableLiveData<Boolean> liveData) {
            this.database = database;
            this.liveData = liveData;
        }

        @Override
        protected Boolean doInBackground(Article... articles) { //background thread
            Article article = articles[0];
            try {
                database.articleDao().saveArticle(article);
            } catch (Exception e) { //防止插入相同的新闻，
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {

            liveData.setValue(success);
        }


    }



}

package com.laioffer.tinnews.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.model.NewsResponse;
import com.laioffer.tinnews.repository.NewsRepository;

// 教程：https://developer.android.com/codelabs/kotlin-android-training-view-model#7


//找repo拿data， 然后塞入live data里面
public class HomeViewModel extends ViewModel {
    private final NewsRepository repository;
    private final MutableLiveData<String> countryInput = new MutableLiveData<>();

    public HomeViewModel(NewsRepository newsRepository) {
        this.repository = newsRepository;
    }

    public void setCountryInput(String country) { //ui上只是set了一个country，接下来就交给listener监听livedata是否有变化，
                                                    // 这样
        countryInput.setValue(country);
    }


    public void setFavoriteArticleInput(Article article) {
        repository.favoriteArticle(article);
        //只是存
        //The difference this time is that we don’t need to expose the observing result.
        // So we don’t have to do the Transformations.switchMap trick.
        // This is a plain simple direct call to the repository.
    }

    public LiveData<NewsResponse> getTopHeadlines() {
        return Transformations.switchMap(countryInput, repository::getTopHeadlines);
        //前面input发生变化的时候，后面这个function就会被trigger
    }

}

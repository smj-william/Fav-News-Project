package com.laioffer.tinnews.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.laioffer.tinnews.R;
import com.laioffer.tinnews.databinding.FragmentHomeBinding;
import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.model.NewsResponse;
import com.laioffer.tinnews.repository.NewsRepository;
import com.laioffer.tinnews.repository.NewsViewModelFactory;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.util.List;

//implements listener 是为了track left and right swipe
public class HomeFragment extends Fragment implements CardStackListener {

    private HomeViewModel viewModel;
    private FragmentHomeBinding binding;
    private CardStackLayoutManager layoutManager;
    private List<Article> articles;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();//这样就不用find view by id了
        //return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup CardStackView
        CardSwipeAdapter swipeAdapter = new CardSwipeAdapter();

        //layoutManager = new CardStackLayoutManager(requireContext()); implements listener 后改成下面
        layoutManager = new CardStackLayoutManager(requireContext(), this);

        layoutManager.setStackFrom(StackFrom.Top);
        binding.homeCardStackView.setLayoutManager(layoutManager);
        binding.homeCardStackView.setAdapter(swipeAdapter);

        //link like and unlike button
        binding.homeLikeButton.setOnClickListener(v -> swipeCard(Direction.Right));
        binding.homeUnlikeButton.setOnClickListener(v -> swipeCard(Direction.Left));

        NewsRepository repository = new NewsRepository();

        //viewModel = new HomeViewModel(repository); 这样只是创建了个单独的viewModel，没有store owner。一个viewModel只想和一个fragment相关联
        //有了factory之后，就要用下面这个方法
        //这里也要用provider配合factory来建立view model，这样屏幕旋转的时候，不会丢失本身fragment之前保存的data，
        // 因为android本身不知道我们用的是那个constructor， 所以用factory建造就不会丢失
        //为了保存旋转前老viewmodel里面的东西，这也称作数据后移。
        //作为HomeFragment这个view来说，只是显示而已，不管数据哪儿来的，所以把逻辑放到viewModel里处理好
        //factory为了保证viewModel正确的产生，使得旋转后也保证viewModel保存之前的数据
        viewModel = new ViewModelProvider(this, new NewsViewModelFactory(repository)).get(HomeViewModel.class);

        viewModel.setCountryInput("us");
        //下列支持中文
        //ViewModel.setCountryInput(Locale.getDefault().getCountry());

        //update view
        //等效于LiveData<NewsResponse> data = viewModel.getTopHeadlines();
        // data.observe(.....)
        viewModel
                .getTopHeadlines()
                .observe(
                        getViewLifecycleOwner(), //告诉现在什么状态，为了只让observer发生在view created之后，onStop之前
                        newsResponse -> { //observer
                            if (newsResponse != null) {
                                Log.d("HomeFragment", newsResponse.toString());
                                articles = newsResponse.articles;
                                swipeAdapter.setArticles(articles);
                            }
                        });

    }

    //public API, set swip animation
    private void swipeCard(Direction direction) {
        SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                .setDirection(direction) //建立setting，建立direction
                .setDuration(Duration.Normal.duration)
                .build();
        layoutManager.setSwipeAnimationSetting(setting);
        binding.homeCardStackView.swipe();
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {
        if (direction == Direction.Left) {
            Log.d("CardStackView", "Unliked " + layoutManager.getTopPosition());
        } else if (direction == Direction.Right) {
            Log.d("CardStackView", "Liked "  + layoutManager.getTopPosition());

            //
            Article article = articles.get(layoutManager.getTopPosition() - 1);
            viewModel.setFavoriteArticleInput(article);
        }
    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {

    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }
}

//fragment -> set country (fragment end) --> viewModel --> ask repo（model） to get headline --> update livedata
//fragment -> observe -> viewModel拿回来的live data
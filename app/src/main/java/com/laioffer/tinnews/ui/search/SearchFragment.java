package com.laioffer.tinnews.ui.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.laioffer.tinnews.R;
import com.laioffer.tinnews.databinding.FragmentSearchBinding;
import com.laioffer.tinnews.repository.NewsRepository;
import com.laioffer.tinnews.repository.NewsViewModelFactory;


public class SearchFragment extends Fragment {

    private SearchViewModel viewModel;
    private FragmentSearchBinding binding;


    public SearchFragment() {
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

        //这就是把mainActivity里面点击search bottom 菜单的时候设成search页面
        //return inflater.inflate(R.layout.fragment_search, container, false);

        //现在用binding，帮我们简化了find view by Id这个过程
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SearchNewsAdapter searchNewsAdapter = new SearchNewsAdapter();//创建adapter
        searchNewsAdapter.setItemCallback(article -> {
            SearchFragmentDirections.ActionNavigationSearchToNavigationDetails direction = SearchFragmentDirections.actionNavigationSearchToNavigationDetails(article);
            NavHostFragment.findNavController(SearchFragment.this).navigate(direction);
        });


        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);//创建grid layout布局
        binding.newsResultsRecyclerView.setLayoutManager(gridLayoutManager);//把layout manager 给recycler view
        binding.newsResultsRecyclerView.setAdapter(searchNewsAdapter); // 要adaper给数据给recycler view


        //input
        binding.newsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    viewModel.setSearchInput(query);
                }
                binding.newsSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        NewsRepository repository = new NewsRepository();

        //viewModel = new SearchViewModel(repository);
        //和HomeFragment那边一样，这里也要用provider配合factory来建立view model，这样屏幕旋转的时候，不会丢失本身fragment之前保存的data，
        // 因为android本身不知道我们用的是那个constructor
        viewModel = new ViewModelProvider(this, new NewsViewModelFactory(repository)).get(SearchViewModel.class);

        //viewModel.setSearchInput("Covid-19"); test

        //observe output
        viewModel
                .searchNews()
                .observe(
                        getViewLifecycleOwner(),
                        newsResponse -> {
                            if (newsResponse != null) {
                                Log.d("SearchFragment", newsResponse.toString());
                                //数据回来的时候，把数据给adapter
                                searchNewsAdapter.setArticles(newsResponse.articles);

                            }
                        });

    }
}
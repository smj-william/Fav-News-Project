package com.laioffer.tinnews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.laioffer.tinnews.databinding.ActivityMainBinding;
import com.laioffer.tinnews.model.NewsResponse;
import com.laioffer.tinnews.network.NewsAPI;
import com.laioffer.tinnews.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    //Activity：
    //1，提供entry point， 和screen
    //2，提供了decker view hireachrry
    //3，fragment 是个sub activity，可以maintain 主tree下面subtree


    private NavController navController; //用来control nav去哪儿

    // nav 教程： 教程：https://developer.android.com/codelabs/kotlin-android-training-view-model#7
    // mvm教程https://developer.android.com/jetpack/guide
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);//拿到navigation那个view

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();//为了做navigation

        NavigationUI.setupWithNavController(navView, navController);

        //最后取消顶头action bar
        //NavigationUI.setupActionBarWithNavController(this, navController);//action bar就是最上面的，每次选择最下面不同nav到的页面，最上面名字会变


         //networking
//        NewsAPI api = RetrofitClient.newInstance().create(NewsAPI.class);
//
//        api.getTopHeadlines("US").enqueue(new Callback<NewsResponse>() {
//        @Override
//        public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
//            //只要拿到response就是成功，就算拿到的是个error
//            if (response.isSuccessful()) {
//                Log.d("getTopHeadlines", response.body().toString());
//            } else {
//                Log.d("getTopHeadlines", response.toString());
//            }
//        }
//
//        @Override
//        public void onFailure(Call<NewsResponse> call, Throwable t) {
//            Log.d("getTopHeadlines", t.toString());
//        }
//    });
    }

    @Override
    public boolean onSupportNavigateUp() {

        return navController.navigateUp(); //就是退回的箭头
    }


}
package net.simplifiedlearning.simplifiedcoding.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.simplifiedlearning.simplifiedcoding.Adapters.ImagesAdapter;
import net.simplifiedlearning.simplifiedcoding.Models.Image;
import net.simplifiedlearning.simplifiedcoding.Models.User;
import net.simplifiedlearning.simplifiedcoding.R;
import net.simplifiedlearning.simplifiedcoding.Utils.SharedPrefManager;
import net.simplifiedlearning.simplifiedcoding.Webservices.ApiInterface;
import net.simplifiedlearning.simplifiedcoding.Webservices.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static android.content.ContentValues.TAG;

/**
 * Created by truongnm on 3/31/18.
 */

public class FragmentEgg extends Fragment {
    private List<Image> images = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImagesAdapter imagesAdapter;
    private SwipeRefreshLayout swipeContainer;
    private User user;

    public FragmentEgg() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_egg, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final FragmentActivity fragmentActivity = getActivity();
        swipeContainer = getView().findViewById(R.id.swipe_refresh);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        recyclerView = getView().findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragmentActivity);
        recyclerView.setLayoutManager(linearLayoutManager);
        imagesAdapter = new ImagesAdapter(fragmentActivity, images);
        recyclerView.setAdapter(imagesAdapter);

        user = SharedPrefManager.getInstance(fragmentActivity).getUser();

        getData();
    }

    private void getData(){
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<List<Image>> call = apiInterface.getImages(user.getId());
        call.enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, retrofit2.Response<List<Image>> response) {
                if(response.isSuccessful()){
                    images.clear();
                    images.addAll(response.body());
                    Log.e(TAG, images.toString());
                    imagesAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, response.message());
                }
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Image>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                swipeContainer.setRefreshing(false);
            }
        });
    }

}
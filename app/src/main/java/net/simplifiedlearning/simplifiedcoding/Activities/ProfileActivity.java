package net.simplifiedlearning.simplifiedcoding.Activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import net.simplifiedlearning.simplifiedcoding.Adapters.GridViewAdapter;
import net.simplifiedlearning.simplifiedcoding.Adapters.ImagesAdapter;
import net.simplifiedlearning.simplifiedcoding.Models.Image;
import net.simplifiedlearning.simplifiedcoding.R;
import net.simplifiedlearning.simplifiedcoding.Utils.SharedPrefManager;
import net.simplifiedlearning.simplifiedcoding.Models.User;
import net.simplifiedlearning.simplifiedcoding.Webservices.ApiInterface;
import net.simplifiedlearning.simplifiedcoding.Webservices.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    TextView textViewId, textViewUsername, textViewEmail, textViewGender;
    private User user;
    private List<Image> images = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImagesAdapter imagesAdapter;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        textViewId = (TextView) findViewById(R.id.textViewId);
        textViewUsername = (TextView) findViewById(R.id.textViewUsername);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        textViewGender = (TextView) findViewById(R.id.textViewGender);

        //getting the current user
        user = SharedPrefManager.getInstance(this).getUser();

        //setting the values to the textviews
        textViewId.setText(String.valueOf(user.getId()));
        textViewUsername.setText(user.getUsername());
        textViewEmail.setText(user.getEmail());
        textViewGender.setText(user.getGender());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(ProfileActivity.this, UploadImagesActivity.class));
            }
        });

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
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

        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProfileActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        imagesAdapter = new ImagesAdapter(ProfileActivity.this, images);
        recyclerView.setAdapter(imagesAdapter);

        getData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_btn:
                // app icon in action bar clicked; go home
                finish();
                SharedPrefManager.getInstance(getApplicationContext()).logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

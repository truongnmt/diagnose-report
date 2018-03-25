package net.simplifiedlearning.simplifiedcoding.Activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        //when the user presses logout button
        //calling the logout method
        findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            finish();
            SharedPrefManager.getInstance(getApplicationContext()).logout();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(ProfileActivity.this, UploadImagesActivity.class));
            }
        });

        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProfileActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        imagesAdapter = new ImagesAdapter(ProfileActivity.this, images);
        recyclerView.setAdapter(imagesAdapter);

        getData();
    }

    private void getData(){
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<List<Image>> call = apiInterface.getImages(user.getId());
        call.enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, retrofit2.Response<List<Image>> response) {
                if(response.isSuccessful()){
                    images.addAll(response.body());
                    imagesAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Image>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }
}

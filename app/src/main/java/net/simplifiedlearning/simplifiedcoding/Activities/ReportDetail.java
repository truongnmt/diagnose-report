package net.simplifiedlearning.simplifiedcoding.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import net.simplifiedlearning.simplifiedcoding.Adapters.ImagesAdapter;
import net.simplifiedlearning.simplifiedcoding.Models.Image;
import net.simplifiedlearning.simplifiedcoding.R;
import net.simplifiedlearning.simplifiedcoding.Utils.SharedPrefManager;
import net.simplifiedlearning.simplifiedcoding.Webservices.ApiInterface;
import net.simplifiedlearning.simplifiedcoding.Webservices.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.ContentValues.TAG;

public class ReportDetail extends AppCompatActivity {
    private List<Image> images = new ArrayList<>();
    private ImagesAdapter imagesAdapter;
    private int reportId;
    private String name;
    private String description;
    private String patient_name;
    private int patient_age;
    private int patient_height;
    private int patient_weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int id = extras.getInt("id");
            reportId = id;

            name = extras.getString("name");
            ((TextView) findViewById(R.id.show_report_name)).setText(name);

            description = extras.getString("description");
            ((TextView) findViewById(R.id.show_report_description)).setText(description);

            float general_result = extras.getFloat("general_result");
            if (Float.compare(general_result,-1) == 0) {
                TextView generalResult = findViewById(R.id.show_report_general_result);
                generalResult.setTextColor(ContextCompat.getColor(this, R.color.warning));
                generalResult.setText(String.valueOf(general_result + "%"));
                generalResult.setText("Unknown");
            }
            else {
                TextView generalResult = findViewById(R.id.show_report_general_result);
                if (Float.compare(general_result, 70) == 1)
                    generalResult.setTextColor(ContextCompat.getColor(this, R.color.safe));
                else
                    generalResult.setTextColor(ContextCompat.getColor(this, R.color.danger));
                generalResult.setText(String.valueOf(general_result + "%"));
            }

            String created_at = extras.getString("created_at");
            ((TextView) findViewById(R.id.show_report_created_at)).setText(created_at);

            patient_name = extras.getString("patient_name");
            ((TextView) findViewById(R.id.show_patient_name)).setText(patient_name);

            patient_age = extras.getInt("patient_age");
            ((TextView) findViewById(R.id.show_patient_age)).setText(String.valueOf(patient_age));

            patient_height = extras.getInt("patient_height");
            ((TextView) findViewById(R.id.show_patient_height)).setText(String.valueOf(patient_height));

            patient_weight = extras.getInt("patient_weight");
            ((TextView) findViewById(R.id.show_patient_weight)).setText(String.valueOf(patient_weight));

            getImagesData(id);
        }

        RecyclerView recyclerView = findViewById(R.id.show_report_images);
        final int spanCount = 4;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount);

        recyclerView.setLayoutManager(gridLayoutManager);
        imagesAdapter = new ImagesAdapter(this, images);
        recyclerView.setAdapter(imagesAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report_detail, menu);
        Drawable drawable = menu.findItem(R.id.edit_report_btn).getIcon();

        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.white));
        menu.findItem(R.id.edit_report_btn).setIcon(drawable);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            case R.id.edit_report_btn:
                Intent intent = new Intent(this, ReportEdit.class);
                intent.putExtra("report_id", this.reportId);
                intent.putExtra("name", name);
                intent.putExtra("description", description);
                intent.putExtra("patient_name", patient_name);
                intent.putExtra("patient_age", patient_age);
                intent.putExtra("patient_height", patient_height);
                intent.putExtra("patient_weight", patient_weight);
                startActivityForResult(intent, 1);
                return true;
            case R.id.delete_report_btn:
                finish();
                deleteReport();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getImagesData(int report_id){
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<List<Image>> call = apiInterface.getImages(report_id);
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
            }

            @Override
            public void onFailure(Call<List<Image>> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private void deleteReport() {
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("report_id", String.valueOf(this.reportId));
        builder.addFormDataPart("apicall", "delete");
        MultipartBody requestBody = builder.build();
        Call<ResponseBody> call = apiInterface.deleteReport(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Toast.makeText(ReportDetail.this, "Report deleted successfully", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, response.body().toString());
                } else {
                    Log.e(TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ReportDetail.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, t.getMessage());
            }
        });
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch(requestCode) {
//            case (1) : {
//                if (resultCode == Activity.RESULT_OK) {
//
//                }
//                break;
//            }
//        }
//    }
}

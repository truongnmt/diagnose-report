package net.simplifiedlearning.simplifiedcoding.Activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.simplifiedlearning.simplifiedcoding.R;
import net.simplifiedlearning.simplifiedcoding.Webservices.ApiInterface;
import net.simplifiedlearning.simplifiedcoding.Webservices.ServiceGenerator;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.ContentValues.TAG;

public class ReportEdit extends AppCompatActivity {
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
        setContentView(R.layout.activity_report_edit);
        Toolbar toolbar = findViewById(R.id.edit_report_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int id = extras.getInt("report_id");
            reportId = id;

            name = extras.getString("name");
            ((TextView) findViewById(R.id.edit_report_name)).setText(name);

            description = extras.getString("description");
            ((TextView) findViewById(R.id.edit_report_description)).setText(description);

            patient_name = extras.getString("patient_name");
            ((TextView) findViewById(R.id.edit_report_patient_name)).setText(patient_name);

            patient_age = extras.getInt("patient_age");
            ((TextView) findViewById(R.id.edit_report_patient_age)).setText(String.valueOf(patient_age));

            patient_height = extras.getInt("patient_height");
            ((TextView) findViewById(R.id.edit_report_patient_height)).setText(String.valueOf(patient_height));

            patient_weight = extras.getInt("patient_weight");
            ((TextView) findViewById(R.id.edit_report_patient_weight)).setText(String.valueOf(patient_weight));
        }

        String arr[]={
                "Type of report",
                "Report type 1",
                "Report type 2",
                "Report type 3"};
        Spinner spin = findViewById(R.id.spinner3);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, arr
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_list_item_single_choice
        );
        spin.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            case R.id.edit_report_save:
                // app icon in action bar clicked; go home
                updateReport();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("result", 1);
                setResult(ReportEdit.RESULT_OK, resultIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateReport() {
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("report_id", String.valueOf(this.reportId));
        builder.addFormDataPart("name", ((TextView) findViewById(R.id.edit_report_name)).getText().toString());
        builder.addFormDataPart("description", ((TextView) findViewById(R.id.edit_report_description)).getText().toString());
        builder.addFormDataPart("patient_name", ((TextView) findViewById(R.id.edit_report_patient_name)).getText().toString());
        builder.addFormDataPart("patient_age", ((TextView) findViewById(R.id.edit_report_patient_age)).getText().toString());
        builder.addFormDataPart("patient_height", ((TextView) findViewById(R.id.edit_report_patient_height)).getText().toString());
        builder.addFormDataPart("patient_weight", ((TextView) findViewById(R.id.edit_report_patient_weight)).getText().toString());
        builder.addFormDataPart("apicall", "patch");
        MultipartBody requestBody = builder.build();

        Call<ResponseBody> call = apiInterface.updateReport(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Toast.makeText(ReportEdit.this, "Report updated successfully", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, response.body().toString());
                } else {
                    Log.e(TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ReportEdit.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, t.getMessage());
            }
        });
    }
}

package net.simplifiedlearning.simplifiedcoding.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import net.simplifiedlearning.simplifiedcoding.Adapters.GridViewAdapter;
import net.simplifiedlearning.simplifiedcoding.Models.ImagePreviewItem;
import net.simplifiedlearning.simplifiedcoding.R;
import net.simplifiedlearning.simplifiedcoding.Utils.SharedPrefManager;
import net.simplifiedlearning.simplifiedcoding.Models.User;
import net.simplifiedlearning.simplifiedcoding.Webservices.ApiInterface;
import net.simplifiedlearning.simplifiedcoding.Webservices.ServiceGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import android.view.MenuItem;

import com.example.ExpandableHeightGridView;

public class UploadReportActivity extends AppCompatActivity {
    public final static int PICK_IMAGE_REQUEST = 1;
    public final static int STORAGE_PERMISSION_CODE = 123;
    private ProgressDialog mProgressDialog;
    private List<Uri> mUris = new ArrayList<>();
    private User user;
    private ExpandableHeightGridView gridView;
    private ClipData chosenIntentData;
    private Uri chosenDataUri;
    private EditText reportName, reportDescription, reportPatientName, reportPatientHeight, reportPatientWeight, reportPatientAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_report);
        requestStoragePermission();
        user = SharedPrefManager.getInstance(this).getUser();

        gridView =  findViewById(R.id.uploadPreviewImage);
        gridView.setExpanded(true);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reportName = findViewById(R.id.report_name);
        reportDescription = findViewById(R.id.report_description);
        reportPatientName = findViewById(R.id.report_patient_name);
        reportPatientAge = findViewById(R.id.report_patient_age);
        reportPatientHeight = findViewById(R.id.report_patient_height);
        reportPatientWeight = findViewById(R.id.report_patient_weight);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_upload, menu);
//        return true;
//    }

    public void onClickBtn(View view)
    {
        switch (view.getId()) {
            case R.id.button_select_image:
                requestPermissionAndPickImage();
                break;
            case R.id.upload_report_btn:
                uploadFiles();
                break;
            default:
                break;
        }
    }

    private void requestPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            pickImage();
            return;
        }

        int result = ContextCompat.checkSelfPermission(this,
                READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            pickImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Files to Upload"),
                PICK_IMAGE_REQUEST);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null &&
                data.getClipData() != null) {
            chosenDataUri = null;
            chosenIntentData = data.getClipData();

            final ArrayList<ImagePreviewItem> imagePreviewItems = new ArrayList<>();
            ClipData clipData = data.getClipData();
            for(int i = 0; i < clipData.getItemCount(); i++){
                ClipData.Item item = clipData.getItemAt(i);
                Uri uri = item.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    imagePreviewItems.add(new ImagePreviewItem(bitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            GridViewAdapter gridAdapter = new GridViewAdapter(this, R.layout.preview_image_item_layout, imagePreviewItems);
            gridView.setAdapter(gridAdapter);
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null &&
                data.getData() != null) {
            chosenIntentData = null;
            chosenDataUri = data.getData();

            final ArrayList<ImagePreviewItem> imagePreviewItems = new ArrayList<>();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), chosenDataUri);
                imagePreviewItems.add(new ImagePreviewItem(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }

            GridViewAdapter gridAdapter = new GridViewAdapter(this, R.layout.preview_image_item_layout, imagePreviewItems);
            gridView.setAdapter(gridAdapter);
        }
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            return "";
        }
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            return "";
        }
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    public void uploadFiles() {
        String name = reportName.getText().toString();
        String description = reportDescription.getText().toString();
        String patientName = reportPatientName.getText().toString();
        String patientAge = reportPatientAge.getText().toString();
        String patientHeight = reportPatientHeight.getText().toString();
        String patientWeight = reportPatientWeight.getText().toString();
        if (name.isEmpty() || description.isEmpty() || patientName.isEmpty() || patientAge.isEmpty() ||
                patientHeight.isEmpty() || patientWeight.isEmpty()){
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder sb = new StringBuilder();
        if (chosenIntentData != null) {
            for (int i = 0; i < chosenIntentData.getItemCount(); i++) {
                ClipData.Item item = chosenIntentData.getItemAt(i);
                Uri uri = item.getUri();
                mUris.add(uri);
                sb.append(i + "-")
                        .append(getRealPathFromURI(uri))
                        .append("\n");
            }
        } else if (chosenDataUri != null) {
            mUris.add(chosenDataUri);
        } else {
            Toast.makeText(this, "Please select some image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mUris.isEmpty()) {
            Toast.makeText(this, "Please select some image", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.w("Picked files:", sb.toString());
        showProgress();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (int i = 0; i < mUris.size(); i++) {
            Uri uri = mUris.get(i);
            File file = new File(getRealPathFromURI(uri));
            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("image/*"),
                    file);
            builder.addFormDataPart("images[]", file.getName(), requestBody);
        }
        builder.addFormDataPart("user_id", String.valueOf(user.getId()));
        builder.addFormDataPart("name", name);
        builder.addFormDataPart("description", description);
        builder.addFormDataPart("patient_name", patientName);
        builder.addFormDataPart("patient_age", patientAge);
        builder.addFormDataPart("patient_height", patientHeight);
        builder.addFormDataPart("patient_weight", patientWeight);

        MultipartBody requestBody = builder.build();
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.uploadFileMultiPart(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response == null || response.body() == null) {
                    dismissDialog();
                    Toast.makeText(getApplicationContext(), "Upload images failed!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(UploadReportActivity.this, "Uploaded successfully!", Toast.LENGTH_SHORT).show();
                dismissDialog();
                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Upload images failed!", Toast.LENGTH_SHORT).show();
                dismissDialog();
            }
        });
        mUris = new ArrayList<>();
    }

    private void dismissDialog() {
        mProgressDialog.dismiss();
    }

    private void showProgress() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Uploading...");
        }
        mProgressDialog.show();
    }

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
//        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }
}

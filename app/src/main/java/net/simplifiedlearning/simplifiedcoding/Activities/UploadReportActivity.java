package net.simplifiedlearning.simplifiedcoding.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import net.simplifiedlearning.simplifiedcoding.Adapters.GridViewAdapter;
import net.simplifiedlearning.simplifiedcoding.Adapters.RecyclerListAdapter;
import net.simplifiedlearning.simplifiedcoding.Models.ImagePreviewItem;
import net.simplifiedlearning.simplifiedcoding.R;
import net.simplifiedlearning.simplifiedcoding.Utils.SharedPrefManager;
import net.simplifiedlearning.simplifiedcoding.Models.User;
import net.simplifiedlearning.simplifiedcoding.Webservices.ApiInterface;
import net.simplifiedlearning.simplifiedcoding.Webservices.ServiceGenerator;
import net.simplifiedlearning.simplifiedcoding.helper.OnStartDragListener;
import net.simplifiedlearning.simplifiedcoding.helper.SimpleItemTouchHelperCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

public class UploadReportActivity extends AppCompatActivity implements OnStartDragListener {
    public final static int PICK_IMAGE_REQUEST = 1;
    public final static int STORAGE_PERMISSION_CODE = 123;
    private ProgressDialog mProgressDialog;
    private List<Uri> mUris = new ArrayList<>();
    private User user;
    private ClipData chosenIntentData;
    private Uri chosenDataUri;
    private EditText reportName, reportDescription, reportPatientName, reportPatientHeight, reportPatientWeight, reportPatientAge;
    private ItemTouchHelper mItemTouchHelper;
    private RecyclerListAdapter adapter;
    private ArrayList<ImagePreviewItem> imagePreviewItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_report);
        requestStoragePermission();
        user = SharedPrefManager.getInstance(this).getUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reportName = findViewById(R.id.report_name);
        reportDescription = findViewById(R.id.report_description);
        reportPatientName = findViewById(R.id.report_patient_name);
        reportPatientAge = findViewById(R.id.report_patient_age);
        reportPatientHeight = findViewById(R.id.report_patient_height);
        reportPatientWeight = findViewById(R.id.report_patient_weight);

        adapter = new RecyclerListAdapter(this, this, imagePreviewItems);
        RecyclerView recyclerView = findViewById(R.id.uploadPreviewImage);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        final int spanCount = 3;
        final GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        String arr[]={
                "Type of report",
                "Report type 1",
                "Report type 2",
                "Report type 3"};
        Spinner spin = findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, arr
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_list_item_single_choice
        );
        spin.setAdapter(adapter);
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

    public void onClickBtn(View view) throws FileNotFoundException {
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

            imagePreviewItems.clear();
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
            adapter.notifyDataSetChanged();

//            GridViewAdapter gridAdapter = new GridViewAdapter(this, R.layout.preview_image_item_layout, imagePreviewItems);
//            gridView.setAdapter(gridAdapter);
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null &&
                data.getData() != null) {
            chosenIntentData = null;
            chosenDataUri = data.getData();

            imagePreviewItems.clear();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), chosenDataUri);
                imagePreviewItems.add(new ImagePreviewItem(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();

//            GridViewAdapter gridAdapter = new GridViewAdapter(this, R.layout.preview_image_item_layout, imagePreviewItems);
//            gridView.setAdapter(gridAdapter);
        }
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public void uploadFiles() throws FileNotFoundException {
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
                        .append(getPath(getApplicationContext(), uri))
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

            File file = new File(getPath(getApplicationContext(), uri));
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

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}

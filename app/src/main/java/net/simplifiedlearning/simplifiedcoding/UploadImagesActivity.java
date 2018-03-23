package net.simplifiedlearning.simplifiedcoding;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static net.simplifiedlearning.simplifiedcoding.URLs.URL_UPLOAD;

public class UploadImagesActivity extends AppCompatActivity {
    public final static int PICK_IMAGE_REQUEST = 1;
    public final static int STORAGE_PERMISSION_CODE = 123;
    private static final String TAG = "";
    private ProgressDialog mProgressDialog;
    private TextView mTextResult;
    private TextView mTextInput;
    private List<Uri> mUris = new ArrayList<>();
    private User user;
    private GridView gridView;
    private GridViewAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_images);
        requestStoragePermission();

        mTextResult = (TextView) findViewById(R.id.text_result);
        mTextInput = (TextView) findViewById(R.id.text_input);
        user = SharedPrefManager.getInstance(this).getUser();

        gridView = (GridView) findViewById(R.id.uploadPreviewImage);
    }

    public void onClickBtn(View view)
    {
        switch (view.getId()) {
            case R.id.button_select_image:
                requestPermionAndPickImage();
                break;
            default:
                break;
        }
    }

    private void requestPermionAndPickImage() {
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

            gridAdapter = new GridViewAdapter(this, R.layout.preview_image_item_layout, imagePreviewItems);
            gridView.setAdapter(gridAdapter);


//            gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getData());
//            gridView.setAdapter(gridAdapter);

//            GridView gridview = (GridView) findViewById(R.id.uploadPreviewImage);
//            gridview.setAdapter(new GridViewAdapter(this, data.getClipData()));
//
//            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                public void onItemClick(AdapterView<?> parent, View v,
//                                        int position, long id) {
//                    Toast.makeText(HelloGridView.this, "" + position,
//                            Toast.LENGTH_SHORT).show();
//                }
//            });

//            mUris = new ArrayList<>();
//            ClipData clipData = data.getClipData();
//            StringBuilder builder = new StringBuilder();
//            for (int i = 0; i < clipData.getItemCount(); i++) {
//                ClipData.Item item = clipData.getItemAt(i);
//                Uri uri = item.getUri();
//                mUris.add(uri);
//                builder.append(i + "-")
//                        .append(getRealPathFromURI(uri))
//                        .append("\n");
//            }
//            mTextInput.setText(builder.toString());
//            uploadFiles();
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
        if (mUris.isEmpty()) {
            Toast.makeText(this, "Please select some image", Toast.LENGTH_SHORT).show();
            return;
        }
        showProgress();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_UPLOAD)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();

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
        builder.addFormDataPart("name", "nha nghi");
        builder.addFormDataPart("user_id", String.valueOf(user.getId()));

        MultipartBody requestBody = builder.build();
        UploadService service = retrofit.create(UploadService.class);
        Call<ResponseBody> call = service.uploadFileMultilPart(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response == null || response.body() == null) {
                    mTextResult.setText("Upload images failed!");
                    dismissDialog();
                    return;
                }
                try {
                    String responseUrl = response.body().string();
                    mTextResult.setText(responseUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dismissDialog();
                Toast.makeText(getApplicationContext(), String.valueOf(response.body()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mTextResult.setText("Upload images failed!");
                dismissDialog();
            }
        });
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

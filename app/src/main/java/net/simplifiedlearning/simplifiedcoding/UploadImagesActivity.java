package net.simplifiedlearning.simplifiedcoding;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
    public final static int READ_EXTERNAL_REQUEST = 2;
    private ProgressDialog mProgressDialog;
    private TextView mTextResult;
    private TextView mTextInput;
    private List<Uri> mUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_images);
        mTextResult = (TextView) findViewById(R.id.text_result);
        mTextInput = (TextView) findViewById(R.id.text_input);
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
//        } else {
//            requestPermissions(new String[]{
//                    READ_EXTERNAL_STORAGE}, READ_EXTERNAL_REQUEST);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        if (requestCode != READ_EXTERNAL_REQUEST) return;
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImage();
        } else {
            Toast.makeText(getApplicationContext(), "Permission denied.",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void pickImage() {
        // Gọi intent của hệ thống để chọn ảnh nhé.
        Intent intent = new Intent();
        intent.setType("image/*");
        // Thêm dòng này để có thể select nhiều ảnh trong 1 lần nhé các bạn
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
            ClipData clipData = data.getClipData();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);
                Uri uri = item.getUri();
                mUris.add(uri);
                builder.append(i + "-")
                        .append(getRealPathFromURI(uri))
                        .append("\n");

            }

            mTextInput.setText(builder.toString());
            // Sau khi get đc data thì ta upload thôi
            uploadFiles();
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public void uploadFiles() {
        if (mUris.isEmpty()) {
            Toast.makeText(this, "Please select some image", Toast.LENGTH_SHORT).show();
            return;
        }
        // Hàm call api sẽ mất 1 thời gian nên mình show 1 dialog nhé.
        showProgress();
        // Trong retrofit 2 để upload file ta sử dụng Multipart, khai báo 1 MultipartBody.Builder
        // uploaded_file là key mà mình đã định nghĩa trong khi khởi tạo server

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
            // Khởi tạo RequestBody từ những file đã được chọn
            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("image/*"),
                    file);
            // Add thêm request body vào trong builder
            builder.addFormDataPart("uploaded_file", file.getName(), requestBody);
        }

        MultipartBody requestBody = builder.build();
        UploadService service = retrofit.create(UploadService.class);
        Call<ResponseBody> call = service.uploadFileMultilPart(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response == null || response.body() == null) {
                    mTextResult.setText("Upload images failed!");
                    return;
                }
                try {
                    String responseUrl = response.body().string();
                    mTextResult.setText(responseUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dissmissDialog();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mTextResult.setText("Upload images failed!");
                dissmissDialog();
            }


        });
    }

    private void dissmissDialog() {
        mProgressDialog.dismiss();
    }

    private void showProgress() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Uploading...");
        }
        mProgressDialog.show();
    }
}

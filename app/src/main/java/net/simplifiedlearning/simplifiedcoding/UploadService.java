package net.simplifiedlearning.simplifiedcoding;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by truongnmt on 20/03/2018.
 */

public interface UploadService {
    @POST("/")
    Call<ResponseBody> uploadFileMultilPart(@Body RequestBody files);
}
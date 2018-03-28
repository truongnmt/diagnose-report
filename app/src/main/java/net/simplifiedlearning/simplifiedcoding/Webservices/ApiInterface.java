package net.simplifiedlearning.simplifiedcoding.Webservices;

import net.simplifiedlearning.simplifiedcoding.Models.Image;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by nhictt on 20/03/2018.
 */

public interface ApiInterface {
    @POST("/diagnose-report/upload.php")
    Call<ResponseBody> uploadFileMultiPart(@Body RequestBody files);

    @GET("/diagnose-report/images.php")
    Call<List<Image>> getImages(
            @Query("user_id") int user_id
    );
}
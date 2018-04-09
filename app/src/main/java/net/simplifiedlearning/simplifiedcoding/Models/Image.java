package net.simplifiedlearning.simplifiedcoding.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by truongnm on 3/22/18.
 */

public class Image {
    @SerializedName("id")
    private int id;
    @SerializedName("report_id")
    private int report_id;
    @SerializedName("url")
    private String url;
    @SerializedName("result")
    private int result;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReportID() {
        return report_id;
    }

    public void setReportID(int report_id) {
        this.report_id = report_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}

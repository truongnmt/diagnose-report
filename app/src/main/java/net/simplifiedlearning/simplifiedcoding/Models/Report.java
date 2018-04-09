package net.simplifiedlearning.simplifiedcoding.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by truongnm on 3/22/18.
 */

public class Report {
    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int user_id;
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("patient_name")
    private String patient_name;
    @SerializedName("patient_age")
    private int patient_age;
    @SerializedName("patient_height")
    private int patient_height;
    @SerializedName("patient_weight")
    private int patient_weight;
    @SerializedName("general_result")
    private float general_result;
    @SerializedName("created_at")
    private String created_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public int getPatient_age() {
        return patient_age;
    }

    public void setPatient_age(int patient_age) {
        this.patient_age = patient_age;
    }

    public int getPatient_height() {
        return patient_height;
    }

    public void setPatient_height(int patient_height) {
        this.patient_height = patient_height;
    }

    public int getPatient_weight() {
        return patient_weight;
    }

    public void setPatient_weight(int patient_weight) {
        this.patient_weight = patient_weight;
    }

    public float getGeneral_result() {
        return general_result;
    }

    public void setGeneral_result(float general_result) {
        this.general_result = general_result;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}

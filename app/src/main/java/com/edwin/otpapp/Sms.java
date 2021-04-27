package com.edwin.otpapp;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Sms {

    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("otp")
    @Expose
    private String otp;

    /**
     * No args constructor for use in serialization
     *
     */
    public Sms() {
    }

    /**
     *
     * @param phone
     * @param message
     * @param status
     */
    public Sms(String phone, String message, String status, String otp) {
        super();
        this.phone = phone;
        this.message = message;
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
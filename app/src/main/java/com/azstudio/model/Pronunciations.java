
package com.azstudio.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pronunciations {

    @SerializedName("uk")
    @Expose
    private String uk;
    @SerializedName("us")
    @Expose
    private String us;

    public String getUk() {
        return uk;
    }

    public void setUk(String uk) {
        this.uk = uk;
    }

    public String getUs() {
        return us;
    }

    public void setUs(String us) {
        this.us = us;
    }

}

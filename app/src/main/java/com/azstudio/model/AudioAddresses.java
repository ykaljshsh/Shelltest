
package com.azstudio.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AudioAddresses {

    @SerializedName("uk")
    @Expose
    private List<String> uk = null;
    @SerializedName("us")
    @Expose
    private List<String> us = null;

    public List<String> getUk() {
        return uk;
    }

    public void setUk(List<String> uk) {
        this.uk = uk;
    }

    public List<String> getUs() {
        return us;
    }

    public void setUs(List<String> us) {
        this.us = us;
    }

}

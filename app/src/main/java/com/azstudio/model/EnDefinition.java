
package com.azstudio.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EnDefinition {

    @SerializedName("pos")
    @Expose
    private String pos;
    @SerializedName("defn")
    @Expose
    private String defn;

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getDefn() {
        return defn;
    }

    public void setDefn(String defn) {
        this.defn = defn;
    }

}

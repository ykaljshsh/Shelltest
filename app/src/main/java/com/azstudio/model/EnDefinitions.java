
package com.azstudio.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EnDefinitions {

    @SerializedName("adj")
    @Expose
    private List<String> adj = null;
    @SerializedName("n")
    @Expose
    private List<String> n = null;

    public List<String> getAdj() {
        return adj;
    }

    public void setAdj(List<String> adj) {
        this.adj = adj;
    }

    public List<String> getN() {
        return n;
    }

    public void setN(List<String> n) {
        this.n = n;
    }

}

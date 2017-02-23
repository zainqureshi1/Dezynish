package com.e2esp.dezynish.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zain on 2/17/2017.
 */
public class Resume {

    private String title;
    private List<DataResume> data = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<DataResume> getData() {
        return data;
    }

    public void setData(List<DataResume> data) {
        this.data = data;
    }
}

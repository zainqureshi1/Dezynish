package com.e2esp.dezynish.models.orders;

public class DrawerSubItem {

    private String section;
    private int count = 0;

    public DrawerSubItem(String section, int count) {
        this.section = section;
        this.count = count;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}

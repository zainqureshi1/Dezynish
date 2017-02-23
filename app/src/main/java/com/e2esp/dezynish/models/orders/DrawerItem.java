package com.e2esp.dezynish.models.orders;

import com.e2esp.dezynish.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.List;

public class DrawerItem implements Parent<DrawerSubItem> {

    private String section;
    private int icon;
    private int count = 0;
    private ArrayList<DrawerSubItem> subItems;

    public DrawerItem(String section, int icon, int count, ArrayList<DrawerSubItem> subItems) {
        this.section = section;
        this.icon = icon;
        this.count = count;
        this.subItems = subItems;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void addSubItems(ArrayList<DrawerSubItem> subItems) {
        this.getChildList().addAll(subItems);
    }

    @Override
    public List<DrawerSubItem> getChildList() {
        if (subItems == null) {
            subItems = new ArrayList<>();
        }
        return subItems;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return "Products".equals(section);
    }

}

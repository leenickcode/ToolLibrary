package com.lee.toollibrary.views;

/**
 * Created by nick on 2018/5/28.
 *
 * @author nick
 */
public class BottomItem {
    /**
     * 模块名
     */
    private String name;
    /**
     * 未选中时模块图标
     */
    private int icondef;
    /**
     * 选中时的图标
     */
    private int iconPre;

    public BottomItem(String name, int icon) {
        this.name = name;
        this.icondef = icon;
    }

    public BottomItem(String name, int icondef, int iconPre) {
        this.name = name;
        this.icondef = icondef;
        this.iconPre = iconPre;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcondef() {
        return icondef;
    }

    public void setIcondef(int icondef) {
        this.icondef = icondef;
    }

    public int getIconPre() {
        return iconPre;
    }

    public void setIconPre(int iconPre) {
        this.iconPre = iconPre;
    }
}

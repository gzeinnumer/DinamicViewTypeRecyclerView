package com.gzeinnumer.dinamicviewtyperecyclerview.lib.helper;

public class TypeViewItem {
    int type;
    int layout;

    public TypeViewItem(int type, int layout) {
        this.type = type;
        this.layout = layout;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }
}

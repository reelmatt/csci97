package com.cscie97.store.model;

import java.util.ArrayList;
import java.util.List;

public class Basket {
    private String id;

    private List<ProductAssociation> productList;

    public Basket (String id) {
        this.id = id;
        this.productList = new ArrayList<ProductAssociation>();
    }

    public String getId() {
        return this.id;
    }

    public void clear() {
        return;
    }
    public String toString() {
        return "Basket #" + this.id;
    }
}
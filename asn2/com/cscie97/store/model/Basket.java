package com.cscie97.store.model;

import java.util.ArrayList;
import java.util.List;

public class Basket {
    private Integer id;

    private List<ProductAssociation> productList;

    public Basket (Integer id) {
        this.id = id;
        this.productList = new ArrayList<ProductAssociation>();
    }

    public String toString() {
        return "Basket #" + this.id;
    }
}
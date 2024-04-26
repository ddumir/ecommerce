package com.example.ecommerce.domain;

import java.io.Serializable;

public class ItemsDomain implements Serializable {
    private String productName;
    private String productDescription;
    private String productImage; // Change type to String
//    private double price;
private double productPrice;
    private double productMrpPrice;
    private int review;
    private double rating;
    private int NumberinCart;

    public ItemsDomain() {
    }

    public ItemsDomain(String productName, String productDescription, String productImage, double productPrice, double productMrpPrice, int review, double rating) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.productImage = productImage; // Update type
        this.productPrice = productPrice;
        this.productMrpPrice = productMrpPrice;
        this.review = review;
        this.rating = rating;
    }

    public String getProductName() {
        return productName;
    }

    public void setTitle(String title) {
        this.productName = productName;
    }

    public String getproductDescription() {
        return productDescription;
    }

    public void setDescription(String description) {
        this.productDescription = productDescription;
    }

    public String getproductImage() {
        return productImage; // Update return type
    }

    public void setPicUrl(String productImage) {
        this.productImage = productImage; // Update parameter type
    }

    public double getproductPrice() {
        return productPrice;
    }

    public void setproductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public double getproductMrpPrice() {
        return productMrpPrice;
    }

    public void setproductMrpPrice(double productMrpPrice) {
        this.productMrpPrice = productMrpPrice;
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getNumberinCart() {
        return NumberinCart;
    }

    public void setNumberinCart(int numberinCart) {
        this.NumberinCart = numberinCart;
    }
}

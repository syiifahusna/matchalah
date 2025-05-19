package com.matchalah.model;

public class NotificationMessage {

    private String message;
    private String url;
    private boolean inStock;
    private long timestamp;

    public NotificationMessage(){}

    public NotificationMessage(String message, String url, boolean inStock, long timestamp) {
        this.message = message;
        this.url = url;
        this.inStock = inStock;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

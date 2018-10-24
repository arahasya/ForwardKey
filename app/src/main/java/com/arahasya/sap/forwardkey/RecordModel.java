package com.arahasya.sap.forwardkey;


import android.support.annotation.NonNull;

public class RecordModel {
    private String id, keyword;
    private String contact_numbers, contact_names;
    private int count;

    RecordModel() {

    }

    String getKeyword() {
        return keyword;
    }

    public String getId() {
        return id;
    }

    String getContactNumbers() {
        return contact_numbers;
    }

    String getContactNames() {
        return contact_names;
    }

    int getCount() {
        return count;
    }


    public void setId(String id) {
        this.id = id;
    }

    void setCount(int count) {
        this.count = count;
    }

    void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    void setContactNames(String contact_names) {
        this.contact_names = contact_names;
    }

    void setContactNumbers(String contact_numbers) {
        this.contact_numbers = contact_numbers;
    }


    @NonNull
    public String toString() {
        return "ID: " + id + " Keyword: " + keyword + " Contact Names: " + contact_names + " Contact Numbers: " + contact_numbers;

    }

}

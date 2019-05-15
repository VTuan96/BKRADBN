package com.example.bkrad_bn.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Customer implements Serializable {
    String uniqueName;
    String email;
    String fullName;
    int enterpriseId;

    public Customer() {
    }

    public Customer(String uniqueName, String email, String fullName, int enterpriseId) {
        this.uniqueName = uniqueName;
        this.email = email;
        this.fullName = fullName;
        this.enterpriseId = enterpriseId;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(int enterpriseId) {
        this.enterpriseId = enterpriseId;
    }
}
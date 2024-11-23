package com.backend.ecommerce_backend.api.model;

public class DataChange<T> {
    private T data;
    private ChangeType changeType;

    public DataChange() {
    }
    public DataChange(T data, ChangeType changeType) {
        this.data = data;
        this.changeType = changeType;
    }

    public T getData() {
        return data;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setData(T data) {
        this.data = data;
    }

    public enum ChangeType{
        INSERT, UPDATE, DELETE
    }
}

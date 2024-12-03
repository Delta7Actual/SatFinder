package com.example.satfinder.Objects.Interfaces;

public interface IStorageCallback<T> {
    void onSuccess(T result);
    void onFailure(String error);
}

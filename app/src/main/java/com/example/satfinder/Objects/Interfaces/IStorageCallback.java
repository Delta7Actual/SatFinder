package com.example.satfinder.Objects.Interfaces;

/**
 * Callback interface for handling storage operations (e.g., saving or retrieving data).
 * @param <T> The type of the result that will be returned on success.
 */
public interface IStorageCallback<T> {
    void onSuccess(T result);
    void onFailure(String errorMessage);
}

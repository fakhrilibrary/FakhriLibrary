package com.fakhrilib.fakhri_library_backend.Exceptation;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

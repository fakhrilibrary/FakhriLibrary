package com.fakhrilib.fakhri_library_backend.Exceptation;

public class FileUploadException extends RuntimeException {
    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}

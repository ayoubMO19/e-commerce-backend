package com.vexa.ecommerce.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String type, Integer id) {
        super(type + " with id " + id + " not found");
    }
}

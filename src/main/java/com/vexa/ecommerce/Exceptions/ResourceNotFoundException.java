package com.vexa.ecommerce.Exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String type, Integer id) {
        super(type + " with id " + id + " not found");
    }
}

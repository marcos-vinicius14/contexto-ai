package com.contextoia.common.exceptions;

import java.util.UUID;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, UUID id) {
        super(String.format("%s não encontrado com id: %s", resource, id),
                "RESOURCE_NOT_FOUND");
    }
}

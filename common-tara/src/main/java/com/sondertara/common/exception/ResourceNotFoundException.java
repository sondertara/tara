package com.sondertara.common.exception;

import com.sondertara.common.text.StringUtils;

/**
 *  */
public class ResourceNotFoundException extends RuntimeException {

    private String resourceType;
    private String resourceId;

    public ResourceNotFoundException(String resourceType, String resourceId) {
        super(StringUtils.format("not found {} resource, id or name: {}", resourceType, resourceId));
        this.resourceId = resourceId;
        this.resourceType = resourceType;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }
}

package com.dragonsofmugloar.client.exception;

import org.springframework.web.client.ResourceAccessException;

public class MugloarServerException extends MugloarApiException {
    public MugloarServerException(String message) {
        super(message);
    }
}
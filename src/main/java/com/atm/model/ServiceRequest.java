package com.atm.model;

import com.atm.enums.RequestStatus;
import com.atm.enums.RequestType;

import java.time.LocalDateTime;

public class ServiceRequest {
    private String requestId, reason, reviewNote;
    private RequestType type;
    private RequestStatus status;
    private LocalDateTime createdAt, resolvedAt;
}

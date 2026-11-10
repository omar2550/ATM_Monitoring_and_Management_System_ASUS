package com.atm.model;

import com.atm.dao.ServiceRequestDao;
import com.atm.enums.RequestStatus;
import com.atm.enums.RequestType;

import java.time.LocalDateTime;
import java.util.UUID;

public class ServiceRequest {
    private String requestId, reason, reviewNote;
    private RequestType type;
    private RequestStatus status;
    private LocalDateTime createdAt, resolvedAt;

    public ServiceRequest() { }

    public ServiceRequest(RequestType type, String reason) {
        this.requestId = UUID.randomUUID().toString();
        this.type = type;
        this.reason = reason;
        this.status = RequestStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void approve(Admin admin, String note) {
        if (!isPending())
            throw new IllegalArgumentException("Request is already " + status);

        status = RequestStatus.APPROVED;
        reviewNote = note;
        resolvedAt = LocalDateTime.now();

        new ServiceRequestDao().review(requestId, admin.getUserId(), status,
                reviewNote, resolvedAt);
    }

    public void reject(Admin admin, String note) {
        if (!isPending())
            throw new IllegalArgumentException("Request is already " + status);

        status = RequestStatus.REJECTED;
        reviewNote = note;
        resolvedAt = LocalDateTime.now();

        new ServiceRequestDao().review(requestId, admin.getUserId(), status,
                reviewNote, resolvedAt);
    }

    public boolean isPending() {
        return status == RequestStatus.PENDING;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReviewNote() {
        return reviewNote;
    }

    public void setReviewNote(String reviewNote) {
        this.reviewNote = reviewNote;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}

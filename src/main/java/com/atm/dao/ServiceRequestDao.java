package com.atm.dao;

import com.atm.model.RequestStatus;
import com.atm.model.RequestType;
import com.atm.model.ServiceRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** service_requests. الـ associations (submits / about / reviews) بره الـ class. */
public class ServiceRequestDao extends BaseDao {

    private static final String SELECT =
            "SELECT request_id, type, status, reason, review_note, created_at, resolved_at FROM service_requests ";

    public void save(ServiceRequest r, String customerId, String accountNumber, String cardNumber) {
        execute("INSERT INTO service_requests (request_id, customer_id, type, status, reason, review_note, " +
                        "account_number, card_number, created_at, resolved_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                r.getRequestId(), customerId, r.getType(), r.getStatus(), r.getReason(), r.getReviewNote(),
                accountNumber, cardNumber, r.getCreatedAt(), r.getResolvedAt());
    }

    /** مراجعة الطلب (approve / reject) وتسجيل الـ admin اللي راجعه. بتنجح بس لو الطلب لسه PENDING. */
    public void review(String requestId, String adminId, RequestStatus status, String note, LocalDateTime at) {
        int rows = execute("UPDATE service_requests SET status = ?, review_note = ?, reviewed_by = ?, " +
                        "resolved_at = ? WHERE request_id = ? AND status = 'PENDING'",
                status, note, adminId, at, requestId);
        if (rows == 0) {
            throw new IllegalStateException("Request not found or already reviewed: " + requestId);
        }
    }

    public Optional<ServiceRequest> findById(String requestId) {
        return queryOne(SELECT + "WHERE request_id = ?", this::map, requestId);
    }

    public List<ServiceRequest> findByCustomerId(String customerId) {
        return queryList(SELECT + "WHERE customer_id = ? ORDER BY created_at DESC", this::map, customerId);
    }

    public List<ServiceRequest> findPending() {
        return queryList(SELECT + "WHERE status = 'PENDING' ORDER BY created_at", this::map);
    }

    /** مين قدّم الطلب وبيخص أنهي حساب/كارت. */
    public Optional<RequestTarget> findTarget(String requestId) {
        return queryOne("SELECT customer_id, account_number, card_number FROM service_requests WHERE request_id = ?",
                rs -> new RequestTarget(rs.getString(1), rs.getString(2), rs.getString(3)), requestId);
    }

    private ServiceRequest map(ResultSet rs) throws SQLException {
        ServiceRequest r = new ServiceRequest();
        r.setRequestId(rs.getString("request_id"));
        r.setType(RequestType.valueOf(rs.getString("type")));
        r.setStatus(RequestStatus.valueOf(rs.getString("status")));
        r.setReason(rs.getString("reason"));
        r.setReviewNote(rs.getString("review_note"));
        r.setCreatedAt(ts(rs, "created_at"));
        r.setResolvedAt(ts(rs, "resolved_at"));
        return r;
    }
}

package service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import model.Request;
import repository.RequestRepository;

public class RequestService {

    private final RequestRepository requestRepository;

    public RequestService() {
        this.requestRepository = new RequestRepository();
    }

    // 1. Submit a new request
    public boolean submitRequest(Request request) throws SQLException {

        if (request == null) {
            return false;
        }

        if (request.getUserId() <= 0) {
            return false;
        }

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            return false;
        }

        if (request.getAmount() == null
                || request.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }

        // Ensure a new request starts as PENDING.
        request.setStatus("PENDING");

        return requestRepository.submitRequest(request);
    }

    // 2. Get a request by its ID
    public Request getRequestById(int requestId) throws SQLException {

        if (requestId <= 0) {
            return null;
        }

        return requestRepository.getRequestById(requestId);
    }

    // 3. Get all requests submitted by a particular user
    public List<Request> getRequestsByUserId(int userId) throws SQLException {

        if (userId <= 0) {
            return List.of();
        }

        return requestRepository.getRequestsByUserId(userId);
    }

    // 4. Get all requests
    public List<Request> getAllRequests() throws SQLException {

        return requestRepository.getAllRequests();
    }
}
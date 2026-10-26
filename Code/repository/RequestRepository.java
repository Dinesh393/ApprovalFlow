
package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Request;
import util.DBConnection;

public class RequestRepository {

    // 1. Submit a new request
    public boolean submitRequest(Request request) throws SQLException {

        String sql = """
                INSERT INTO requests
                (user_id, title, description, amount)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, request.getUserId());
            ps.setString(2, request.getTitle());
            ps.setString(3, request.getDescription());
            ps.setBigDecimal(4, request.getAmount());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected != 1) {
                return false;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    request.setRequestId(keys.getInt(1));
                    request.setStatus("PENDING");
                    return true;
                }
            }
        }

        return false;
    }

    // 2. Retrieve a request by its ID
    public Request getRequestById(int requestId) throws SQLException {

        String sql = """
                SELECT request_id, user_id, title,
                       description, amount, status
                FROM requests
                WHERE request_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, requestId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRequest(rs);
                }
            }
        }

        return null;
    }

    // 3. Retrieve all requests submitted by a user
    public List<Request> getRequestsByUserId(int userId)
            throws SQLException {

        String sql = """
                SELECT request_id, user_id, title,
                       description, amount, status
                FROM requests
                WHERE user_id = ?
                ORDER BY created_at DESC
                """;

        List<Request> requests = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapRequest(rs));
                }
            }
        }

        return requests;
    }

    // 4. Retrieve all requests
    public List<Request> getAllRequests() throws SQLException {

        String sql = """
                SELECT request_id, user_id, title,
                       description, amount, status
                FROM requests
                ORDER BY created_at DESC
                """;

        List<Request> requests = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                requests.add(mapRequest(rs));
            }
        }

        return requests;
    }

    // Helper method: Convert a database row into a Request object
    // Instead of writing this repeated code in every retrieval method, I'm using this helper method. Gonna implement in every class.
    private Request mapRequest(ResultSet rs) throws SQLException {

        Request request = new Request();

        request.setRequestId(rs.getInt("request_id"));
        request.setUserId(rs.getInt("user_id"));
        request.setTitle(rs.getString("title"));
        request.setDescription(rs.getString("description"));
        request.setAmount(rs.getBigDecimal("amount"));
        request.setStatus(rs.getString("status"));

        return request;
    }
}

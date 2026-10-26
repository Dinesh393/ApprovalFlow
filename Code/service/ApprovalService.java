package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import model.ApprovalHistory;
import model.ApprovalStep;
import repository.ApprovalHistoryRepository;
import repository.ApprovalRepository;
import util.DBConnection;

public class ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final ApprovalHistoryRepository historyRepository;

    public ApprovalService() {
        this.approvalRepository = new ApprovalRepository();
        this.historyRepository = new ApprovalHistoryRepository();
    }
    

    // 1. Assign an approver to a request
    public boolean assignApprover(ApprovalStep approvalStep)
            throws SQLException {

        if (approvalStep == null
                || approvalStep.getRequestId() <= 0
                || approvalStep.getApproverId() <= 0) {
            return false;
        }

        approvalStep.setStatus("PENDING");

        return approvalRepository.assignApprover(approvalStep);
    }
    

    // 2. Get all approval steps for a request
    public List<ApprovalStep> getApprovalStepsByRequestId(int requestId)
            throws SQLException {

        if (requestId <= 0) {
            return List.of();
        }

        return approvalRepository.getApprovalStepsByRequestId(requestId);
    }
    

    // 3. Get pending approval tasks for an approver
    public List<ApprovalStep> getPendingStepsByApproverId(int approverId)
            throws SQLException {

        if (approverId <= 0) {
            return List.of();
        }

        return approvalRepository.getPendingStepsByApproverId(approverId);
    }

    
    // 4. Approve or reject an assigned approval step
    public boolean processApproval(int stepId, int approverId,
                                   String action, String comments)
            throws SQLException {

        if (stepId <= 0 || approverId <= 0 || action == null) {
            return false;
        }

        action = action.trim().toUpperCase();

        if (!action.equals("APPROVED") && !action.equals("REJECTED")) {
            return false;
        }

        try (Connection connection = DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                int requestId;

                // Verify assignment and lock the pending step.
                String checkSql =
                    "SELECT request_id FROM approval_steps "
                  + "WHERE step_id = ? AND approver_id = ? "
                  + "AND status = 'PENDING' FOR UPDATE";

                try (PreparedStatement ps =
                         connection.prepareStatement(checkSql)) {

                    ps.setInt(1, stepId);
                    ps.setInt(2, approverId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            connection.rollback();
                            return false;
                        }

                        requestId = rs.getInt("request_id");
                    }
                }

                // Update the approval step.
                String updateStepSql =
                    "UPDATE approval_steps SET status = ? "
                  + "WHERE step_id = ? AND approver_id = ? "
                  + "AND status = 'PENDING'";

                try (PreparedStatement ps =
                         connection.prepareStatement(updateStepSql)) {

                    ps.setString(1, action);
                    ps.setInt(2, stepId);
                    ps.setInt(3, approverId);

                    if (ps.executeUpdate() != 1) {
                        connection.rollback();
                        return false;
                    }
                }

                // Record the action in approval history.
                String historySql =
                    "INSERT INTO approval_history "
                  + "(step_id, action, comments) VALUES (?, ?, ?)";

                try (PreparedStatement ps =
                         connection.prepareStatement(historySql)) {

                    ps.setInt(1, stepId);
                    ps.setString(2, action);
                    ps.setString(3, comments);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                            "Failed to record approval history.");
                    }
                }

                // Reject the entire request if any step is rejected.
                if (action.equals("REJECTED")) {

                    String rejectSql =
                        "UPDATE requests SET status = 'REJECTED' "
                      + "WHERE request_id = ? AND status = 'PENDING'";

                    try (PreparedStatement ps =
                             connection.prepareStatement(rejectSql)) {

                        ps.setInt(1, requestId);

                        if (ps.executeUpdate() != 1) {
                            throw new SQLException(
                                "Failed to update request status.");
                        }
                    }

                } else {

                    // Check whether any approval steps remain pending.
                    String pendingSql =
                        "SELECT COUNT(*) FROM approval_steps "
                      + "WHERE request_id = ? AND status = 'PENDING'";

                    int pendingCount;

                    try (PreparedStatement ps =
                             connection.prepareStatement(pendingSql)) {

                        ps.setInt(1, requestId);

                        try (ResultSet rs = ps.executeQuery()) {
                            rs.next();
                            pendingCount = rs.getInt(1);
                        }
                    }

                    // Approve the request only when all steps are approved.
                    if (pendingCount == 0) {

                        String approveSql =
                            "UPDATE requests SET status = 'APPROVED' "
                          + "WHERE request_id = ? AND status = 'PENDING'";

                        try (PreparedStatement ps =
                                 connection.prepareStatement(approveSql)) {

                            ps.setInt(1, requestId);

                            if (ps.executeUpdate() != 1) {
                                throw new SQLException(
                                    "Failed to update request status.");
                            }
                        }
                    }
                }

                connection.commit();
                return true;

            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    // 5. Get history for a request
    public List<ApprovalHistory> getHistoryByRequestId(int requestId)
            throws SQLException {

        if (requestId <= 0) {
            return List.of();
        }

        return historyRepository.getHistoryByRequestId(requestId);
    }

    // 6. Get history for an individual approval step
    public List<ApprovalHistory> getHistoryByStepId(int stepId)
            throws SQLException {

        if (stepId <= 0) {
            return List.of();
        }

        return historyRepository.getHistoryByStepId(stepId);
    }
}
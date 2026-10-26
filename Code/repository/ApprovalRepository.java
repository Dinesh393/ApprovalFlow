package repository;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.ApprovalStep;
import util.DBConnection;
public class ApprovalRepository {
	public boolean assignApprover(ApprovalStep approvalStep) throws SQLException{
		String sql = "INSERT INTO approval_steps (request_id, approver_id) VALUES (?, ?)";
		try(Connection connection = DBConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
			preparedStatement.setInt(1, approvalStep.getRequestId());
			preparedStatement.setInt(2, approvalStep.getApproverId());
			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected != 1) {
                return false;
            }

            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                if (keys.next()) {
                    approvalStep.setStepId(keys.getInt(1));
                    approvalStep.setStatus("PENDING");
                    return true;
                }
            }
		}
		return false;
	}
	
	public List<ApprovalStep> getApprovalStepsByRequestId(int requestId) throws SQLException{
		String sql = "SELECT * FROM approval_steps WHERE request_id = ? ORDER BY step_id ASC";
		List<ApprovalStep> approvalSteps = new ArrayList<>();
		try(Connection connection = DBConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)){
			preparedStatement.setInt(1, requestId);
			try(ResultSet resultSet = preparedStatement.executeQuery()){
				while(resultSet.next()) {
					approvalSteps.add(mapApprovalStep(resultSet));
				}
				
			}
		}
		return approvalSteps;
	}
	
	public List<ApprovalStep> getPendingStepsByApproverId(int approverId) throws SQLException {
		String sql = "SELECT * FROM approval_steps WHERE approver_id = ? AND status = 'PENDING' ORDER BY step_id ASC";
		List<ApprovalStep> pendingSteps = new ArrayList<>();
		try(Connection connection = DBConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)){
			preparedStatement.setInt(1, approverId);
			try(ResultSet resultSet = preparedStatement.executeQuery()){
				while(resultSet.next()) {
					pendingSteps.add(mapApprovalStep(resultSet));
				}
				
			}
		}
		return pendingSteps;
	}
	
	
	public List<ApprovalStep> getApprovalStepsByApproverId(int approverId) throws SQLException{
		String sql = "SELECT * FROM approval_steps WHERE approver_id = ? ORDER BY step_id ASC";
		List<ApprovalStep> approvalSteps = new ArrayList<>();
		try(Connection connection = DBConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)){
			preparedStatement.setInt(1, approverId);
			try(ResultSet resultSet = preparedStatement.executeQuery()){
				while(resultSet.next()) {
					approvalSteps.add(mapApprovalStep(resultSet));
				}
				
			}
		}
		return approvalSteps;
	}
	
	public ApprovalStep getApprovalStepById(int stepId) throws SQLException{
		String sql = "SELECT * FROM approval_steps WHERE step_id = ?";
		try(Connection connection = DBConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)){
			preparedStatement.setInt(1, stepId);
			try(ResultSet resultSet = preparedStatement.executeQuery()){
				if(resultSet.next()) {
					return mapApprovalStep(resultSet);
				}
			}
		}
		return null;
	}
	
	private ApprovalStep mapApprovalStep(ResultSet resultSet) throws SQLException{
		ApprovalStep approvalStep = new ApprovalStep();
		
		approvalStep.setStepId(resultSet.getInt("step_id"));
		approvalStep.setRequestId(resultSet.getInt("request_id"));
		approvalStep.setApproverId(resultSet.getInt("approver_id"));
		approvalStep.setStatus(resultSet.getString("status"));
		
		return approvalStep;
	}
}
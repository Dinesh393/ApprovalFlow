package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.ApprovalHistory;
import util.DBConnection;

public class ApprovalHistoryRepository {
	public boolean addHistory(ApprovalHistory approvalHistory) throws SQLException{
		String sql = "INSERT INTO approval_history (step_id, action, comments) VALUES (?, ?, ?)";
		try(Connection connection = DBConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
			preparedStatement.setInt(1, approvalHistory.getStepId());
			preparedStatement.setString(2, approvalHistory.getAction());
			preparedStatement.setString(3, approvalHistory.getComments());
			int rowsAffected = preparedStatement.executeUpdate();
			if(rowsAffected != 1) {
				return false;
			}
			
			try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                if (keys.next()) {
                    approvalHistory.setHistoryId(keys.getInt(1));
                    return true;
                }
            }
		}
		return false;
	}
	
	public List<ApprovalHistory> getHistoryByStepId(int stepId) throws SQLException{
		String sql = "SELECT history_id, step_id, action, comments FROM approval_history WHERE step_id = ? ORDER BY action_at ASC";
		List<ApprovalHistory> approvalHistories = new ArrayList<ApprovalHistory>();
		try(Connection connection = DBConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)){
			preparedStatement.setInt(1, stepId);
			try(ResultSet resultSet = preparedStatement.executeQuery()){
				while(resultSet.next()) {
					approvalHistories.add(mapApprovalHistory(resultSet));
				}
			}
		}
		return approvalHistories;
	}
	
	public List<ApprovalHistory> getHistoryByRequestId(int requestId) throws SQLException{
		String sql = "SELECT ah.history_id, ah.step_id, ah.action, ah.comments "
				     + "FROM approval_history AS ah JOIN approval_steps AS ast "
				     + "ON ah.step_id = ast.step_id WHERE ast.request_id = ? "
				     + "ORDER BY ah.action_at ASC";
		List<ApprovalHistory> approvalHistories = new ArrayList<ApprovalHistory>();
		try(Connection connection = DBConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)){
			preparedStatement.setInt(1, requestId);
			try(ResultSet resultSet = preparedStatement.executeQuery()){
				while(resultSet.next()) {
					approvalHistories.add(mapApprovalHistory(resultSet));
				}
			}
		}
		return approvalHistories;
	}
	
	private ApprovalHistory mapApprovalHistory(ResultSet resultSet) throws SQLException{
		ApprovalHistory approvalHistory = new ApprovalHistory();
		approvalHistory.setHistoryId(resultSet.getInt("history_id"));
		approvalHistory.setStepId(resultSet.getInt("step_id"));
		approvalHistory.setAction(resultSet.getString("action"));
		approvalHistory.setComments(resultSet.getString("comments"));
		
		return approvalHistory;
		
	}
}

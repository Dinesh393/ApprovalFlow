package repository;
import java.util.List;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.User;
import util.DBConnection;

public class UserRepository {
	static Scanner scanner = new Scanner(System.in);
	public boolean addUser(User user) {
		String sql = "INSERT INTO users (full_name, username, email, password_hash, role) VALUES (?, ?, ?, ?, ?)";
		try(Connection connection = DBConnection.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
			preparedStatement.setString(1, user.getFullName());
			preparedStatement.setString(2, user.getUsername());
			preparedStatement.setString(3, user.getEmail());
			preparedStatement.setString(4, user.getPasswordHash());
			preparedStatement.setString(5, user.getRole());
			
			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected != 1) {
                return false;
            }
			try (ResultSet generatedKeys =
                    preparedStatement.getGeneratedKeys()) {

           if (generatedKeys.next()) {
               user.setUserId(generatedKeys.getInt(1));
               return true;
           }
       }

       return false;
		} catch (SQLException e) {
			System.err.println("Unable to add User : " + e.getMessage());
			return false;
		}
	}
	
	public User getUserById(int userId) {
		String sql = "SELECT user_id, full_name, username, email, role "
	               + "FROM users WHERE user_id = ?";

		try(Connection connection = DBConnection.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(sql);){
			preparedStatement.setInt(1, userId);
			try(ResultSet resultSet = preparedStatement.executeQuery()){
				if(resultSet.next()) {
					User user = new User();
					
					user.setUserId(resultSet.getInt("user_id"));
	                user.setFullName(resultSet.getString("full_name"));
	                user.setUsername(resultSet.getString("username"));
	                user.setEmail(resultSet.getString("email"));
	                user.setRole(resultSet.getString("role"));
	                
					return user;
				}
			}
			return null;
			
		} catch (SQLException e) {
			System.err.println("Unable to retrieve the user : " + e.getMessage());
			return null;
		}
	}
	
	public List<User> findAllUsers() throws SQLException{
		String sql = "SELECT user_id, full_name, username, email, role "
	               + "FROM users";
		List<User> users = new ArrayList<>();
		try(Connection connection = DBConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				ResultSet resultSet = preparedStatement.executeQuery()){
			while(resultSet.next()) {
				User user = new User();
				
				user.setUserId(resultSet.getInt("user_id"));
                user.setFullName(resultSet.getString("full_name"));
                user.setUsername(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setRole(resultSet.getString("role"));
                 users.add(user);
			}
			return users;
		}
	}
	
	
	public boolean updateUser(){
		System.out.print("Enter User ID : ");
		int userId = scanner.nextInt();
		scanner.nextLine();

		System.out.print("Enter the new Full Name : ");
		String fullName = scanner.nextLine().trim();
		
		System.out.print("Enter the new Username : ");
		String userName = scanner.nextLine().trim();
		
		System.out.print("Enter the new Email : ");
		String email = scanner.nextLine().trim();
		String sql = "UPDATE users SET full_name = ?, username = ?, email = ? WHERE user_id = ?";
		try(Connection connection = DBConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)){
					preparedStatement.setString(1, fullName);
					preparedStatement.setString(2, userName);
					preparedStatement.setString(3, email);
					preparedStatement.setInt(4, userId);
					
					int rowsAffected = preparedStatement.executeUpdate();
					
					return rowsAffected == 1;
		}
		catch (SQLException e) {
			System.out.println("Unable to update.." + e.getMessage());
			return false;
		}
	}
	
	public boolean deleteUser(){
		System.out.print("Enter User ID : ");
		int userId = scanner.nextInt();
		
		String sql = "DELETE FROM users WHERE user_id = ?";
		try(Connection connection = DBConnection.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)){
					preparedStatement.setInt(1, userId);
					
					int rowsAffected = preparedStatement.executeUpdate();
					
					return rowsAffected == 1;
		}
		catch (SQLException e) {
			System.out.println("Unable to delete.." + e.getMessage());
			return false;
		}
	}
	
	
	public static void main(String[] args) {
//		User user = new User("Paloju Dinesh", "Dinesh@29", "dinesh@gmail.com", "Dinesh@123", "Employee");
		UserRepository userRepository = new UserRepository();
		userRepository.deleteUser();
		}
//		userRepository.addUser(user);

	public User getUserByUsername(String username) throws SQLException {

	    String sql = "SELECT user_id, full_name, username, email, password_hash, role "
	               + "FROM users WHERE username = ?";

	    try (Connection connection = DBConnection.getConnection();
	         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

	        preparedStatement.setString(1, username);

	        try (ResultSet resultSet = preparedStatement.executeQuery()) {

	            if (resultSet.next()) {
	                User user = new User();

	                user.setUserId(resultSet.getInt("user_id"));
	                user.setFullName(resultSet.getString("full_name"));
	                user.setUsername(resultSet.getString("username"));
	                user.setEmail(resultSet.getString("email"));
	                user.setPasswordHash(resultSet.getString("password_hash"));
	                user.setRole(resultSet.getString("role"));

	                return user;
	            }
	        }
	    }

	    return null;
	}
	
	public boolean updateUser(User user) throws SQLException {

	    String sql = "UPDATE users SET full_name = ?, email = ? "
	               + "WHERE user_id = ?";

	    try (Connection connection = DBConnection.getConnection();
	         PreparedStatement preparedStatement =
	                 connection.prepareStatement(sql)) {

	        preparedStatement.setString(1, user.getFullName());
	        preparedStatement.setString(2, user.getEmail());
	        preparedStatement.setInt(3, user.getUserId());

	        return preparedStatement.executeUpdate() == 1;
	    }
	}
	
	
	
	
	}

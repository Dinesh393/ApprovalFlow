package service;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;
import java.util.List;
import model.User;
import repository.UserRepository;

public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    //Register user
    public boolean registerUser(User user) throws SQLException {

        if (user == null) {
            return false;
        }

        if (user.getFullName() == null || user.getFullName().isBlank()
                || user.getUsername() == null || user.getUsername().isBlank()
                || user.getEmail() == null || user.getEmail().isBlank()
                || user.getPasswordHash() == null || user.getPasswordHash().isBlank()
                || user.getRole() == null || user.getRole().isBlank()) {
            return false;
        }

        // Public registration should not allow privileged roles.
        if (!"EMPLOYEE".equals(user.getRole())) {
            return false;
        }

        // Hash the supplied plain-text password before storing it.
        String passwordHash = BCrypt.hashpw(
                user.getPasswordHash(),
                BCrypt.gensalt(12)
        );

        user.setPasswordHash(passwordHash);

        return userRepository.addUser(user);
    }
    

    //Login user
    public User loginUser(String username, String password) throws SQLException {

        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            System.out.println("DEBUG: Username or password is blank");
            return null;
        }

        User user = userRepository.getUserByUsername(username);

        System.out.println("DEBUG: User found = " + (user != null));

        if (user == null) {
            return null;
        }

        System.out.println("DEBUG: Retrieved username = " + user.getUsername());
        System.out.println("DEBUG: Password hash exists = "
                + (user.getPasswordHash() != null));

        boolean passwordMatches =
                BCrypt.checkpw(password, user.getPasswordHash());

        System.out.println("DEBUG: Password matches = " + passwordMatches);

        if (!passwordMatches) {
            return null;
        }

        user.setPasswordHash(null);

        return user;
    }
    
    //Get user by UserID
    public User getUserById(int userId) throws SQLException{
    	if(userId <= 0) return null;
    	User user = userRepository.getUserById(userId);
    	if(user == null) return null;
    	
    	return user;
    }
    
    public List<User> getAllUsers() throws SQLException{
    	List<User> usersList = userRepository.findAllUsers();
    	
    	return usersList;
    }
    
 // Update user
    public boolean updateUser(User user) throws SQLException {

        if (user == null) {
            return false;
        }

        if (user.getUserId() <= 0) {
            return false;
        }

        if (user.getFullName() == null || user.getFullName().isBlank()
                || user.getEmail() == null || user.getEmail().isBlank()) {
            return false;
        }

        User existingUser = userRepository.getUserById(user.getUserId());

        if (existingUser == null) {
            return false;
        }

        // Prevent ordinary profile updates from changing the user's role.
        user.setRole(existingUser.getRole());

        // Preserve the existing password hash if the update doesn't handle passwords.
        user.setPasswordHash(null);

        return userRepository.updateUser(user);
    }
     
}
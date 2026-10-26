
package model;

public class User {
    private int userId;
    private String fullName;
    private String username;
    private String email;
    private String passwordHash;
    private String role;

    public User() {
        super();
    }

    public User(int userId) {
        this();
        this.userId = userId;
    }

    public User(int userId, String fullName) {
        this(userId);
        this.fullName = fullName;
    }

    public User(int userId, String fullName, String username) {
        this(userId, fullName);
        this.username = username;
    }

    public User(int userId, String fullName, String username, String email) {
        this(userId, fullName, username);
        this.email = email;
    }

    public User(int userId, String fullName, String username,
                String email, String passwordHash) {
        this(userId, fullName, username, email);
        this.passwordHash = passwordHash;
    }

    public User(int userId, String fullName, String username,
                String email, String passwordHash, String role) {
        this(userId, fullName, username, email, passwordHash);
        this.role = role;
    }

    public User(String fullName, String username, String email,
                String passwordHash, String role) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }
    
    public User(String fullName, String username, String email, String role) {
    this.fullName = fullName;
    this.username = username;
    this.email = email;
    this.role = role;
}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
    
    
}

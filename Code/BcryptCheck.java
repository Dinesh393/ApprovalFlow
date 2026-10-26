import org.mindrot.jbcrypt.BCrypt;

public class BcryptCheck {

    public static void main(String[] args) {
    	String password = "TestPass@123!";
    	String hash = BCrypt.hashpw(password, BCrypt.gensalt(12));

    	System.out.println("Verification: " + BCrypt.checkpw(password, hash));
    	System.out.println("Hash: " + hash);
    }
}
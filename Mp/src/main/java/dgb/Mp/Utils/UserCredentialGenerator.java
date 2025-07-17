package dgb.Mp.Utils;

import java.security.SecureRandom;
import java.util.UUID;

public class UserCredentialGenerator {

    // Characters to choose from for password generation
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*_+";
    private static final int PASSWORD_LENGTH = 8;  // Adjust the password length as needed

    public static String generateUsername(String email) {
        // Using UUID to create a unique username
        return email.split("@")[0] + UUID.randomUUID().toString().substring(0, 2);  // 2 characters of UUID as username
    }

    public static String generatePassword() {
        // Generate a secure random password
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }
}


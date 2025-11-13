package security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    private static final int COST = 12;

    public static String hash(String password) {
        validatePolicy(password);
        return BCrypt.hashpw(password, BCrypt.gensalt(COST));
    }

    public static boolean matches(String password, String hashedPassword) {
        if (hashedPassword == null || hashedPassword.isEmpty()) return false;
        return BCrypt.checkpw(password, hashedPassword);
    }

    public static boolean needsRehash(String password) {
        try {
            String[] parts = password.split("\\$");
            if (parts.length < 3) return true;
            String costPart = parts[2];
            int cost = Integer.parseInt(costPart);
            return cost < COST;
        } catch (Exception e) {
            return true;
        }
    }

    public static void validatePolicy(String password) {
        if (password == null) throw new IllegalArgumentException();

        if (password.length() < 8) throw new IllegalArgumentException("Minimum password length is 8");
        if (password.length() > 64) throw new IllegalArgumentException("Maximum password length is 64");

        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasOther = password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));

        int score = 0;
        if (hasLower) score++;
        if (hasUpper) score++;
        if (hasDigit) score++;
        if (hasOther) score++;

        if (score < 3) throw new IllegalArgumentException("Password is unsafe");
    }
    
}

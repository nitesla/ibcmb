package longbridge.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Created by Fortune on 5/4/2017.
 */

@Component
public class PasswordCreator {

    private static SecureRandom random = new SecureRandom();

    private String DIGITS = "0123456789";
    private String LOCASE_ALPHABETS = "abcdefghjkmnpqrstuvwxyz";
    private String UPCASE_ALPHABETS = "ABCDEFGHJKMNPQRSTUVWXYZ";
    private String ALL_ALPHABETS = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";
    private String SPECIAL_CHARS = "@#$%^)(&";
    private String ALL = DIGITS + LOCASE_ALPHABETS + UPCASE_ALPHABETS + SPECIAL_CHARS;
    private char[] upcaseArray = UPCASE_ALPHABETS.toCharArray();
    private char[] locaseArray = LOCASE_ALPHABETS.toCharArray();
    private char[] allAphabetsArray = ALL_ALPHABETS.toCharArray();
    private char[] digitsArray = DIGITS.toCharArray();
    private char[] specialCharsArray = SPECIAL_CHARS.toCharArray();
    private char[] allArray = ALL.toCharArray();


    public String generatePassword(int MinLength, String specialChars) {

        specialCharsArray = specialChars.toCharArray();
        StringBuilder sb = new StringBuilder();
        // get at least one lowercase letter
        sb.append(locaseArray[random.nextInt(locaseArray.length)]);
        // get at least one uppercase letter
        sb.append(upcaseArray[random.nextInt(upcaseArray.length)]);
        // get at least one digit
        sb.append(digitsArray[random.nextInt(digitsArray.length)]);
        // get at least one symbol
        sb.append(specialCharsArray[random.nextInt(specialCharsArray.length)]);
        // fill in remaining with random letters
        for (int i = 0; i < MinLength - 4; i++) {
            sb.append(allAphabetsArray[random.nextInt(allAphabetsArray.length)]);
        }
        return sb.toString();
    }

}
package longbridge.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Created by Fortune on 5/4/2017.
 */

@Component
public class PasswordCreator {

    private static SecureRandom random = new SecureRandom();

    private String DIGITS = "123456789";
    private String LOCASE_ALPHABETS = "abcdefghjkmnpqrstuvwxyz";
    private String UPCASE_ALPHABETS = "ABCDEFGHJKMNPQRSTUVWXYZ";
    private String ALL_ALPHABETS = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";
    private String SPECIAL_CHARS = "@$#";
    private String ALL = DIGITS + LOCASE_ALPHABETS + UPCASE_ALPHABETS + SPECIAL_CHARS;
    private char[] upcaseArray = UPCASE_ALPHABETS.toCharArray();
    private char[] locaseArray = LOCASE_ALPHABETS.toCharArray();
    private char[] allAphabetsArray = ALL_ALPHABETS.toCharArray();
    private char[] digitsArray = DIGITS.toCharArray();
    private char[] specialCharsArray = SPECIAL_CHARS.toCharArray();
    private char[] allArray = ALL.toCharArray();

    private int numOfDigits = 1;
    private int numOfSpecial = 1;
    private int minLength = 8;


    public String generatePassword(int length, int noOfDigits,  String specialChars) {

        minLength = length;
        numOfDigits = noOfDigits;
        int count = 0;

//        if(specialChars!=null) {
//            specialCharsArray = specialChars.toCharArray();
//        }
        StringBuilder sb = new StringBuilder();
        // get at least one lowercase letter
//        sb.append(locaseArray[random.nextInt(locaseArray.length)]);
//        count+=1;
        // get at 2 uppercase letter
        for (int i = 0; i < 2; i++) {
            sb.append(upcaseArray[random.nextInt(upcaseArray.length)]);
            count += 1;
        }
        for (int i = 0; i < numOfDigits; i++) {
            sb.append(digitsArray[random.nextInt(digitsArray.length)]);
            count += 1;
        }
        sb.append(upcaseArray[random.nextInt(upcaseArray.length)]);
        count += 1;
        for (int i = 0; i < numOfSpecial; i++) {
            sb.append(specialCharsArray[random.nextInt(specialCharsArray.length)]);
            count += 1;
        }
        // fill in remaining with random letters
        for (int i = 0; i < minLength - count; i++) {
            sb.append(upcaseArray[random.nextInt(upcaseArray.length)]);
        }
        return sb.toString();
    }

}

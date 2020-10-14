package longbridge.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Created by Fortune on 5/4/2017.
 */

@Component
public class PasswordCreator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final SecureRandom random = new SecureRandom();

    private final String DIGITS = "123456789";
    private final String LOCASE_ALPHABETS = "abcdefghjkmnpqrstuvwxyz";
    private final String UPCASE_ALPHABETS = "ABCDEFGHJKMNPQRSTUVWXYZ";
    private final String ALL_ALPHABETS = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";
    private final String SPECIAL_CHARS = "@$#";
    private final String ALL = DIGITS + LOCASE_ALPHABETS + UPCASE_ALPHABETS + SPECIAL_CHARS;
    private final char[] upcaseArray = UPCASE_ALPHABETS.toCharArray();
    private char[] locaseArray = LOCASE_ALPHABETS.toCharArray();
    private char[] allAphabetsArray = ALL_ALPHABETS.toCharArray();
    private final char[] digitsArray = DIGITS.toCharArray();
    private final char[] specialCharsArray = SPECIAL_CHARS.toCharArray();
    private char[] allArray = ALL.toCharArray();


    public String generatePassword(int length, int noOfDigits,  String specialChars) {

        logger.debug("Generating random password for user");

        int count = 0;


        StringBuilder sb = new StringBuilder();
        // get at least one lowercase letter
//        sb.append(locaseArray[random.nextInt(locaseArray.length)]);
//        count+=1;
        // get at 2 uppercase letter
        for (int i = 0; i < 2; i++) {
            sb.append(upcaseArray[random.nextInt(upcaseArray.length)]);
            count += 1;
        }
        for (int i = 0; i < noOfDigits; i++) {
            sb.append(digitsArray[random.nextInt(digitsArray.length)]);
            count += 1;
        }
        sb.append(upcaseArray[random.nextInt(upcaseArray.length)]);
        count += 1;
        int numOfSpecial = 1;
        for (int i = 0; i < numOfSpecial; i++) {
            sb.append(specialCharsArray[random.nextInt(specialCharsArray.length)]);
            count += 1;
        }
        // fill in remaining with random letters
        for (int i = 0; i < length - count; i++) {
            sb.append(upcaseArray[random.nextInt(upcaseArray.length)]);
        }
        return sb.toString();
    }

}

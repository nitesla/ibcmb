package longbridge.utils;

/**
 * Created by Fortune on 3/2/2018.
 */
public class NumberUtils {

    public static String generateReferenceNumber(int numOfDigits) {
        if(numOfDigits<1) {
            throw new IllegalArgumentException(numOfDigits + ": Number must be equal or greater than 1");
        }
        long random = (long) Math.floor(Math.random() * 9 * (long)Math.pow(10,numOfDigits-1)) + (long)Math.pow(10,numOfDigits-1);
        String refCode = Long.toString(random);
        return refCode;
    }

}

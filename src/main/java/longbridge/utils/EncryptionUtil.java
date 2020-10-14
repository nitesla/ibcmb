package longbridge.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptionUtil {

    public static String getSHA512(String stringToHash, String   salt){
        System.out.print("stringToHash:"+stringToHash);
        String generatedSHA = null;
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                if (salt != null && !salt.trim().isEmpty()) {
                    md.update(salt.getBytes(StandardCharsets.UTF_8));
                }

                byte[] bytes = md.digest(stringToHash.getBytes(StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                for (byte aByte : bytes) {
                    sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
                }
                generatedSHA = sb.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        return generatedSHA;
    }
}

package longbridge.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptionUtil {

    public static String getSHA512(String stringToHash, String   salt){
        System.out.println("the stringhas "+stringToHash);
        String generatedSHA = null;
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-512");
                if (salt != null && !salt.trim().isEmpty()) {
                    md.update(salt.getBytes(StandardCharsets.UTF_8));
                }

                byte[] bytes = md.digest(stringToHash.getBytes(StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < bytes.length; i++) {
                    sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
                }
                generatedSHA = sb.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        System.out.println("the generatedSHA "+generatedSHA);
        return generatedSHA;
    }
}

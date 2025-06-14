package hr.javafx.fleetmanagementandmaintanancescheadule.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Utility class for Password Hashinh.
 */
public class PasswordHashing {

    /**
     * Preforms the hash.
     */
    public static String doHashing(String password) {
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.update(password.getBytes());

            byte[] resultByteArray = messageDigest.digest();

            StringBuilder sb = new StringBuilder();

            for(byte b : resultByteArray){
                sb.append(String.format("%02x",b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return "";
    }

}
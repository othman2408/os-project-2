import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Ticket {

    private static String ticket;

    public static String generateTicket(String sequence) {
        byte[] hash = String.format("%32s", sequence).getBytes();
        try {
            for (int i = 0; i < Math.random() * 64 + 1; ++i) {
                hash = MessageDigest.getInstance("SHA-256").digest(hash);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return ticket = formatHexWithDelimiter(hash, ":").substring(78);
    }

    private static String formatHexWithDelimiter(byte[] array, String delimiter) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : array) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex).append(delimiter);
        }
        // Removing the last delimiter
        hexString.deleteCharAt(hexString.length() - 1);
        return hexString.toString();
    }

    @Override
    public String toString() {
        return ticket;
    }


    public static void main(String[] args) {
        
        System.out.println(generateTicket("othman"));
        
    }
}
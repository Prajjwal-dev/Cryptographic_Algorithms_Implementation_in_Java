import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;

public class DESDemo {

    // Convert byte[] to hex string
    private static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) sb.append(String.format("%02X", b));
        return sb.toString();
    }

    // Convert hex string to byte[]
    private static byte[] hexToBytes(String hex) {
        hex = hex.replaceAll("\\s+","");
        if (hex.length() % 2 != 0) throw new IllegalArgumentException("Invalid hex string");
        byte[] out = new byte[hex.length()/2];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
        }
        return out;
    }

    private static SecretKey getDESKey(byte[] keyBytes) {
        if (keyBytes.length != 8)
            throw new IllegalArgumentException("DES key must be exactly 8 bytes");
        return new SecretKeySpec(keyBytes, "DES");
    }

    private static byte[] desEncrypt(byte[] plaintext, SecretKey key, String transformation, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        if (transformation.contains("/CBC/")) cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        else cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintext);
    }

    private static byte[] desDecrypt(byte[] ciphertext, SecretKey key, String transformation, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        if (transformation.contains("/CBC/")) cipher.init(Cipher.DECRYPT_MODE, key, iv);
        else cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(ciphertext);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8);

        System.out.println("=== DES Demo (Java JCE) ===");
        System.out.println("Note: DES key must be exactly 8 characters (8 bytes).");

        System.out.print("Enter mode (ECB/CBC) [default ECB]: ");
        String mode = sc.nextLine().trim().toUpperCase();
        if (mode.isEmpty()) mode = "ECB";
        if (!mode.equals("ECB") && !mode.equals("CBC")) {
            System.out.println("Invalid mode. Using ECB.");
            mode = "ECB";
        }

        String transformation = "DES/" + mode + "/PKCS5Padding";

        // FIXED INPUT: Only one read
        System.out.print("Encrypt or Decrypt? (E/D): ");
        String choiceInput = sc.nextLine().trim().toUpperCase();
        char choice = choiceInput.isEmpty() ? 'E' : choiceInput.charAt(0);
        if (choice != 'E' && choice != 'D') choice = 'E';

        System.out.print("Enter 8-character key: ");
        String keyStr = sc.nextLine();
        while (keyStr.length() != 8) {
            System.out.println("Key must be exactly 8 characters. Try again:");
            keyStr = sc.nextLine();
        }
        SecretKey key = getDESKey(keyStr.getBytes(StandardCharsets.UTF_8));

        IvParameterSpec iv = null;
        if (mode.equals("CBC")) {
            System.out.print("Enter IV (16 hex chars) or press Enter for default 0000000000000000: ");
            String ivHex = sc.nextLine().trim();
            if (ivHex.isEmpty()) ivHex = "0000000000000000";

            byte[] ivBytes = hexToBytes(ivHex);
            if (ivBytes.length != 8) {
                System.out.println("IV must be 8 bytes (16 hex chars). Exiting.");
                return;
            }
            iv = new IvParameterSpec(ivBytes);
        }

        try {
            if (choice == 'E') {
                System.out.print("Enter plaintext: ");
                String plain = sc.nextLine();
                byte[] cipherBytes = desEncrypt(plain.getBytes(StandardCharsets.UTF_8), key, transformation, iv);
                System.out.println("Ciphertext (hex): " + toHex(cipherBytes));
            } else {
                System.out.print("Enter ciphertext in hex: ");
                String hex = sc.nextLine();
                byte[] cipherBytes = hexToBytes(hex);
                byte[] plainBytes = desDecrypt(cipherBytes, key, transformation, iv);
                System.out.println("Decrypted plaintext: " + new String(plainBytes, StandardCharsets.UTF_8));
            }
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            System.out.println("Decryption failed: Wrong key/IV or corrupted ciphertext.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

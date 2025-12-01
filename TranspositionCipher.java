import java.util.*;

public class TranspositionCipher {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Do you want to Encrypt or Decrypt? (E/D): ");
        char choice = sc.next().toUpperCase().charAt(0);
        sc.nextLine();

        System.out.print("Enter text: ");
        String text = sc.nextLine().replaceAll("\\s+", "").toUpperCase();

        System.out.print("Enter key (number): ");
        int key = sc.nextInt();

        if (choice == 'E') {
            char[] encrypted = new char[text.length()];
            int index = 0;
            for (int i = 0; i < key; i++) {
                for (int j = i; j < text.length(); j += key) {
                    encrypted[index++] = text.charAt(j);
                }
            }
            System.out.println("Encrypted: " + new String(encrypted));
        } else { // Decrypt
            char[] decrypted = new char[text.length()];
            int cols = (int) Math.ceil((double) text.length() / key);
            int shortCols = key * cols - text.length();
            int index = 0;
            int pos = 0;

            int[] colLength = new int[key];
            for (int i = 0; i < key; i++) {
                colLength[i] = cols - (i >= key - shortCols ? 1 : 0);
            }

            char[][] grid = new char[key][cols];
            for (int c = 0; c < key; c++) {
                for (int r = 0; r < colLength[c]; r++) {
                    grid[c][r] = text.charAt(index++);
                }
            }

            for (int r = 0; r < cols; r++) {
                for (int c = 0; c < key; c++) {
                    if (r < colLength[c]) decrypted[pos++] = grid[c][r];
                }
            }
            System.out.println("Decrypted: " + new String(decrypted));
        }
    }
}

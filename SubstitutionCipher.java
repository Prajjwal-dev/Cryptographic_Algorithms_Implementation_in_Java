import java.util.*;

public class SubstitutionCipher {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Do you want to Encrypt or Decrypt? (E/D): ");
        char choice = sc.next().toUpperCase().charAt(0);
        sc.nextLine();

        System.out.println("Enter 26-letter substitution key (A-Z):");
        String key = sc.nextLine().toUpperCase();

        System.out.print("Enter text: ");
        String text = sc.nextLine().toUpperCase();

        StringBuilder result = new StringBuilder();

        if (choice == 'E') {
            for (char ch : text.toCharArray()) {
                if (Character.isLetter(ch)) result.append(key.charAt(ch - 'A'));
                else result.append(ch);
            }
        } else { // Decrypt
            char[] inverse = new char[26];
            for (int i = 0; i < 26; i++) inverse[key.charAt(i) - 'A'] = (char)('A' + i);
            for (char ch : text.toCharArray()) {
                if (Character.isLetter(ch)) result.append(inverse[ch - 'A']);
                else result.append(ch);
            }
        }

        System.out.println((choice == 'E' ? "Encrypted: " : "Decrypted: ") + result);
    }
}

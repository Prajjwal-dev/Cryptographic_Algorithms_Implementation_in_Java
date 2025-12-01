import java.util.Scanner;

public class CaesarCipher {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Do you want to Encrypt or Decrypt? (E/D): ");
        char choice = sc.next().toUpperCase().charAt(0);
        sc.nextLine(); // consume newline

        System.out.print("Enter text: ");
        String text = sc.nextLine();

        System.out.print("Enter shift value: ");
        int shift = sc.nextInt();

        if (choice == 'D') shift = -shift;

        StringBuilder result = new StringBuilder();
        for (char ch : text.toCharArray()) {
            if (Character.isLetter(ch)) {
                char base = Character.isLowerCase(ch) ? 'a' : 'A';
                ch = (char) ((ch - base + shift + 26) % 26 + base);
            }
            result.append(ch);
        }

        System.out.println((choice == 'E' ? "Encrypted: " : "Decrypted: ") + result);
    }
}

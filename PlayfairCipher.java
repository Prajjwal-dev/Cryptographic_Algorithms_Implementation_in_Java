import java.util.*;

public class PlayfairCipher {
    static char[][] table = new char[5][5];
    static Map<Character, int[]> pos = new HashMap<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Do you want to Encrypt or Decrypt? (E/D): ");
        char choice = sc.next().toUpperCase().charAt(0);

        System.out.print("Enter key: ");
        String key = sc.nextLine().toUpperCase().replace("J", "I");

        generateTable(key);

        System.out.print("Enter text: ");
        String text = sc.nextLine().toUpperCase().replace("J", "I").replaceAll("\\s+","");

        String processedText = prepareText(text, choice == 'E');

        String result = (choice == 'E') ? encrypt(processedText) : decrypt(processedText);
        System.out.println((choice == 'E' ? "Encrypted: " : "Decrypted: ") + result);
    }

    static void generateTable(String key) {
        Set<Character> used = new HashSet<>();
        int row = 0, col = 0;

        for (char c : key.toCharArray()) {
            if (Character.isLetter(c) && !used.contains(c)) {
                table[row][col++] = c;
                pos.put(c, new int[]{row,col-1});
                used.add(c);
                if (col == 5) { col = 0; row++; }
            }
        }

        for (char c='A'; c<='Z'; c++) {
            if (c=='J') continue;
            if (!used.contains(c)) {
                table[row][col++] = c;
                pos.put(c, new int[]{row,col-1});
                used.add(c);
                if (col==5){ col=0; row++; }
            }
        }
    }

    static String prepareText(String text, boolean encrypt) {
        if (!encrypt) return text; // For decryption, use as-is
        StringBuilder sb = new StringBuilder();
        int i=0;
        while (i<text.length()) {
            char a = text.charAt(i++);
            char b = (i<text.length()) ? text.charAt(i) : 'X';
            if (a==b) {
                sb.append(a).append('X');
            } else {
                sb.append(a).append(b);
                i++;
            }
        }
        if (sb.length()%2==1) sb.append('X');
        return sb.toString();
    }

    static String encrypt(String text) {
        return process(text, true);
    }

    static String decrypt(String text) {
        return process(text, false);
    }

    static String process(String text, boolean encrypt) {
        StringBuilder result = new StringBuilder();
        int shift = encrypt ? 1 : 4;
        for (int i=0; i<text.length(); i+=2){
            char a = text.charAt(i);
            char b = text.charAt(i+1);
            int[] pa = pos.get(a);
            int[] pb = pos.get(b);

            if (pa[0]==pb[0]){
                result.append(table[pa[0]][(pa[1]+shift)%5]);
                result.append(table[pb[0]][(pb[1]+shift)%5]);
            } else if (pa[1]==pb[1]){
                result.append(table[(pa[0]+shift)%5][pa[1]]);
                result.append(table[(pb[0]+shift)%5][pb[1]]);
            } else {
                result.append(table[pa[0]][pb[1]]);
                result.append(table[pb[0]][pa[1]]);
            }
        }
        return result.toString();
    }
}

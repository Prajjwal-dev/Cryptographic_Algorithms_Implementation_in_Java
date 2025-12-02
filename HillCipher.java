import java.util.*;

/**
 * Hill Cipher (Java) - Input based
 * - User chooses Encrypt (E) or Decrypt (D)
 * - Enter matrix size n (block size)
 * - Enter n*n integers (key matrix) row-wise
 * - Enter plaintext/ciphertext (letters only; non-letters removed)
 * - Uses modulo 26 arithmetic (A=0..Z=25). Pads with 'X' if needed.
 *
 * Note: Key matrix must be invertible modulo 26 to allow decryption.
 */
public class HillCipher {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Encrypt or Decrypt? (E/D): ");
        char choice = sc.next().toString().trim().toUpperCase().charAt(0);

        System.out.print("Enter matrix size n (e.g., 2 or 3): ");
        int n = sc.nextInt();
        int[][] key = new int[n][n];

        System.out.println("Enter key matrix elements (n*n integers) row-wise (e.g., for n=2: a11 a12 a21 a22):");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                key[i][j] = sc.nextInt() % 26;
                if (key[i][j] < 0) key[i][j] += 26;
            }
        }
        sc.nextLine(); // consume newline

        System.out.print("Enter text: ");
        String raw = sc.nextLine();

        // Clean text (letters only) and uppercase
        String text = raw.replaceAll("[^A-Za-z]", "").toUpperCase();

        if (text.length() == 0) {
            System.out.println("No letters found in input text.");
            return;
        }

        if (choice == 'E') {
            String padded = padText(text, n);
            String cipher = encrypt(padded, key, n);
            System.out.println("Encrypted: " + cipher);
        } else if (choice == 'D') {
            // compute inverse of key matrix mod 26
            int det = mod(roundDeterminant(key, 26), 26);
            int detInv = modInverse(det, 26);
            if (detInv == -1) {
                System.out.println("Key matrix is not invertible modulo 26 (determinant " + det + " has no inverse).");
                return;
            }
            int[][] inv = invertMatrixMod(key, detInv, 26);
            // Decrypt: use inverse matrix
            // If ciphertext length not multiple of n, still process but typically ciphertext is block-multiple
            String padded = padText(text, n); // if user pasted cipher missing pad, safe to pad
            String plain = encryptWithMatrix(padded, inv, n); // same multiply code works
            System.out.println("Decrypted (may contain padding 'X'): " + plain);
        } else {
            System.out.println("Invalid choice. Use E or D.");
        }
    }

    // Pad with 'X' to make length multiple of n
    private static String padText(String text, int n) {
        StringBuilder sb = new StringBuilder(text);
        while (sb.length() % n != 0) sb.append('X');
        return sb.toString();
    }

    // Wrapper to call encryption using provided matrix (works for both key and inverse)
    private static String encrypt(String plaintext, int[][] key, int n) {
        return encryptWithMatrix(plaintext, key, n);
    }

    // Multiply block vectors by matrix (matrix * vector) mod 26 and convert to letters
    private static String encryptWithMatrix(String text, int[][] matrix, int n) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < text.length(); i += n) {
            int[] vec = new int[n];
            for (int j = 0; j < n; j++) vec[j] = text.charAt(i + j) - 'A';

            int[] res = multiplyMatrixVector(matrix, vec, n);
            for (int k = 0; k < n; k++) out.append((char) ('A' + mod(res[k], 26)));
        }
        return out.toString();
    }

    // Multiply matrix (n x n) with vector (n) -> vector (n)
    private static int[] multiplyMatrixVector(int[][] mat, int[] vec, int n) {
        int[] res = new int[n];
        for (int i = 0; i < n; i++) {
            long sum = 0;
            for (int j = 0; j < n; j++) {
                sum += (long) mat[i][j] * vec[j];
            }
            res[i] = (int) (sum % 26);
        }
        return res;
    }

    // Compute modular inverse of a modulo m using extended gcd; return -1 if none
    private static int modInverse(int a, int m) {
        a = mod(a, m);
        int[] eg = extendedGCD(a, m);
        int gcd = eg[0], x = eg[1];
        if (gcd != 1) return -1;
        int inv = mod(x, m);
        return inv;
    }

    // Extended GCD: returns [g, x, y] such that a*x + b*y = g
    private static int[] extendedGCD(int a, int b) {
        if (b == 0) return new int[]{a, 1, 0};
        int[] t = extendedGCD(b, a % b);
        int g = t[0], x1 = t[1], y1 = t[2];
        int x = y1;
        int y = x1 - (a / b) * y1;
        return new int[]{g, x, y};
    }

    // Compute determinant modulo base but keep full integer determinant for gcd check; we will use recursive determinant
    private static long roundDeterminant(int[][] mat, int mod) {
        int n = mat.length;
        long det = determinant(mat);
        det %= mod;
        if (det < 0) det += mod;
        return det;
    }

    // Recursive determinant (integer) - acceptable for small n (2,3,4)
    private static long determinant(int[][] mat) {
        int n = mat.length;
        if (n == 1) return mat[0][0];
        if (n == 2) return (long) mat[0][0] * mat[1][1] - (long) mat[0][1] * mat[1][0];

        long det = 0;
        for (int col = 0; col < n; col++) {
            int[][] sub = subMatrix(mat, 0, col);
            long cofactor = ((col % 2 == 0) ? 1 : -1) * mat[0][col] * determinant(sub);
            det += cofactor;
        }
        return det;
    }

    private static int[][] subMatrix(int[][] mat, int skipRow, int skipCol) {
        int n = mat.length;
        int[][] sub = new int[n - 1][n - 1];
        int r = 0;
        for (int i = 0; i < n; i++) {
            if (i == skipRow) continue;
            int c = 0;
            for (int j = 0; j < n; j++) {
                if (j == skipCol) continue;
                sub[r][c++] = mat[i][j];
            }
            r++;
        }
        return sub;
    }

    // Compute inverse matrix modulo m given determinant inverse
    private static int[][] invertMatrixMod(int[][] mat, int detInv, int m) {
        int n = mat.length;
        int[][] cof = new int[n][n];
        // compute cofactor matrix (matrix of cofactors)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int[][] sub = subMatrix(mat, i, j);
                long subDet = determinant(sub);
                long cofactor = ((i + j) % 2 == 0) ? subDet : -subDet;
                cof[i][j] = mod((int) cofactor, m);
            }
        }
        // adjugate = transpose of cofactor
        int[][] adj = transpose(cof);
        // multiply adj by detInv mod m
        int[][] inv = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inv[i][j] = mod(adj[i][j] * detInv, m);
            }
        }
        return inv;
    }

    private static int[][] transpose(int[][] mat) {
        int n = mat.length;
        int[][] t = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                t[i][j] = mat[j][i];
        return t;
    }

    // mod handling for negatives
    private static int mod(int a, int m) {
        int r = a % m;
        if (r < 0) r += m;
        return r;
    }

    // Overloaded for long
    private static int mod(long a, int m) {
        long r = a % m;
        if (r < 0) r += m;
        return (int) r;
    }
}

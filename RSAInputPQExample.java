import java.math.BigInteger;
import java.util.Scanner;

public class RSAInputPQExample {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 1. INPUT p and q
        System.out.print("Enter prime p: ");
        BigInteger p = new BigInteger(sc.nextLine().trim());

        System.out.print("Enter prime q: ");
        BigInteger q = new BigInteger(sc.nextLine().trim());

        // 2. Compute n and phi(n)
        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        // 3. Choose smallest e such that gcd(e, phi) = 1
        BigInteger e = BigInteger.valueOf(3);
        while (e.compareTo(phi) < 0) {
            if (phi.gcd(e).equals(BigInteger.ONE)) {
                break;
            }
            e = e.add(BigInteger.TWO); // try next odd number
        }

        // 4. Compute d = e⁻¹ mod phi
        BigInteger d = e.modInverse(phi);

        // 5. OUTPUT
        System.out.println("\n========== RSA RESULT ==========");
        System.out.println("p = " + p);
        System.out.println("q = " + q);
        System.out.println("n = " + n);
        System.out.println("phi(n) = " + phi);
        System.out.println("Chosen e = " + e);
        System.out.println("Computed d = " + d);
        System.out.println("\nPublic Key  = (" + e + ", " + n + ")");
        System.out.println("Private Key = (" + d + ", " + n + ")");
        System.out.println("================================");
    }
}

import java.io.FileReader;
import java.math.BigInteger;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ShamirSecretSharing {

    // Decode the value from a given base to BigInteger
    public static BigInteger decodeValue(String value, int base) {
        return new BigInteger(value, base);
    }

    // Lagrange Interpolation to find the constant term (c)
    public static BigInteger lagrangeInterpolation(Map<BigInteger, BigInteger> points, int k) {
        BigInteger secret = BigInteger.ZERO;

        for (Map.Entry<BigInteger, BigInteger> i : points.entrySet()) {
            BigInteger xi = i.getKey();
            BigInteger yi = i.getValue();

            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (Map.Entry<BigInteger, BigInteger> j : points.entrySet()) {
                BigInteger xj = j.getKey();
                if (!xi.equals(xj)) {
                    numerator = numerator.multiply(xj.negate());
                    denominator = denominator.multiply(xi.subtract(xj));
                }
            }

            BigInteger li = numerator.divide(denominator); // Lagrange basis polynomial
            secret = secret.add(yi.multiply(li));
        }

        return secret;
    }

    public static Map<BigInteger, BigInteger> parseJson(String filePath) {
        Map<BigInteger, BigInteger> points = new HashMap<>();

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(filePath));
            JSONObject keys = (JSONObject) jsonObject.get("keys");
            int n = ((Long) keys.get("n")).intValue();

            for (int i = 1; i <= n; i++) {
                JSONObject point = (JSONObject) jsonObject.get(String.valueOf(i));
                if (point != null) {
                    int base = Integer.parseInt((String) point.get("base"));
                    String value = (String) point.get("value");
                    points.put(BigInteger.valueOf(i), decodeValue(value, base));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return points;
    }

    public static void main(String[] args) {
        // Test case 1
        String filePath1 = "testcase1.json";
        Map<BigInteger, BigInteger> points1 = parseJson(filePath1);
        int k1 = 3;
        BigInteger secret1 = lagrangeInterpolation(points1, k1);
        System.out.println(secret1); // Print only the constant term (c)

        // Test case 2
        String filePath2 = "testcase2.json";
        Map<BigInteger, BigInteger> points2 = parseJson(filePath2);
        int k2 = 7;
        BigInteger secret2 = lagrangeInterpolation(points2, k2);
        System.out.println(secret2); // Print only the constant term (c)
    }
}
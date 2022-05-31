package gg.dragonfruit.network.encryption;

import java.math.BigInteger;
import java.security.SecureRandom;

import gg.dragonfruit.network.util.BigIntegerCache;

public class EndToEndEncryption {
    BigInteger secretKey;
    BigInteger sharedKey;
    SecureRandom rand = new SecureRandom();

    /**
     * Generates a new private key and uses it to calculate the public key. This key
     * should be sent to the other user and used to decrypt the message once
     * received.
     * 
     * @return a new public key.
     */
    public BigInteger getPublicKey() {

        this.secretKey = new BigInteger(BigIntegerCache.NUMBER_OF_KEYS.bitLength(), rand);

        while (this.secretKey.compareTo(BigIntegerCache.NUMBER_OF_KEYS) >= 0) {
            this.secretKey = new BigInteger(BigIntegerCache.NUMBER_OF_KEYS.bitLength(), rand);
        }

        return BigIntegerCache.SMALL_PRIME.modPow(secretKey, BigIntegerCache.NUMBER_OF_KEYS);
    }

    BigInteger currentNumber;

    private BigInteger nextNumber() {
        String numberToString = currentNumber.toString();
        int firstNumber = Integer.parseInt(numberToString.substring(0, 1));

        switch (firstNumber) {
            case 0:
            case 1:
                currentNumber.add(BigIntegerCache.BIG_INT_5);
            case 2:
                currentNumber.multiply(BigIntegerCache.BIG_INT_7);
            case 3:
                currentNumber.multiply(BigIntegerCache.BIG_INT_6);
            case 4:
                currentNumber.multiply(BigIntegerCache.BIG_INT_5);
            case 5:
                currentNumber.multiply(BigIntegerCache.BIG_INT_4);
            case 6:
                currentNumber.multiply(BigIntegerCache.BIG_INT_3);
            case 7:
                currentNumber.multiply(BigIntegerCache.BIG_INT_2);
            case 8:
                currentNumber.divide(BigIntegerCache.BIG_INT_6);
            case 9:
                currentNumber.divide(BigIntegerCache.BIG_INT_7);
        }

        return currentNumber = currentNumber.abs();
    }

    private String offsetMap(int totalLength) {
        StringBuilder sb = new StringBuilder();

        while (sb.length() < totalLength) {
            sb.append(nextNumber().toString());
        }

        return sb.toString();
    }

    /**
     * Encrypts the given string.
     *
     * @param string         the string to be encrypted.
     * @param otherPublicKey the other user's public key.
     * @return the encrypted string.
     */
    public String encrypt(String string, BigInteger otherPublicKey) {
        this.sharedKey = otherPublicKey.modPow(secretKey, BigIntegerCache.NUMBER_OF_KEYS);
        this.currentNumber = sharedKey;
        String offsetMap = offsetMap(string.length());

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < string.length(); i++) {
            sb.append((char) (string.charAt(i) + Integer.parseInt(new String(new char[] { offsetMap.charAt(i) }))));
        }

        return sb.toString();
    }

    /**
     * Decrypts the given string.
     *
     * @param string         the string to be decrypted.
     * @param otherPublicKey the other user's public key.
     * @return the decrypted string.
     */
    public String decrypt(String string, BigInteger otherPublicKey) {
        this.sharedKey = otherPublicKey.modPow(secretKey, BigIntegerCache.NUMBER_OF_KEYS);
        this.currentNumber = sharedKey;
        String offsetMap = offsetMap(string.length());

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < string.length(); i++) {
            sb.append((char) (string.charAt(i) - Integer.parseInt(new String(new char[] { offsetMap.charAt(i) }))));
        }

        return sb.toString();
    }

    /**
     * Encrypts the given strings in order.
     *
     * @param s              the strings to be encrypted.
     * @param otherPublicKey the other user's public key.
     * @return the encrypted strings.
     */
    public String[] encrypt(BigInteger otherPublicKey, String... s) {
        this.sharedKey = otherPublicKey.modPow(secretKey, BigIntegerCache.NUMBER_OF_KEYS);
        this.currentNumber = sharedKey;
        int totalLength = 0;

        for (String string : s) {
            totalLength += string.length();
        }

        String offsetMap = offsetMap(totalLength);
        String[] result = new String[s.length];

        int o = 0;
        for (int p = 0; p < s.length; p++) {
            String string = s[p];
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < string.length(); i++) {
                sb.append((char) (string.charAt(i) + Integer.parseInt(new String(new char[] { offsetMap.charAt(o) }))));
                o++;
            }

            result[p] = sb.toString();
        }

        return result;
    }

    /**
     * Decrypts the given strings in order.
     *
     * @param s              the strings to be decrypted.
     * @param otherPublicKey the other user's public key.
     * @return the decrypted strings.
     */
    public String[] decrypt(BigInteger otherPublicKey, String... s) {
        this.sharedKey = otherPublicKey.modPow(secretKey, BigIntegerCache.NUMBER_OF_KEYS);
        this.currentNumber = sharedKey;
        int totalLength = 0;

        for (String string : s) {
            totalLength += string.length();
        }

        String offsetMap = offsetMap(totalLength);
        String[] result = new String[s.length];

        int o = 0;
        for (int p = 0; p < s.length; p++) {
            String string = s[p];
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < string.length(); i++) {
                sb.append((char) (string.charAt(i) - Integer.parseInt(new String(new char[] { offsetMap.charAt(o) }))));
                o++;
            }

            result[p] = sb.toString();
        }

        return result;
    }
}

package gg.dragonfruit.network.encryption;

import java.math.BigInteger;
import java.security.SecureRandom;

import gg.dragonfruit.network.util.BigIntegerCache;

public class EndToEndEncryption {
    BigInteger secretKey;
    BigInteger sharedKey;
    BigInteger numberOfKeys = null;
    BigInteger otherPublicKey;
    SecureRandom rand = new SecureRandom();

    public void setNumberOfKeys(BigInteger numberOfKeys) {
        this.numberOfKeys = numberOfKeys;
    }

    /**
     * Sets the other user's public key and creates a shared key.
     * 
     * @param otherPublicKey the other user's public key.
     */
    public void setOtherPublicKey(BigInteger otherPublicKey) {
        this.otherPublicKey = otherPublicKey;
    }

    public void setSharedKey() {
        this.sharedKey = this.otherPublicKey.modPow(secretKey, numberOfKeys);
    }

    public boolean needsKeyExchange() {
        return this.otherPublicKey == null;
    }

    /**
     * Generates a new private key and uses it to calculate the public key. This key
     * should be sent to the other user and used to decrypt the message once
     * received.
     * 
     * @return a new public key.
     */
    public BigInteger getPublicKey() {

        this.secretKey = new BigInteger(numberOfKeys.bitLength(), rand);

        while (this.secretKey.compareTo(numberOfKeys) >= 0) {
            this.secretKey = new BigInteger(numberOfKeys.bitLength(), rand);
        }

        if (!needsKeyExchange()) {
            setSharedKey();
        }

        return BigIntegerCache.SMALL_PRIME.modPow(secretKey, numberOfKeys);
    }

    /**
     * Encrypts the given string.
     *
     * @param string         the string to be encrypted.
     * @param otherPublicKey the other user's public key.
     * @return the encrypted string.
     */
    public String encrypt(String string) {
        return new BigInteger(string.getBytes())
                .multiply(this.sharedKey).toString();
    }

    /**
     * Decrypts the given string.
     *
     * @param string         the string to be decrypted.
     * @param otherPublicKey the other user's public key.
     * @return the decrypted string.
     */
    public String decrypt(String string) {
        return new String(new BigInteger(string).divide(this.sharedKey)
                .toByteArray());
    }

    /**
     * Encrypts the given strings in order.
     *
     * @param s              the strings to be encrypted.
     * @param otherPublicKey the other user's public key.
     * @return the encrypted strings.
     */
    public String[] encrypt(String... s) {
        String[] result = new String[s.length];

        BigInteger currentKey = this.sharedKey;

        for (int i = 0; i < s.length; i++) {
            result[i] = new BigInteger(s[i].getBytes())
                    .multiply(currentKey = BigIntegerCache.SMALL_PRIME.modPow(currentKey, numberOfKeys)).toString();
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
    public String[] decrypt(String... s) {
        String[] result = new String[s.length];

        BigInteger currentKey = this.sharedKey;

        for (int i = 0; i < s.length; i++) {
            result[i] = new String(new BigInteger(s[i])
                    .divide(currentKey = BigIntegerCache.SMALL_PRIME.modPow(currentKey, numberOfKeys)).toByteArray());
        }

        return result;
    }

    public BigInteger getNumberOfKeys() {
        return this.numberOfKeys;
    }
}

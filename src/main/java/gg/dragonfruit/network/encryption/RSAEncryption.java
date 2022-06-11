package gg.dragonfruit.network.encryption;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSAEncryption {

    static KeyPairGenerator generator;

    static {
        try {
            generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(6144);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static KeyPair generateKeyPair() {
        return generator.generateKeyPair();
    }

    public static String encrypt(String string, PublicKey otherPublicKey)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, otherPublicKey);
        byte[] secretMessageBytes = string.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(encryptCipher.doFinal(secretMessageBytes));
    }

    public static String decrypt(String string, PrivateKey privateKey)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] secretMessageBytes = string.getBytes(StandardCharsets.UTF_8);
        return new String(encryptCipher.doFinal(secretMessageBytes), StandardCharsets.UTF_8);
    }
}

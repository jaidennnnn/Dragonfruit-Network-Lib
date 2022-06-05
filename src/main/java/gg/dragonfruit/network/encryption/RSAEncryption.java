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

import gg.dragonfruit.network.Connection;

public class RSAEncryption {

    public static PrivateKey PRIVATE_KEY;
    public static PublicKey PUBLIC_KEY;

    public static void init() {

        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(6144);
            KeyPair pair = generator.generateKeyPair();
            PRIVATE_KEY = pair.getPrivate();
            PUBLIC_KEY = pair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String encryptForVerification(String string)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, PRIVATE_KEY);
        byte[] secretMessageBytes = string.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(encryptCipher.doFinal(secretMessageBytes));
    }

    public static String encrypt(String string, Connection connection)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, connection.getRSAPublicKey());
        byte[] secretMessageBytes = string.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(encryptCipher.doFinal(secretMessageBytes));
    }

    public static String decryptForVerification(String string, Connection connection)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.DECRYPT_MODE, connection.getRSAPublicKey());
        byte[] secretMessageBytes = string.getBytes(StandardCharsets.UTF_8);
        return new String(encryptCipher.doFinal(secretMessageBytes), StandardCharsets.UTF_8);
    }

    public static String decrypt(String string)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.DECRYPT_MODE, PRIVATE_KEY);
        byte[] secretMessageBytes = string.getBytes(StandardCharsets.UTF_8);
        return new String(encryptCipher.doFinal(secretMessageBytes), StandardCharsets.UTF_8);
    }
}

package gg.dragonfruit.network.encryption;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    public static byte[] encrypt(byte[] data, PublicKey otherPublicKey)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, otherPublicKey);
        return encryptCipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, PrivateKey privateKey)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        return encryptCipher.doFinal(data);
    }

    public static String encrypt(String string, PublicKey otherPublicKey)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        return Base64.getEncoder().encodeToString(encrypt(string.getBytes(StandardCharsets.UTF_8), otherPublicKey));
    }

    public static String decrypt(String string, PrivateKey privateKey)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        return new String(decrypt(string.getBytes(StandardCharsets.UTF_8), privateKey), StandardCharsets.UTF_8);
    }

    public static byte[] serializePublicKey(PublicKey publicKey) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(publicKey);
        byte[] data = out.toByteArray();
        os.close();
        out.close();
        return data;
    }

    public static PublicKey deserializePublicKey(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        PublicKey publicKey = (PublicKey) is.readObject();
        is.close();
        return publicKey;
    }
}

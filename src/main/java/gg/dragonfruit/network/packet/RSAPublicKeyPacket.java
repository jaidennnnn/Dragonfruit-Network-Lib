package gg.dragonfruit.network.packet;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import gg.dragonfruit.network.ClientHandler;
import gg.dragonfruit.network.Connection;
import gg.dragonfruit.network.ServerConnection;
import gg.dragonfruit.network.encryption.RSAEncryption;

public class RSAPublicKeyPacket extends Packet {

    byte[] publicKey;
    boolean encrypted = false;
    boolean respond;

    public RSAPublicKeyPacket(PublicKey publicKey, PublicKey otherPublicKey, boolean respond) {
        try {
            this.publicKey = RSAEncryption.serializePublicKey(publicKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.respond = respond;

        if (otherPublicKey != null) {
            try {
                this.publicKey = RSAEncryption.encrypt(this.publicKey, otherPublicKey);
                encrypted = true;
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                    | BadPaddingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void received(Connection connection) {
        boolean isFromServer = connection instanceof ServerConnection;
        if (encrypted) {
            try {
                this.publicKey = RSAEncryption.decrypt(this.publicKey,
                        isFromServer ? ClientHandler.RSA_PRIVATE_KEY : connection.getRSAPrivateKey());
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                    | BadPaddingException e) {
                e.printStackTrace();
                return;
            }
        }

        PublicKey publicKeyObj;
        try {
            publicKeyObj = RSAEncryption.deserializePublicKey(this.publicKey);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return;
        }

        connection.setRSAPublicKey(publicKeyObj);
        if (respond) {
            connection.sendPacket(new RSAPublicKeyPacket(
                    isFromServer ? ClientHandler.getNewRSAPublicKey()
                            : connection.getNewRSAPublicKey(),
                    null,
                    false));
        }
    }

}

package gg.dragonfruit.network.packet;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import gg.dragonfruit.network.Connection;
import gg.dragonfruit.network.encryption.RSAEncryption;

public class RequestVerificationPacket extends Packet {

    PublicKey decryptionKey;
    String stringToEncrypt;

    public RequestVerificationPacket(String stringToEncrypt, PublicKey decryptionKey) {
        this.stringToEncrypt = stringToEncrypt;
        this.decryptionKey = decryptionKey;
    }

    @Override
    public void received(Connection connection) {
        connection.setRSAPublicKey(decryptionKey);
        try {
            connection.sendPacket(new VerificationPacket(RSAEncryption.encryptForVerification(stringToEncrypt)));
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException e) {
            e.printStackTrace();
        }
    }

}

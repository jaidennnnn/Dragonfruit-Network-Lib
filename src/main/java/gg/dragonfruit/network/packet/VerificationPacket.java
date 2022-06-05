package gg.dragonfruit.network.packet;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import gg.dragonfruit.network.Connection;
import gg.dragonfruit.network.encryption.RSAEncryption;

public class VerificationPacket extends Packet {

    String encryptedString;

    public VerificationPacket(String encryptedString) {
        this.encryptedString = encryptedString;
    }

    @Override
    public void received(Connection connection) {
        try {
            String decrypted = RSAEncryption.decryptForVerification(encryptedString, connection);

            if (decrypted.equalsIgnoreCase(connection.getStringForVerification())) {
                connection.secureTunnelEstablished();
            }
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException e) {
            e.printStackTrace();
        }
    }

}

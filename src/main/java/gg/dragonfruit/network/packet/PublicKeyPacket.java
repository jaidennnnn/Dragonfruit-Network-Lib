package gg.dragonfruit.network.packet;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import gg.dragonfruit.network.Connection;
import gg.dragonfruit.network.NetworkLibrary;
import gg.dragonfruit.network.encryption.RSAEncryption;

public class PublicKeyPacket extends Packet {

    String publicKeyStr;
    boolean respond;

    public PublicKeyPacket(BigInteger publicKey, boolean respond, Connection recipient) {
        try {
            this.publicKeyStr = RSAEncryption.encrypt(publicKey.toString(), recipient);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException e) {
            e.printStackTrace();
        }
        this.respond = respond;
    }

    @Override
    public void received(Connection connection) {
        try {
            connection.setPublicKey(new BigInteger(RSAEncryption.decrypt(publicKeyStr)));
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException e) {
            e.printStackTrace();
        }
        connection.receivedPublicKey();

        if (respond) {
            connection.sendPacket(new PublicKeyPacket(
                    NetworkLibrary.getPacketTransmitter().getSelfEndToEndEncryption().getPublicKey(),
                    false, connection));
        }
    }

}

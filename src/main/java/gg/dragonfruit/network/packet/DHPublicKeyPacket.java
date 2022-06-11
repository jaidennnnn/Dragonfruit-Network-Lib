package gg.dragonfruit.network.packet;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import gg.dragonfruit.network.Connection;
import gg.dragonfruit.network.PacketTransmitter;
import gg.dragonfruit.network.encryption.RSAEncryption;

public class DHPublicKeyPacket extends RSAEncryptedPacket {

    String publicKeyStr;
    boolean respond;

    public DHPublicKeyPacket(BigInteger publicKey, boolean respond, Connection recipient) {
        this.publicKeyStr = publicKey.toString();
        this.respond = respond;
    }

    @Override
    public void received(Connection connection) {
        connection
                .setDHPublicKey(new BigInteger(publicKeyStr));

        if (respond) {
            connection.sendPacket(new DHPublicKeyPacket(
                    PacketTransmitter.getSelfEndToEndEncryption(connection.getNumberOfKeys()).getPublicKey(),
                    false, connection));
        }
    }

    @Override
    public void encrypt(PublicKey otherPublicKey) {
        try {
            this.publicKeyStr = RSAEncryption.encrypt(this.publicKeyStr, otherPublicKey);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decrypt(PrivateKey privateKey) {
        try {
            this.publicKeyStr = RSAEncryption.decrypt(this.publicKeyStr, privateKey);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException e) {
            e.printStackTrace();
        }
    }

}

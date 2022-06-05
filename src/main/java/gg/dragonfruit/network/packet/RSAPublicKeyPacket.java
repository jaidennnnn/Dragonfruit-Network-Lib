package gg.dragonfruit.network.packet;

import java.security.PublicKey;

import gg.dragonfruit.network.Connection;
import gg.dragonfruit.network.encryption.RSAEncryption;

public class RSAPublicKeyPacket extends Packet {
    PublicKey decryptionKey;

    public RSAPublicKeyPacket(PublicKey decryptionKey) {
        this.decryptionKey = decryptionKey;
    }

    @Override
    public void received(Connection connection) {
        connection.setRSAPublicKey(decryptionKey);
        connection.randomStr();
        connection.sendPacket(
                new RequestVerificationPacket(connection.getStringForVerification(), RSAEncryption.PUBLIC_KEY));
    }
}

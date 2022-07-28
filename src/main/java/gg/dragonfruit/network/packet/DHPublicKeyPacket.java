package gg.dragonfruit.network.packet;

import java.math.BigInteger;

import gg.dragonfruit.network.Connection;

public class DHPublicKeyPacket extends Packet {

    byte[] publicKeyBytes;

    public DHPublicKeyPacket(BigInteger publicKey) {
        this.publicKeyBytes = publicKey.toByteArray();
    }

    @Override
    public void received(Connection connection) {
        System.out.println("packet received");
        connection.setOtherPublicKey(new BigInteger(publicKeyBytes));
        System.out.println("packet processed");
    }
}

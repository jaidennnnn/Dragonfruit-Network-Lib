package gg.dragonfruit.network.packet;

import java.math.BigInteger;

import gg.dragonfruit.network.Connection;

public class DHPublicKeyPacket extends Packet {

    byte[] publicKeyBytes;

    public DHPublicKeyPacket(BigInteger publicKey) {
        this.publicKeyBytes = publicKey.toByteArray();
        System.out.println("Packet init");
    }

    @Override
    public void received(Connection connection) {
        connection.setOtherPublicKey(new BigInteger(publicKeyBytes));
    }
}

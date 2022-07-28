package gg.dragonfruit.network.packet;

import java.math.BigInteger;

import gg.dragonfruit.network.Connection;

public class DHPublicKeyPacket extends Packet {

    String publicKeyStr;

    public DHPublicKeyPacket(BigInteger publicKey) {
        this.publicKeyStr = publicKey.toString();
    }

    @Override
    public void received(Connection connection) {
        System.out.println("recieved");
        connection.setOtherPublicKey(new BigInteger(publicKeyStr));
    }
}

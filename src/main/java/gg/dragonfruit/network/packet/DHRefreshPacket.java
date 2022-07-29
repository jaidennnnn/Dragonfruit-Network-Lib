package gg.dragonfruit.network.packet;

import java.math.BigInteger;

import gg.dragonfruit.network.Connection;

public class DHRefreshPacket extends Packet {

    byte[] publicKeyStr;

    public DHRefreshPacket(BigInteger publicKey) {
        this.publicKeyStr = publicKey.toByteArray();
    }

    @Override
    public void received(Connection connection) {
        connection.setOtherPublicKey(new BigInteger(publicKeyStr));
        connection.sendPacket(new DHKeyReceivedPacket());
    }

}

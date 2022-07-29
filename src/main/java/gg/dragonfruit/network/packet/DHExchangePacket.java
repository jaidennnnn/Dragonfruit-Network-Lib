package gg.dragonfruit.network.packet;

import java.math.BigInteger;

import gg.dragonfruit.network.Connection;

public class DHExchangePacket extends Packet {

    byte[] publicKeyBytes;

    public DHExchangePacket(BigInteger publicKey) {
        this.publicKeyBytes = publicKey.toByteArray();
    }

    @Override
    public void received(Connection connection) {
        connection.sendPacket(new DHPublicKeyPacket(
                connection.getSelfEndToEndEncryption().getPublicKey()));
        connection.setOtherPublicKey(new BigInteger(publicKeyBytes));
    }

}

package gg.dragonfruit.network.packet;

import java.math.BigInteger;

import gg.dragonfruit.network.Connection;

public class DHInitPacket extends Packet {
    byte[] numberOfKeysBytes;
    byte[] publicKeyBytes;

    public DHInitPacket(BigInteger numberOfKeys, BigInteger publicKey) {
        this.numberOfKeysBytes = numberOfKeys.toByteArray();
        this.publicKeyBytes = publicKey.toByteArray();
    }

    @Override
    public void received(Connection connection) {
        connection.getSelfEndToEndEncryption().setNumberOfKeys(new BigInteger(numberOfKeysBytes));
        connection.sendPacket(new DHPublicKeyPacket(
                connection.getSelfEndToEndEncryption().getPublicKey()));
        connection.setOtherPublicKey(new BigInteger(publicKeyBytes));
    }

}

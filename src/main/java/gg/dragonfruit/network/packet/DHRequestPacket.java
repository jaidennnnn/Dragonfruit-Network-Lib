package gg.dragonfruit.network.packet;

import java.math.BigInteger;

import gg.dragonfruit.network.Connection;

public class DHRequestPacket extends Packet {

    byte[] numberOfKeysBytes;
    byte[] publicKeyBytes;

    public DHRequestPacket(BigInteger numberOfKeys, BigInteger publicKey) {
        this.numberOfKeysBytes = numberOfKeys.toByteArray();
        this.publicKeyBytes = publicKey.toByteArray();
    }

    @Override
    public void received(Connection connection) {
        System.out.println("0");
        connection.getSelfEndToEndEncryption().setNumberOfKeys(new BigInteger(numberOfKeysBytes));
        System.out.println("1");
        connection.sendPacket(new DHPublicKeyPacket(
                connection.getSelfEndToEndEncryption().getPublicKey()));
        System.out.println("2");
        connection.setOtherPublicKey(new BigInteger(publicKeyBytes));
        System.out.println("DHRequestPacket");
    }

}

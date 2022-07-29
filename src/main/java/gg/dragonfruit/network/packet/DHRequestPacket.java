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
        try {
            connection.getSelfEndToEndEncryption().setNumberOfKeys(new BigInteger(numberOfKeysBytes));
            System.out.println("1");
            connection.sendPacket(new DHPublicKeyPacket(
                    connection.getSelfEndToEndEncryption().getPublicKey()));
            System.out.println("2");
            connection.setOtherPublicKey(new BigInteger(publicKeyBytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("DHRequestPacket");
    }

}

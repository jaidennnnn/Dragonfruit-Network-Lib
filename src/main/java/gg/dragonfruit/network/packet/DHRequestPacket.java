package gg.dragonfruit.network.packet;

import java.math.BigInteger;

import gg.dragonfruit.network.Connection;

public class DHRequestPacket extends Packet {

    byte[] numberOfKeysByte;

    public DHRequestPacket(BigInteger numberOfKeys) {
        this.numberOfKeysByte = numberOfKeys.toByteArray();
    }

    @Override
    public void received(Connection connection) {
        connection.getSelfEndToEndEncryption().setNumberOfKeys(new BigInteger(numberOfKeysByte));
        connection.sendPacket(new DHPublicKeyPacket(
                connection.getSelfEndToEndEncryption().getPublicKey()));

    }

}

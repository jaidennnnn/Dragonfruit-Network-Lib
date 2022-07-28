package gg.dragonfruit.network.packet;

import gg.dragonfruit.network.Connection;

public class DHRequestPacket extends Packet {

    @Override
    public void received(Connection connection) {
        System.out.println("DH key requested");
        connection.sendPacket(new DHPublicKeyPacket(
                connection.getSelfEndToEndEncryption().getPublicKey()));
    }

}

package gg.dragonfruit.network.packet;

import gg.dragonfruit.network.Connection;

public class DHRequestKeyPacket extends Packet {

    @Override
    public void received(Connection connection) {
        connection.exchangeDHPublicKeys();
    }

}

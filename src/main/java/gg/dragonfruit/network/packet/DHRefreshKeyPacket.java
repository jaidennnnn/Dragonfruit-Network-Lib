package gg.dragonfruit.network.packet;

import gg.dragonfruit.network.Connection;

public class DHRefreshKeyPacket extends Packet {

    @Override
    public void received(Connection connection) {
        connection.requestDHPublicKey();
    }

}

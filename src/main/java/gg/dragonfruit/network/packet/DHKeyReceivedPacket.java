package gg.dragonfruit.network.packet;

import gg.dragonfruit.network.Connection;

public class DHKeyReceivedPacket extends Packet {

    @Override
    public void received(Connection connection) {
        connection.refreshedPublicKey();
    }

}

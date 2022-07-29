package gg.dragonfruit.network.packet;

import gg.dragonfruit.network.Connection;

public class DHRequestRefreshPacket extends Packet {

    @Override
    public void received(Connection connection) {
        connection.refreshDHSharedKey();
    }

}

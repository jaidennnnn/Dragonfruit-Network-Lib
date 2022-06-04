package gg.dragonfruit.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.snf4j.core.handler.AbstractDatagramHandler;

import gg.dragonfruit.network.packet.EncryptedPacket;
import gg.dragonfruit.network.packet.Packet;
import gg.dragonfruit.network.util.PacketUtil;

public class PacketHandler extends AbstractDatagramHandler {

    @Override
    public void read(SocketAddress socketAddress, Object obj) {

    }

    @Override
    public void read(Object obj) {

        SocketAddress socketAddress = this.getSession().getRemoteAddress();

        if (!(socketAddress instanceof InetSocketAddress)) {
            return;
        }

        InetSocketAddress iNetSocketAddress = (java.net.InetSocketAddress) socketAddress;
        Connection connection = NetworkLibrary.getConnections().get(iNetSocketAddress.getAddress(),
                iNetSocketAddress.getPort());

        if (connection == null) {
            return;
        }

        connection.setSession(this.getSession());

        byte[] data = (byte[]) obj;

        Packet received;
        try {
            received = PacketUtil.deserializePacket(data);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return;
        }

        if (received instanceof EncryptedPacket) {
            EncryptedPacket encryptedPacket = (EncryptedPacket) received;
            encryptedPacket.decrypt(NetworkLibrary.getPacketTransmitter().getConnection().getEndToEndEncryption(),
                    connection.getPublicKey());
        }

        received.received(connection);
    }
}

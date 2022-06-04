package gg.dragonfruit.network;

import java.io.IOException;
import java.net.SocketAddress;

import org.snf4j.core.handler.AbstractDatagramHandler;

import gg.dragonfruit.network.packet.EncryptedPacket;
import gg.dragonfruit.network.packet.Packet;
import gg.dragonfruit.network.util.PacketUtil;

public class ServerHandler extends AbstractDatagramHandler {

    @Override
    public void read(SocketAddress socketAddress, Object obj) {

    }

    @Override
    public void read(Object obj) {

        NetworkLibrary.getConnections().getOrCreate(this.getSession()).whenComplete((connection, ex) -> {
            if (connection == null) {
                return;
            }

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
                encryptedPacket.decrypt(
                        NetworkLibrary.getPacketTransmitter().getServerConnection().getEndToEndEncryption(),
                        connection.getPublicKey());
            }

            received.received(connection);
        });

    }
}

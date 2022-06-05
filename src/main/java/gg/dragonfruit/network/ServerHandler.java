package gg.dragonfruit.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import org.snf4j.core.handler.AbstractDatagramHandler;
import org.snf4j.core.handler.SessionEvent;

import gg.dragonfruit.network.encryption.RSAEncryption;
import gg.dragonfruit.network.packet.EncryptedPacket;
import gg.dragonfruit.network.packet.Packet;
import gg.dragonfruit.network.packet.PublicKeyPacket;
import gg.dragonfruit.network.packet.RSAPublicKeyPacket;
import gg.dragonfruit.network.util.PacketUtil;

public class ServerHandler extends AbstractDatagramHandler {

    @Override
    public void read(SocketAddress socketAddress, Object obj) {

    }

    @Override
    public void event(SessionEvent event) {
        if (event == SessionEvent.ENDING) {
            SocketAddress socketAddress = this.getSession().getRemoteAddress();

            if (!(socketAddress instanceof InetSocketAddress)) {
                return;
            }

            InetSocketAddress iNetSocketAddress = (java.net.InetSocketAddress) socketAddress;

            try {
                NetworkLibrary.getConnections().disconnect(iNetSocketAddress.getAddress(), iNetSocketAddress.getPort());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } else if (event == SessionEvent.OPENED) {
            Connection connection = NetworkLibrary.getConnections().getOrCreate(this.getSession());
            PacketTransmitter packetTransmitter = NetworkLibrary.getPacketTransmitter();
            packetTransmitter.sendPacket(new RSAPublicKeyPacket(RSAEncryption.PUBLIC_KEY),
                    connection);
        }
    }

    @Override
    public void read(Object obj) {

        Connection connection = NetworkLibrary.getConnections().getOrCreate(this.getSession());

        byte[] data = (byte[]) obj;

        Packet received;
        try {
            received = PacketUtil.deserializePacket(data);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return;
        }

        if (received instanceof PublicKeyPacket) {
            connection.awaitInitializationAsync().whenComplete((func, ex) -> {
                received.received(connection);
            });
        }

        if (received instanceof EncryptedPacket) {
            connection.awaitInitializationAsync().whenComplete((func, ex) -> {
                EncryptedPacket encryptedPacket = (EncryptedPacket) received;
                encryptedPacket.decrypt(
                        NetworkLibrary.getPacketTransmitter().getSelfEndToEndEncryption(),
                        connection.getPublicKey());
                received.received(connection);
            });
            return;
        }

        received.received(connection);
    }
}

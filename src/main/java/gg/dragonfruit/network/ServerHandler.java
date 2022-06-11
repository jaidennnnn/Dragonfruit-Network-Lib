package gg.dragonfruit.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import org.snf4j.core.handler.AbstractDatagramHandler;
import org.snf4j.core.handler.SessionEvent;

import gg.dragonfruit.network.collection.ClientConnectionList;
import gg.dragonfruit.network.packet.DHEncryptedPacket;
import gg.dragonfruit.network.packet.DHPublicKeyPacket;
import gg.dragonfruit.network.packet.Packet;
import gg.dragonfruit.network.packet.RSAEncryptedPacket;
import gg.dragonfruit.network.util.PacketUtil;

public class ServerHandler extends AbstractDatagramHandler {

    static ClientConnectionList connected = new ClientConnectionList();

    public static ClientConnectionList getConnections() {
        return connected;
    }

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
                connected.disconnect(iNetSocketAddress.getAddress(), iNetSocketAddress.getPort());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void read(Object obj) {

        ClientConnection connection = connected.getOrCreate(this.getSession());

        byte[] data = (byte[]) obj;

        Packet received;
        try {
            received = PacketUtil.deserializePacket(data);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return;
        }

        if (received instanceof DHEncryptedPacket) {
            connection.awaitKeyNumber().whenComplete((func, ex) -> {
                DHEncryptedPacket encryptedPacket = (DHEncryptedPacket) received;
                encryptedPacket.decrypt(
                        PacketTransmitter.getSelfEndToEndEncryption(connection.getNumberOfKeys()),
                        connection.getDHPublicKey());
                received.received(connection);
            });
            return;
        }

        if (received instanceof RSAEncryptedPacket) {
            RSAEncryptedPacket encryptedPacket = (RSAEncryptedPacket) received;
            encryptedPacket.decrypt(connection.getRSAPrivateKey());
        }

        if (received instanceof DHPublicKeyPacket) {
            connection.awaitKeyNumber().whenComplete((func, ex) -> {
                received.received(connection);
            });
        }

        received.received(connection);
    }
}

package gg.dragonfruit.network.collection;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.snf4j.core.session.IDatagramSession;

import gg.dragonfruit.network.Connection;
import gg.dragonfruit.network.NetworkLibrary;
import gg.dragonfruit.network.listener.ConnectionListener;
import gg.dragonfruit.network.packet.PublicKeyPacket;

public class ConnectionList extends ConcurrentLinkedQueue<Connection> {

    public CompletableFuture<Connection> getOrCreate(IDatagramSession session) {
        return CompletableFuture.supplyAsync(() -> {
            SocketAddress socketAddress = session.getRemoteAddress();

            if (!(socketAddress instanceof InetSocketAddress)) {
                return null;
            }

            InetSocketAddress iNetSocketAddress = (java.net.InetSocketAddress) socketAddress;

            for (Connection c : this) {
                if (c.getAddress().equals(iNetSocketAddress.getAddress())
                        && c.getPort() == iNetSocketAddress.getPort()) {
                    return c;
                }
            }

            Connection connection = new Connection(iNetSocketAddress.getAddress(), iNetSocketAddress.getPort(),
                    session);
            BigInteger publicKey = NetworkLibrary.getPacketTransmitter().getServerConnection().newPublicKey();
            connection.sendPacket(new PublicKeyPacket(publicKey, true));
            this.add(connection);
            connection.awaitPublicKey();

            for (ConnectionListener listener : ConnectionListener.getListeners()) {
                listener.connected(connection);
            }

            return connection;
        });
    }

    public void disconnect(Connection connection) throws UnknownHostException {
        disconnect(connection.getAddress(), connection.getPort());
    }

    public void disconnect(InetAddress address, int port) throws UnknownHostException {
        for (Connection c : this) {
            if (c.getAddress().equals(address) && c.getPort() == port) {
                for (ConnectionListener listener : ConnectionListener.getListeners()) {
                    listener.disconnected(c);
                }

                this.remove(c);
                break;
            }
        }
    }
}

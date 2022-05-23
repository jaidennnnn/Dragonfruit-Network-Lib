package gg.dragonfruit.network.collection;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

import gg.dragonfruit.network.Connection;
import gg.dragonfruit.network.NetworkLibrary;
import gg.dragonfruit.network.listener.ConnectionListener;
import gg.dragonfruit.network.packet.PublicKeyPacket;

public class ConnectionList extends ConcurrentLinkedQueue<Connection> {

    public CompletableFuture<Connection> getAndCheck(InetAddress address, int port) {
        return CompletableFuture.supplyAsync(() -> {
            for (Connection c : this) {
                if (c.getAddress().equals(address) && c.getPort() == port) {
                    if (!c.checkConnection()) {
                        this.remove(c);
                        return null;
                    }

                    return c;
                }
            }

            return null;
        });
    }

    public Connection get(InetAddress address, int port) {
        for (Connection c : this) {
            if (c.getAddress().equals(address) && c.getPort() == port) {
                return c;
            }
        }

        return null;
    }

    public CompletableFuture<Connection> connect(InetAddress address, int port) {
        return CompletableFuture.supplyAsync(() -> {
            Connection connection = new Connection(address, port);
            BigInteger publicKey = NetworkLibrary.getPacketTransmitter().getConnection().newPublicKey();
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
        for (Connection c : this) {
            if (c.getAddress().equals(connection.getAddress()) && c.getPort() == connection.getPort()) {
                for (ConnectionListener listener : ConnectionListener.getListeners()) {
                    listener.disconnected(connection);
                }

                this.remove(connection);
                break;
            }
        }
    }
}

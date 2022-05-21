package gg.dragonfruit.network.collection;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentLinkedQueue;

import gg.dragonfruit.network.Connection;
import gg.dragonfruit.network.NetworkLibrary;
import gg.dragonfruit.network.packet.KeepAlivePacket;

public class ConnectionList extends ConcurrentLinkedQueue<Connection> {

    public Connection get(InetAddress address, int port) {

        for (Connection c : this) {
            if (c.getAddress().equals(address) && c.getPort() == port) {
                if (c.timedOut()) {
                    this.remove(c);
                    return null;
                }

                return c;
            }
        }

        return null;
    }

    public Connection connect(InetAddress address, int port) {
        Connection connection = new Connection(address, port);
        BigInteger publicKey = NetworkLibrary.getPacketTransmitter().getConnection().newPublicKey();
        connection.sendPacket(new KeepAlivePacket(publicKey, true));
        this.add(connection);
        return connection;
    }

    public void disconnect(Connection connection) {
        for (Connection c : this) {
            if (c.getAddress().equals(connection.getAddress()) && c.getPort() == connection.getPort()) {
                this.remove(connection);
                break;
            }
        }
    }
}

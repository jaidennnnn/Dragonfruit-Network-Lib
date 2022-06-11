package gg.dragonfruit.network.collection;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.snf4j.core.session.IDatagramSession;

import gg.dragonfruit.network.ClientConnection;
import gg.dragonfruit.network.listener.ConnectionListener;

public class ClientConnectionList extends ConcurrentLinkedQueue<ClientConnection> {

    public ClientConnection getOrCreate(IDatagramSession session) {

        SocketAddress socketAddress = session.getRemoteAddress();

        if (!(socketAddress instanceof InetSocketAddress)) {
            return null;
        }

        InetSocketAddress iNetSocketAddress = (java.net.InetSocketAddress) socketAddress;

        for (ClientConnection c : this) {
            if (c.getAddress().equals(iNetSocketAddress.getAddress())
                    && c.getPort() == iNetSocketAddress.getPort()) {
                return c;
            }
        }

        ClientConnection connection = new ClientConnection(iNetSocketAddress.getAddress(), iNetSocketAddress.getPort(),
                session);
        this.add(connection);

        for (ConnectionListener listener : ConnectionListener.getListeners()) {
            listener.connected(connection);
        }

        return connection;
    }

    public void disconnect(ClientConnection connection) throws UnknownHostException {
        disconnect(connection.getAddress(), connection.getPort());
    }

    public void disconnect(InetAddress address, int port) throws UnknownHostException {
        for (ClientConnection c : this) {
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

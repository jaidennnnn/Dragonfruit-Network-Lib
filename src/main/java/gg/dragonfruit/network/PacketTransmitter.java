package gg.dragonfruit.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;

import org.snf4j.core.DatagramServerHandler;
import org.snf4j.core.SelectorLoop;
import org.snf4j.core.factory.IDatagramHandlerFactory;
import org.snf4j.core.handler.IDatagramHandler;
import org.snf4j.core.session.IDatagramSession;

import gg.dragonfruit.network.packet.Packet;
import gg.dragonfruit.network.util.DatagramChannelBuilder;
import gg.dragonfruit.network.util.PacketUtil;

public class PacketTransmitter {
    Connection connection;
    DatagramChannel channel;
    boolean isActive = true;
    SelectorLoop loop;

    public PacketTransmitter(int port) {
        InetSocketAddress address = new InetSocketAddress("localhost", port);
        try {
            channel = DatagramChannelBuilder.bindChannel(address);
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }

        try {
            loop = new SelectorLoop();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        this.connection = new Connection(address.getAddress(), port);
    }

    public PacketTransmitter() {
        InetSocketAddress address = new InetSocketAddress(0);

        try {
            channel = DatagramChannelBuilder.bindChannel(address);
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }

        try {
            loop = new SelectorLoop();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        this.connection = new Connection(address.getAddress(), address.getPort());
    }

    public Connection getConnection() {
        return connection;
    }

    public void stop() {
        loop.stop();
    }

    public void start() {
        loop.start();

        try {
            loop.register(channel, new DatagramServerHandler(new IDatagramHandlerFactory() {

                @Override
                public IDatagramHandler create(SocketAddress remoteAddress) {
                    return new PacketHandler();
                }

            }));
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }

    }

    public void sendPacket(Packet packet, Connection connection) {
        IDatagramSession session = connection.getSession();

        if (session != null) {
            try {
                session.write(PacketUtil.serializePacket(packet));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

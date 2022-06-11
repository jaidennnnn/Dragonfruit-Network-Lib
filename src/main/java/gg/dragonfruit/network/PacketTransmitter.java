package gg.dragonfruit.network;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutionException;

import org.snf4j.core.DatagramServerHandler;
import org.snf4j.core.SelectorLoop;
import org.snf4j.core.factory.IDatagramHandlerFactory;
import org.snf4j.core.handler.IDatagramHandler;
import org.snf4j.core.session.IDatagramSession;
import org.snf4j.core.session.ISession;

import gg.dragonfruit.network.encryption.EndToEndEncryption;
import gg.dragonfruit.network.packet.KeyNumberPacket;
import gg.dragonfruit.network.packet.Packet;
import gg.dragonfruit.network.util.BigIntegerCache;
import gg.dragonfruit.network.util.DatagramChannelBuilder;
import gg.dragonfruit.network.util.PacketUtil;

public class PacketTransmitter {

    static DatagramChannel channel;
    boolean isActive = true;
    static SelectorLoop loop;
    static EndToEndEncryption endToEndEncryption = new EndToEndEncryption();

    public static EndToEndEncryption getSelfEndToEndEncryption(BigInteger numberOfKeys) {
        endToEndEncryption.setNumberOfKeys(numberOfKeys);
        return endToEndEncryption;
    }

    public static void startServer(int port) {
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

        loop.start();

        try {
            loop.register(channel, new DatagramServerHandler(new IDatagramHandlerFactory() {

                @Override
                public IDatagramHandler create(SocketAddress remoteAddress) {
                    return new ServerHandler();
                }

            }));
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }

    public static void startClient(InetSocketAddress serverSocketAddress) {

        try {
            channel = DatagramChannelBuilder.connect(serverSocketAddress);
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

        loop.start();

        try {
            ISession session = loop.register(channel, new ClientHandler()).sync().getSession();
            ClientHandler
                    .setServerConnection(
                            new ServerConnection(serverSocketAddress.getAddress(), serverSocketAddress.getPort(),
                                    (IDatagramSession) session));
        } catch (ClosedChannelException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        ClientHandler.getServerConnection()
                .sendPacket(new KeyNumberPacket(BigIntegerCache.NUMBER_OF_KEYS));
    }

    public static void stop() {
        loop.stop();
    }

    public static void sendPacket(Packet packet, Connection connection) {
        IDatagramSession session = connection.getSession();

        try {
            session.write(PacketUtil.serializePacket(packet));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

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
import gg.dragonfruit.network.encryption.RSAEncryption;
import gg.dragonfruit.network.packet.KeyNumberPacket;
import gg.dragonfruit.network.packet.Packet;
import gg.dragonfruit.network.util.BigIntegerCache;
import gg.dragonfruit.network.util.DatagramChannelBuilder;
import gg.dragonfruit.network.util.PacketUtil;

public class PacketTransmitter {
    Connection serverConnection;
    DatagramChannel channel;
    boolean isActive = true;
    SelectorLoop loop;
    EndToEndEncryption selfEndToEndEncryption;

    public EndToEndEncryption getSelfEndToEndEncryption() {
        return selfEndToEndEncryption;
    }

    public void initSelfEndToEndEncryption(BigInteger keyNumber) {
        this.selfEndToEndEncryption = new EndToEndEncryption(keyNumber);
    }

    public void startServer(int port) {
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

        RSAEncryption.init();
    }

    public void startClient(InetSocketAddress serverSocketAddress) {
        selfEndToEndEncryption = new EndToEndEncryption(BigIntegerCache.NUMBER_OF_KEYS);

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
            this.serverConnection = new Connection(serverSocketAddress.getAddress(), serverSocketAddress.getPort(),
                    (IDatagramSession) session);
        } catch (ClosedChannelException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        RSAEncryption.init();

        this.serverConnection.initializeEncryption();
        this.serverConnection.sendPacket(new KeyNumberPacket(BigIntegerCache.NUMBER_OF_KEYS, RSAEncryption.PUBLIC_KEY));
    }

    public Connection getServerConnection() {
        return serverConnection;
    }

    public void stop() {
        loop.stop();
    }

    public void sendPacket(Packet packet, Connection connection) {
        IDatagramSession session = connection.getSession();

        try {
            session.write(PacketUtil.serializePacket(packet));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

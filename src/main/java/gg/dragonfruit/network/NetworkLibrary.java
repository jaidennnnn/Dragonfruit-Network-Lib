package gg.dragonfruit.network;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import gg.dragonfruit.network.collection.ConnectionList;

public final class NetworkLibrary {
    static PacketTransmitter packetTransmitter = new PacketTransmitter();
    static ConnectionList connected = new ConnectionList();
    public static ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

    public static PacketTransmitter getPacketTransmitter() {
        return packetTransmitter;
    }

    public static void main(String[] args) throws Exception {
    }

    public static ConnectionList getConnections() {
        return connected;
    }

    public static void startClient(InetSocketAddress serverSocketAddress) {
        packetTransmitter.startClient(serverSocketAddress);
    }

    public static void startServer(int port) {
        packetTransmitter.startServer(port);

    }

    public static void stop() {
        packetTransmitter.stop();
    }
}

package gg.dragonfruit.network;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import gg.dragonfruit.network.collection.ConnectionList;

public final class NetworkLibrary {
    static PacketTransmitter packetTransmitter;
    static ConnectionList connected = new ConnectionList();
    public static ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

    public static PacketTransmitter getPacketTransmitter() {
        return packetTransmitter;
    }

    public static void main(String[] args) throws Exception {
        start();
    }

    public static ConnectionList getConnections() {
        return connected;
    }

    public static void start() {
        packetTransmitter = new PacketTransmitter();
        packetTransmitter.start();
    }

    public static void start(int port) {
        packetTransmitter = new PacketTransmitter(port);
        packetTransmitter.start();
    }

    public static void stop() {
        packetTransmitter.stop();
    }
}

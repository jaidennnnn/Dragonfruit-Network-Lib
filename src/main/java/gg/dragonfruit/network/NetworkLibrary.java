package gg.dragonfruit.network;

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

    public static ConnectionList getConnected() {
        return connected;
    }

    public static void start() {
        packetTransmitter.start();
    }

    public static void stop() {
        packetTransmitter.close();
    }
}

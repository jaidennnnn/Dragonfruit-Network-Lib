package gg.dragonfruit.network;

import java.net.InetSocketAddress;

public final class NetworkLibrary {

    public static void main(String[] args) throws Exception {
    }

    public static void startClient(InetSocketAddress serverSocketAddress) {
        PacketTransmitter.startClient(serverSocketAddress);
    }

    public static void startServer(int port) {
        PacketTransmitter.startServer(port);

    }

    public static void stop() {
        PacketTransmitter.stop();
    }
}

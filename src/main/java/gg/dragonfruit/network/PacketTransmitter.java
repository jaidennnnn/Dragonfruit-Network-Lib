package gg.dragonfruit.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

import gg.dragonfruit.network.collection.GlueList;
import gg.dragonfruit.network.packet.Packet;
import gg.dragonfruit.network.thread.ListenerThread;
import gg.dragonfruit.network.util.PacketUtil;

public class PacketTransmitter extends Thread {
    DatagramSocket socket;
    Connection connection;
    boolean isActive = true;
    List<ListenerThread> listenerThreads;
    ListenerThread currentListenerThread = null;
    int threadCount;

    public PacketTransmitter(int port, int threadCount) {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }

        this.connection = new Connection(socket.getInetAddress(), port);
        this.threadCount = threadCount;
        this.listenerThreads = new GlueList<ListenerThread>(threadCount);
    }

    public PacketTransmitter(int threadCount) {
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }

        this.connection = new Connection(socket.getInetAddress(), socket.getPort());
        this.threadCount = threadCount;
        this.listenerThreads = new GlueList<ListenerThread>(threadCount);
    }

    public PacketTransmitter() {
        this(Runtime.getRuntime().availableProcessors());
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        this.isActive = false;
        socket.close();
    }

    @Override
    public void run() {

        for (int i = 0; i < threadCount; i++) {
            ListenerThread thread = new ListenerThread(socket);
            thread.start();
            listenerThreads.add(thread);
        }

        currentListenerThread = listenerThreads.get(0);
        currentListenerThread.awaken();

        while (isActive) {
            if (!currentListenerThread.isBusy()) {
                continue;
            }

            currentListenerThread.sleep();

            for (ListenerThread thread : listenerThreads) {
                if (thread.isBusy()) {
                    continue;
                }

                currentListenerThread = thread;
                currentListenerThread.awaken();
            }
        }

        for (ListenerThread thread : listenerThreads) {
            thread.shutdown();
            thread.awaitCompletion();
        }

        socket.close();
    }

    public void sendPacket(Packet packet, Connection connection) {
        try {
            byte[] data = PacketUtil.serializePacket(packet);
            DatagramPacket datagramPacket = new DatagramPacket(data, data.length, connection.getAddress(),
                    connection.getPort());
            socket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

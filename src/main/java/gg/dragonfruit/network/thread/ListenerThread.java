package gg.dragonfruit.network.thread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import gg.dragonfruit.network.Connection;
import gg.dragonfruit.network.NetworkLibrary;
import gg.dragonfruit.network.packet.EncryptedPacket;
import gg.dragonfruit.network.packet.Packet;
import gg.dragonfruit.network.util.PacketUtil;

public class ListenerThread extends Thread {

    DatagramSocket socket;

    public ListenerThread(DatagramSocket socket) {
        this.socket = socket;
    }

    boolean sleeping = true;
    boolean isBusy = false;
    boolean active = true;

    public void sleep() {
        sleeping = true;
    }

    public void awaken() {
        sleeping = false;
        this.interrupt();
    }

    public boolean isBusy() {
        return isBusy;
    }

    byte[] buf = new byte[256];
    final byte[] EMPTY = new byte[256];

    @Override
    public void run() {
        while (active) {
            if (sleeping) {
                try {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException e) {

                }
                continue;
            }

            buf = EMPTY;
            DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);

            try {
                socket.receive(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            isBusy = true;

            InetAddress address = datagramPacket.getAddress();
            int port = datagramPacket.getPort();

            Packet received;
            try {
                received = PacketUtil.deserializePacket(datagramPacket.getData());
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
                isBusy = false;
                continue;
            }

            Connection connection = NetworkLibrary.getConnections().get(address, port);

            if (connection == null) {
                isBusy = false;
                continue;
            }

            if (received instanceof EncryptedPacket) {
                EncryptedPacket encryptedPacket = (EncryptedPacket) received;
                encryptedPacket.decrypt(NetworkLibrary.getPacketTransmitter().getConnection().getEndToEndEncryption(),
                        connection.getPublicKey());
            }

            received.received(connection);

            isBusy = false;
        }
    }

    public void shutdown() {
        active = false;
    }

    public void awaitCompletion() {
        while (isBusy) {

        }
    }
}

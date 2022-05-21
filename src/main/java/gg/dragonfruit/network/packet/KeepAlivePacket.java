package gg.dragonfruit.network.packet;

import java.math.BigInteger;

import java.util.concurrent.TimeUnit;

import gg.dragonfruit.network.Connection;
import gg.dragonfruit.network.NetworkLibrary;
import gg.dragonfruit.network.PacketTransmitter;

public class KeepAlivePacket extends Packet {

    String publicKeyStr;
    boolean respond;

    public KeepAlivePacket(BigInteger publicKey, boolean respond) {
        this.publicKeyStr = publicKey.toString();
        this.respond = respond;
    }

    @Override
    public void recieved(Connection connection) {
        connection.setPublicKey(new BigInteger(publicKeyStr));
        PacketTransmitter packetTransmitter = NetworkLibrary.getPacketTransmitter();

        if (respond) {
            BigInteger publicKey = packetTransmitter.getConnection().newPublicKey();
            packetTransmitter.sendPacket(new KeepAlivePacket(publicKey, false),
                    connection);

            Runnable task = () -> {
                BigInteger publicKey1 = packetTransmitter.getConnection().newPublicKey();
                packetTransmitter.sendPacket(new KeepAlivePacket(publicKey1, true),
                        connection);
            };

            NetworkLibrary.SCHEDULER.schedule(task, 1, TimeUnit.SECONDS);
            return;
        }
    }

}

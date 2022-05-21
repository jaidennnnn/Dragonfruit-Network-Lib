package gg.dragonfruit.network;

import java.math.BigInteger;
import java.net.InetAddress;

import gg.dragonfruit.network.encryption.EndToEndEncryption;
import gg.dragonfruit.network.packet.EncryptedPacket;
import gg.dragonfruit.network.packet.Packet;

public class Connection {
    final InetAddress address;
    final int port;
    BigInteger publicKey;
    EndToEndEncryption endToEndEncryption = new EndToEndEncryption();
    long lastKeepAliveRecieved = 0;

    public Connection(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public void setPublicKey(BigInteger publicKey) {
        this.publicKey = publicKey;
    }

    public BigInteger getPublicKey() {
        return this.publicKey;
    }

    public BigInteger newPublicKey() {
        return this.publicKey = endToEndEncryption.getPublicKey();
    }

    public void sendEncryptedPacket(EncryptedPacket packet) {
        packet.encrypt(NetworkLibrary.getPacketTransmitter().getConnection().getPublicKey());
        NetworkLibrary.getPacketTransmitter().sendPacket(packet,
                this);
    }

    public void sendPacket(Packet packet) {
        NetworkLibrary.getPacketTransmitter().sendPacket(packet, this);
    }

    public InetAddress getAddress() {
        return address;
    }

    public void keepAlive() {
        lastKeepAliveRecieved = System.currentTimeMillis();
    }

    public boolean timedOut() {
        return System.currentTimeMillis() - lastKeepAliveRecieved > 3000;
    }

    public int getPort() {
        return port;
    }
}

package gg.dragonfruit.network;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

import org.snf4j.core.session.IDatagramSession;

import gg.dragonfruit.network.encryption.EndToEndEncryption;
import gg.dragonfruit.network.packet.EncryptedPacket;
import gg.dragonfruit.network.packet.Packet;
import gg.dragonfruit.network.packet.PublicKeyPacket;

public class Connection {
    final InetSocketAddress socketAddress;
    BigInteger publicKey;
    EndToEndEncryption endToEndEncryption = new EndToEndEncryption();
    long lastKeepAliveReceived = 0;
    IDatagramSession session;
    boolean waitingForPublicKey = true;

    public Connection(InetAddress address, int port, IDatagramSession session) {
        this.socketAddress = new InetSocketAddress(address, port);
        this.session = session;
    }

    public Connection(InetSocketAddress socketAddress, IDatagramSession session) {
        this.socketAddress = socketAddress;
        this.session = session;
    }

    public IDatagramSession getSession() {
        return this.session;
    }

    public InetSocketAddress getSocketAddress() {
        return socketAddress;
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
        sendPacket(new PublicKeyPacket(publicKey, true));
        awaitPublicKeyAsync().whenComplete((func, exception) -> {
            packet.encrypt(NetworkLibrary.getPacketTransmitter().getServerConnection().getEndToEndEncryption(),
                    this.getPublicKey());
            sendPacket(packet);
        });
    }

    public void sendPacket(Packet packet) {
        NetworkLibrary.getPacketTransmitter().sendPacket(packet, this);
    }

    public InetAddress getAddress() {
        return socketAddress.getAddress();
    }

    public boolean checkConnection() {
        try {
            return getAddress().isReachable(3000);
        } catch (IOException e) {
            return false;
        }
    }

    public CompletableFuture<Void> awaitPublicKeyAsync() {
        return CompletableFuture.runAsync(() -> awaitPublicKey());
    }

    public void awaitPublicKey() {
        this.waitingForPublicKey = true;
        while (waitingForPublicKey) {

        }
    }

    public void receivedPublicKey() {
        this.waitingForPublicKey = false;
    }

    public int getPort() {
        return socketAddress.getPort();
    }

    public EndToEndEncryption getEndToEndEncryption() {
        return endToEndEncryption;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Connection) {
            Connection c = (Connection) o;

            return c.getAddress().equals(getAddress()) && c.getPort() == getPort();
        }

        return false;
    }
}

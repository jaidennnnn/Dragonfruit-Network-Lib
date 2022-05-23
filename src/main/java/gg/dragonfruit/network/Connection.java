package gg.dragonfruit.network;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;

import gg.dragonfruit.network.encryption.EndToEndEncryption;
import gg.dragonfruit.network.packet.EncryptedPacket;
import gg.dragonfruit.network.packet.Packet;
import gg.dragonfruit.network.packet.PublicKeyPacket;

public class Connection {
    final InetAddress address;
    final int port;
    BigInteger publicKey;
    EndToEndEncryption endToEndEncryption = new EndToEndEncryption();
    long lastKeepAliveReceived = 0;
    boolean waitingForPublicKey = true;

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
        sendPacket(new PublicKeyPacket(publicKey, true));
        awaitPublicKeyAsync().whenComplete((func, exception) -> {
            packet.encrypt(this);
            sendPacket(packet);
        });
    }

    public void sendPacket(Packet packet) {
        NetworkLibrary.getPacketTransmitter().sendPacket(packet, this);
    }

    public InetAddress getAddress() {
        return address;
    }

    public boolean checkConnection() {
        try {
            return address.isReachable(3000);
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
        return port;
    }
}

package gg.dragonfruit.network;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.concurrent.CompletableFuture;

import org.snf4j.core.session.IDatagramSession;

import gg.dragonfruit.network.packet.EncryptedPacket;
import gg.dragonfruit.network.packet.Packet;
import gg.dragonfruit.network.packet.PublicKeyPacket;

public class Connection {
    final InetSocketAddress socketAddress;
    BigInteger publicKey;
    long lastKeepAliveReceived = 0;
    IDatagramSession session;
    boolean waitingForPublicKey = true;
    boolean waitingForKeyNumber = true;
    boolean waitingForSecureTunnel = true;
    PublicKey rsaPublicKey = null;
    String toEncrypt;
    SecureRandom SECURE_RANDOM = new SecureRandom();

    public void randomStr() {
        byte[] array = new byte[64];
        SECURE_RANDOM.nextBytes(array);
        toEncrypt = new String(array, Charset.forName("UTF-8"));
    }

    public String getStringForVerification() {
        return toEncrypt;
    }

    public void secureTunnelEstablished() {
        this.waitingForSecureTunnel = false;
    }

    public Connection(InetAddress address, int port, IDatagramSession session) {
        this.socketAddress = new InetSocketAddress(address, port);
        this.session = session;
    }

    public void initializeEncryption() {
        waitingForKeyNumber = false;
    }

    public void setRSAPublicKey(PublicKey rsaPublicKey) {
        this.rsaPublicKey = rsaPublicKey;
    }

    public PublicKey getRSAPublicKey() {
        return rsaPublicKey;
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

    public void sendEncryptedPacket(EncryptedPacket packet) {
        sendPacket(new PublicKeyPacket(NetworkLibrary.getPacketTransmitter().getSelfEndToEndEncryption().getPublicKey(),
                true, this));
        awaitPublicKeyAsync().whenComplete((func, exception) -> {
            packet.encrypt(NetworkLibrary.getPacketTransmitter().getSelfEndToEndEncryption(),
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

    public CompletableFuture<Void> awaitPublicKeyAsync() {
        return CompletableFuture.runAsync(() -> awaitPublicKey());
    }

    public void awaitPublicKey() {
        this.waitingForPublicKey = true;
        while (waitingForPublicKey) {

        }
    }

    public CompletableFuture<Void> awaitInitializationAsync() {
        return CompletableFuture.runAsync(() -> awaitInitialization());
    }

    public void awaitInitialization() {
        while (waitingForKeyNumber && waitingForSecureTunnel) {

        }
    }

    public boolean isWaitingForHandshake() {
        return waitingForKeyNumber;
    }

    public void receivedPublicKey() {
        this.waitingForPublicKey = false;
    }

    public int getPort() {
        return socketAddress.getPort();
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

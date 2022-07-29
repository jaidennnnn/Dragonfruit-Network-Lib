package gg.dragonfruit.network;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.concurrent.CompletableFuture;

import org.snf4j.core.session.IDatagramSession;

import gg.dragonfruit.network.encryption.EndToEndEncryption;
import gg.dragonfruit.network.packet.DHEncryptedPacket;
import gg.dragonfruit.network.packet.DHRequestPacket;
import gg.dragonfruit.network.packet.Packet;

public class Connection {
    final InetSocketAddress socketAddress;
    IDatagramSession session;
    boolean waitingForDHPublicKey = true;
    EndToEndEncryption endToEndEncryption = new EndToEndEncryption();

    public Connection(InetAddress address, int port, IDatagramSession session) {
        this.socketAddress = new InetSocketAddress(address, port);
        this.session = session;
    }

    public EndToEndEncryption getSelfEndToEndEncryption() {
        return endToEndEncryption;
    }

    public void sendPacket(Packet packet) {
        if (packet instanceof DHEncryptedPacket) {
            sendDHEncryptedPacket((DHEncryptedPacket) packet);
            return;
        }

        PacketTransmitter.sendPacket(packet, this);
    }

    void sendDHEncryptedPacket(DHEncryptedPacket packet) {
        if (this.waitingForDHPublicKey) {
            awaitDHPublicKey().whenComplete((dhPublicKey, exception) -> {
                sendDHEncryptedPacket(packet);
                System.out.println("sent encrypted packet");
            });
            return;
        }

        packet.encrypt(getSelfEndToEndEncryption());
        PacketTransmitter.sendPacket(packet, this);
    }

    public IDatagramSession getSession() {
        return this.session;
    }

    public InetAddress getAddress() {
        return socketAddress.getAddress();
    }

    public int getPort() {
        return socketAddress.getPort();
    }

    CompletableFuture<Void> awaitDHPublicKey() {
        return CompletableFuture.runAsync(() -> {
            while (waitingForDHPublicKey) {

            }
        });
    }

    public void requestDHPublicKey() {
        System.out.println("Requesting DH public key");
        this.waitingForDHPublicKey = true;
        BigInteger numberOfKeys;
        getSelfEndToEndEncryption()
                .setNumberOfKeys(numberOfKeys = BigInteger.probablePrime(4096, new SecureRandom()));
        System.out.println("set Number of keys");
        sendPacket(new DHRequestPacket(numberOfKeys, getSelfEndToEndEncryption().getPublicKey()));
        System.out.println("sent packet");
    }

    public void setOtherPublicKey(BigInteger otherPublicKey) {
        getSelfEndToEndEncryption().setOtherPublicKey(otherPublicKey);
        this.waitingForDHPublicKey = false;
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

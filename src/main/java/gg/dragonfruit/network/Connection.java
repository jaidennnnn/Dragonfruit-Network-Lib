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
        if (getSelfEndToEndEncryption().needsKeyExchange()) {
            requestDHPublicKey().whenComplete((dhPublicKey, exception) -> {
                sendDHEncryptedPacket(packet);
            });
            return;
        }

        BigInteger numberOfKeys = getSelfEndToEndEncryption().getNumberOfKeys();

        if (numberOfKeys == null) {
            getSelfEndToEndEncryption()
                    .setNumberOfKeys(numberOfKeys = BigInteger.probablePrime(6144, new SecureRandom()));
            packet.setNumberOfKeys(numberOfKeys);
        }

        packet.setSenderPublicKey(getSelfEndToEndEncryption().getPublicKey());
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

    CompletableFuture<Void> requestDHPublicKey() {
        return CompletableFuture.runAsync(() -> {
            this.waitingForDHPublicKey = true;
            sendPacket(new DHRequestPacket());
            while (waitingForDHPublicKey) {

            }
        });
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

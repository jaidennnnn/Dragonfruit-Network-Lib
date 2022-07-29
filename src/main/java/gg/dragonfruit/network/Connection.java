package gg.dragonfruit.network;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.snf4j.core.session.IDatagramSession;

import gg.dragonfruit.network.encryption.EndToEndEncryption;
import gg.dragonfruit.network.packet.DHEncryptedPacket;
import gg.dragonfruit.network.packet.DHExchangePacket;
import gg.dragonfruit.network.packet.DHInitPacket;
import gg.dragonfruit.network.packet.DHRequestKeyPacket;
import gg.dragonfruit.network.packet.Packet;

public class Connection {
    final InetSocketAddress socketAddress;
    IDatagramSession session;
    boolean waitingForDHPublicKey = true;
    Queue<DHEncryptedPacket> encryptedPacketQueue = new ConcurrentLinkedQueue<>();
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
            sendDHEncryptedPacket((DHEncryptedPacket) packet, true);
            return;
        }

        PacketTransmitter.sendPacket(packet, this);
    }

    long lastRefreshTime = 0;

    void sendDHEncryptedPacket(DHEncryptedPacket packet, boolean newKey) {

        if (newKey && System.currentTimeMillis() - lastRefreshTime > 60000) {
            requestDHPublicKey();
        }

        if (this.waitingForDHPublicKey) {
            encryptedPacketQueue.add(packet);
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

    public void requestDHPublicKey() {
        this.waitingForDHPublicKey = true;
        sendPacket(new DHRequestKeyPacket());
    }

    public void exchangeDHPublicKeys() {
        this.waitingForDHPublicKey = true;
        BigInteger sharedKey = getSelfEndToEndEncryption().getSharedKey();
        DHExchangePacket exchangePacket = new DHExchangePacket(getSelfEndToEndEncryption().getPublicKey());
        exchangePacket.encrypt(getSelfEndToEndEncryption(), sharedKey);
        sendPacket(exchangePacket);
    }

    public void initDH() {
        this.waitingForDHPublicKey = true;
        BigInteger numberOfKeys;
        getSelfEndToEndEncryption()
                .setNumberOfKeys(numberOfKeys = BigInteger.probablePrime(4096, new SecureRandom()));
        sendPacket(new DHInitPacket(numberOfKeys, getSelfEndToEndEncryption().getPublicKey()));
    }

    public void setOtherPublicKey(BigInteger otherPublicKey) {
        getSelfEndToEndEncryption().setOtherPublicKey(otherPublicKey);
        this.waitingForDHPublicKey = false;

        DHEncryptedPacket packet;
        while ((packet = encryptedPacketQueue.poll()) != null) {
            sendDHEncryptedPacket(packet, false);
        }

        lastRefreshTime = System.currentTimeMillis();
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

package gg.dragonfruit.network;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.concurrent.CompletableFuture;

import org.snf4j.core.session.IDatagramSession;

import gg.dragonfruit.network.encryption.EndToEndEncryption;
import gg.dragonfruit.network.packet.DHEncryptedPacket;
import gg.dragonfruit.network.packet.DHPublicKeyPacket;
import gg.dragonfruit.network.packet.Packet;
import gg.dragonfruit.network.packet.RSAEncryptedPacket;
import gg.dragonfruit.network.packet.RSAPublicKeyPacket;

public abstract class Connection {
    final InetSocketAddress socketAddress;
    BigInteger dhPublicKey;
    IDatagramSession session;
    BigInteger numberOfKeys;
    PublicKey rsaPublicKey = null;
    boolean waitingForRSAPublicKey = true;
    boolean waitingForDHPublicKey = true;

    public Connection(InetAddress address, int port, IDatagramSession session) {
        this.socketAddress = new InetSocketAddress(address, port);
        this.session = session;
    }

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

    public void setKeyNumber(BigInteger numberOfKeys) {
        this.numberOfKeys = numberOfKeys;
    }

    public void setDHPublicKey(BigInteger dhPublicKey) {
        this.dhPublicKey = dhPublicKey;
        this.waitingForDHPublicKey = false;
    }

    public BigInteger getDHPublicKey() {
        return dhPublicKey;
    }

    public void sendPacket(Packet packet) {
        if (packet instanceof DHEncryptedPacket) {
            sendDHEncryptedPacket((DHEncryptedPacket) packet);
            return;
        }

        if (packet instanceof RSAEncryptedPacket) {
            sendRSAEncryptedPacket((RSAEncryptedPacket) packet);
            return;
        }

        PacketTransmitter.sendPacket(packet, this);
    }

    void sendDHEncryptedPacket(DHEncryptedPacket packet) {
        EndToEndEncryption endToEndEncryption = PacketTransmitter.getSelfEndToEndEncryption(numberOfKeys);

        exchangeDHPublicKeys(endToEndEncryption).whenComplete((dhPublicKey, exception) -> {
            packet.encrypt(endToEndEncryption,
                    dhPublicKey);
            PacketTransmitter.sendPacket(packet, Connection.this);
        });
    }

    void sendRSAEncryptedPacket(RSAEncryptedPacket packet) {
        if (rsaPublicKey != null) {
            packet.encrypt(this.rsaPublicKey);
            PacketTransmitter.sendPacket(packet, Connection.this);
            return;
        }

        exchangeRSAPublicKeys().whenComplete((rsaPublicKey, exception) -> {
            packet.encrypt(rsaPublicKey);
            PacketTransmitter.sendPacket(packet, Connection.this);
        });
    }

    public void setRSAPublicKey(PublicKey rsaPublicKey) {
        this.rsaPublicKey = rsaPublicKey;
        this.waitingForRSAPublicKey = false;
    }

    public PublicKey getRSAPublicKey() {
        return rsaPublicKey;
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

    CompletableFuture<BigInteger> exchangeDHPublicKeys(EndToEndEncryption endToEndEncryption) {
        return CompletableFuture.supplyAsync(() -> {
            this.waitingForDHPublicKey = true;
            sendPacket(new DHPublicKeyPacket(endToEndEncryption.getPublicKey(),
                    true, this));
            while (waitingForDHPublicKey) {

            }

            return this.dhPublicKey;
        });
    }

    CompletableFuture<PublicKey> exchangeRSAPublicKeys() {
        return CompletableFuture.supplyAsync(() -> {
            this.waitingForRSAPublicKey = true;
            sendPacket(new RSAPublicKeyPacket(getNewRSAPublicKey(),
                    true));
            while (waitingForRSAPublicKey) {

            }

            return this.rsaPublicKey;
        });
    }

    public abstract PublicKey getNewRSAPublicKey();

    public abstract PrivateKey getRSAPrivateKey();

    @Override
    public boolean equals(Object o) {
        if (o instanceof Connection) {
            Connection c = (Connection) o;

            return c.getAddress().equals(getAddress()) && c.getPort() == getPort();
        }

        return false;
    }

    public BigInteger getNumberOfKeys() {
        return numberOfKeys;
    }
}

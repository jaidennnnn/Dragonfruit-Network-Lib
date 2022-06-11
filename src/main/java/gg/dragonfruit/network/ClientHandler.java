package gg.dragonfruit.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.snf4j.core.EndingAction;
import org.snf4j.core.handler.AbstractDatagramHandler;
import org.snf4j.core.handler.SessionEvent;
import org.snf4j.core.session.DefaultSessionConfig;
import org.snf4j.core.session.ISessionConfig;

import gg.dragonfruit.network.encryption.RSAEncryption;
import gg.dragonfruit.network.packet.DHEncryptedPacket;
import gg.dragonfruit.network.packet.KeyNumberPacket;
import gg.dragonfruit.network.packet.Packet;
import gg.dragonfruit.network.packet.RSAEncryptedPacket;
import gg.dragonfruit.network.util.BigIntegerCache;
import gg.dragonfruit.network.util.PacketUtil;

public class ClientHandler extends AbstractDatagramHandler {

    static ServerConnection serverConnection;
    public static PrivateKey RSA_PRIVATE_KEY;
    public static PublicKey RSA_PUBLIC_KEY;

    public static PublicKey getNewRSAPublicKey() {
        KeyPair keyPair = RSAEncryption.generateKeyPair();
        RSA_PRIVATE_KEY = keyPair.getPrivate();
        return RSA_PUBLIC_KEY = keyPair.getPublic();
    }

    public static ServerConnection getServerConnection() {
        return serverConnection;
    }

    public static void setServerConnection(ServerConnection serverConnection) {
        ClientHandler.serverConnection = serverConnection;
    }

    @Override
    public void read(Object obj) {
        SocketAddress socketAddress = this.getSession().getRemoteAddress();

        if (!(socketAddress instanceof InetSocketAddress)) {
            return;
        }

        byte[] data = (byte[]) obj;

        Packet received;
        try {
            received = PacketUtil.deserializePacket(data);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return;
        }

        if (received instanceof DHEncryptedPacket) {
            DHEncryptedPacket encryptedPacket = (DHEncryptedPacket) received;
            encryptedPacket.decrypt(PacketTransmitter.getSelfEndToEndEncryption(BigIntegerCache.NUMBER_OF_KEYS),
                    serverConnection.getDHPublicKey());
        }

        if (received instanceof RSAEncryptedPacket) {
            RSAEncryptedPacket encryptedPacket = (RSAEncryptedPacket) received;
            encryptedPacket.decrypt(RSA_PRIVATE_KEY);
        }

        received.received(serverConnection);
    }

    @Override
    public void event(SessionEvent event) {
        if (event == SessionEvent.OPENED) {
            serverConnection.sendPacket(new KeyNumberPacket(BigIntegerCache.NUMBER_OF_KEYS));
        }
    }

    @Override
    public ISessionConfig getConfig() {
        return new DefaultSessionConfig()
                .setEndingAction(EndingAction.STOP);
    }

    @Override
    public void read(SocketAddress remoteAddress, Object msg) {
        // TODO Auto-generated method stub

    }

}

package gg.dragonfruit.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.snf4j.core.EndingAction;
import org.snf4j.core.handler.AbstractDatagramHandler;
import org.snf4j.core.session.DefaultSessionConfig;
import org.snf4j.core.session.ISessionConfig;

import gg.dragonfruit.network.encryption.EndToEndEncryption;
import gg.dragonfruit.network.packet.DHEncryptedPacket;
import gg.dragonfruit.network.packet.Packet;
import gg.dragonfruit.network.util.PacketUtil;

public class ClientHandler extends AbstractDatagramHandler {

    static Connection serverConnection;

    public static Connection getServerConnection() {
        return serverConnection;
    }

    public static void setServerConnection(Connection serverConnection) {
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
            EndToEndEncryption endToEndEncryption = serverConnection.getSelfEndToEndEncryption();
            serverConnection.setOtherPublicKey(encryptedPacket.getSenderPublicKey());
            endToEndEncryption.setSharedKey();
            encryptedPacket.decrypt(endToEndEncryption);
            encryptedPacket.confirmReceived(serverConnection);
        }

        received.received(serverConnection);
    }

    @Override
    public ISessionConfig getConfig() {
        return new DefaultSessionConfig()
                .setEndingAction(EndingAction.STOP);
    }

    @Override
    public void read(SocketAddress remoteAddress, Object msg) {
    }

}

package gg.dragonfruit.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.snf4j.core.EndingAction;
import org.snf4j.core.handler.AbstractDatagramHandler;
import org.snf4j.core.handler.SessionEvent;
import org.snf4j.core.session.DefaultSessionConfig;
import org.snf4j.core.session.ISessionConfig;

import gg.dragonfruit.network.encryption.EndToEndEncryption;
import gg.dragonfruit.network.packet.EncryptedPacket;
import gg.dragonfruit.network.packet.Packet;
import gg.dragonfruit.network.util.BigIntegerCache;
import gg.dragonfruit.network.util.PacketUtil;

public class ClientHandler extends AbstractDatagramHandler {

    EndToEndEncryption endToEndEncryption;

    public ClientHandler() {
        endToEndEncryption = new EndToEndEncryption(BigIntegerCache.NUMBER_OF_KEYS);
    }

    @Override
    public void read(Object obj) {
        SocketAddress socketAddress = this.getSession().getRemoteAddress();

        if (!(socketAddress instanceof InetSocketAddress)) {
            return;
        }

        Connection connection = NetworkLibrary.getPacketTransmitter().getServerConnection();

        byte[] data = (byte[]) obj;

        Packet received;
        try {
            received = PacketUtil.deserializePacket(data);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return;
        }

        if (received instanceof EncryptedPacket) {
            EncryptedPacket encryptedPacket = (EncryptedPacket) received;
            encryptedPacket.decrypt(NetworkLibrary.getPacketTransmitter().getSelfEndToEndEncryption(),
                    connection.getPublicKey());
        }

        received.received(connection);
    }

    @Override
    public void event(SessionEvent event) {
        if (event == SessionEvent.OPENED) {

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

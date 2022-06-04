package gg.dragonfruit.network.packet;

import java.math.BigInteger;

import gg.dragonfruit.network.Connection;
import gg.dragonfruit.network.NetworkLibrary;
import gg.dragonfruit.network.PacketTransmitter;

public class PublicKeyPacket extends Packet {

    String publicKeyStr;
    boolean respond;

    public PublicKeyPacket(BigInteger publicKey, boolean respond) {
        this.publicKeyStr = publicKey.toString();
        this.respond = respond;
    }

    @Override
    public void received(Connection connection) {
        connection.setPublicKey(new BigInteger(publicKeyStr));
        PacketTransmitter packetTransmitter = NetworkLibrary.getPacketTransmitter();

        if (respond) {
            BigInteger publicKey = packetTransmitter.getServerConnection().newPublicKey();
            packetTransmitter.sendPacket(new PublicKeyPacket(publicKey, false),
                    connection);
        } else {
            connection.receivedPublicKey();
        }
    }

}

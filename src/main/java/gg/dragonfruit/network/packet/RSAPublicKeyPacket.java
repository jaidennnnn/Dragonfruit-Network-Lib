package gg.dragonfruit.network.packet;

import java.security.PublicKey;

import gg.dragonfruit.network.ClientHandler;
import gg.dragonfruit.network.Connection;
import gg.dragonfruit.network.ServerConnection;

public class RSAPublicKeyPacket extends Packet {

    PublicKey publicKey;
    boolean respond;

    public RSAPublicKeyPacket(PublicKey publicKey, boolean respond) {
        this.publicKey = publicKey;
        this.respond = respond;
    }

    @Override
    public void received(Connection connection) {
        connection.setRSAPublicKey(publicKey);
        if (respond) {
            connection.sendPacket(new RSAPublicKeyPacket(
                    connection instanceof ServerConnection ? ClientHandler.getNewRSAPublicKey()
                            : connection.getNewRSAPublicKey(),
                    false));
        }
    }

}

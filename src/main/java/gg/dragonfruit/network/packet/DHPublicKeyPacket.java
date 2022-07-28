package gg.dragonfruit.network.packet;

import java.math.BigInteger;

import gg.dragonfruit.network.Connection;

public class DHPublicKeyPacket extends Packet {

    String publicKeyStr;
    boolean respond;

    public DHPublicKeyPacket(BigInteger publicKey, boolean respond, Connection recipient) {
        this.publicKeyStr = publicKey.toString();
        this.respond = respond;
    }

    @Override
    public void received(Connection connection) {
        if (respond) {
            connection.sendPacket(new DHPublicKeyPacket(
                    connection.getSelfEndToEndEncryption().getPublicKey(),
                    false, connection));
        }

        connection.setOtherPublicKey(new BigInteger(publicKeyStr));
    }
}

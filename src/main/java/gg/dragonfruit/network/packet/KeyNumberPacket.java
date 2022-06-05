package gg.dragonfruit.network.packet;

import java.math.BigInteger;
import java.security.PublicKey;

import gg.dragonfruit.network.Connection;
import gg.dragonfruit.network.NetworkLibrary;

public class KeyNumberPacket extends Packet {
    BigInteger keyNumber;

    public KeyNumberPacket(BigInteger keyNumber, PublicKey decryptionKey) {
        this.keyNumber = keyNumber;
    }

    @Override
    public void received(Connection connection) {
        NetworkLibrary.getPacketTransmitter().initSelfEndToEndEncryption(keyNumber);
        connection.initializeEncryption();
    }
}

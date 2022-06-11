package gg.dragonfruit.network.packet;

import java.math.BigInteger;

import gg.dragonfruit.network.Connection;

public class KeyNumberPacket extends Packet {
    BigInteger keyNumber;

    public KeyNumberPacket(BigInteger keyNumber) {
        this.keyNumber = keyNumber;
    }

    @Override
    public void received(Connection connection) {
        connection.setKeyNumber(keyNumber);
    }
}

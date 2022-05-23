package gg.dragonfruit.network.packet;

import gg.dragonfruit.network.Connection;

public abstract class EncryptedPacket extends Packet {

    public abstract void encrypt(Connection recipient);

    public abstract void decrypt(Connection sender);
}

package gg.dragonfruit.network.packet;

import java.math.BigInteger;

public abstract class EncryptedPacket extends Packet {

    public abstract void encrypt(BigInteger otherPublicKey);

    public abstract void decrypt(BigInteger otherPublicKey);
}

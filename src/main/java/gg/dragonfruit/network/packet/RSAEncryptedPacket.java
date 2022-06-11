package gg.dragonfruit.network.packet;

import java.security.PrivateKey;
import java.security.PublicKey;

public abstract class RSAEncryptedPacket extends Packet {

    public abstract void encrypt(PublicKey otherPublicKey);

    public abstract void decrypt(PrivateKey privateKey);
}

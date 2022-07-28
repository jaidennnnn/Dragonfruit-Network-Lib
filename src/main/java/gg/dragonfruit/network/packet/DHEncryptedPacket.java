package gg.dragonfruit.network.packet;

import java.math.BigInteger;

import gg.dragonfruit.network.encryption.EndToEndEncryption;

public abstract class DHEncryptedPacket extends Packet {

    byte[] senderPublicKeyBytes = null;

    public void setSenderPublicKey(BigInteger senderPublicKey) {
        this.senderPublicKeyBytes = senderPublicKey.toByteArray();
    }

    public BigInteger getSenderPublicKey() {
        return senderPublicKeyBytes == null ? null : new BigInteger(senderPublicKeyBytes);
    }

    public abstract void encrypt(EndToEndEncryption endToEndEncryption);

    public abstract void decrypt(EndToEndEncryption endToEndEncryption);
}

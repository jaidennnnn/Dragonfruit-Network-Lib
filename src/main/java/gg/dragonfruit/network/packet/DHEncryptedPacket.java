package gg.dragonfruit.network.packet;

import java.math.BigInteger;

import gg.dragonfruit.network.encryption.EndToEndEncryption;

public abstract class DHEncryptedPacket extends Packet {

    String senderPublicKeyStr;
    String numberOfKeysStr = null;

    public void setNumberOfKeys(BigInteger numberOfKeys) {
        this.numberOfKeysStr = numberOfKeys.toString();
    }

    public BigInteger getNumberOfKeys() {
        return new BigInteger(senderPublicKeyStr);
    }

    public void setSenderPublicKey(BigInteger senderPublicKey) {
        this.senderPublicKeyStr = senderPublicKey.toString();
    }

    public BigInteger getSenderPublicKey() {
        return new BigInteger(senderPublicKeyStr);
    }

    public abstract void encrypt(EndToEndEncryption endToEndEncryption);

    public abstract void decrypt(EndToEndEncryption endToEndEncryption);
}
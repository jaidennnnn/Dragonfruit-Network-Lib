package gg.dragonfruit.network.packet;

import java.math.BigInteger;

import gg.dragonfruit.network.encryption.EndToEndEncryption;

public abstract class EncryptedPacket extends Packet {

    public abstract void encrypt(EndToEndEncryption endToEndEncryption, BigInteger recipientKey);

    public abstract void decrypt(EndToEndEncryption endToEndEncryption, BigInteger senderKey);
}

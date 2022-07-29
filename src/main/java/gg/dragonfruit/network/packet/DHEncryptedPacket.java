package gg.dragonfruit.network.packet;

import gg.dragonfruit.network.encryption.EndToEndEncryption;

public abstract class DHEncryptedPacket extends Packet {

    public abstract void encrypt(EndToEndEncryption endToEndEncryption);

    public abstract void decrypt(EndToEndEncryption endToEndEncryption);
}

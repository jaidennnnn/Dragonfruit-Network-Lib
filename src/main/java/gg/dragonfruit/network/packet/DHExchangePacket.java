package gg.dragonfruit.network.packet;

import java.math.BigInteger;

import gg.dragonfruit.network.Connection;
import gg.dragonfruit.network.encryption.EndToEndEncryption;

public class DHExchangePacket extends Packet {

    String publicKeyStr;

    public DHExchangePacket(BigInteger publicKey) {
        this.publicKeyStr = publicKey.toString();
    }

    @Override
    public void received(Connection connection) {
        connection.sendPacket(new DHPublicKeyPacket(
                connection.getSelfEndToEndEncryption().getPublicKey()));
        connection.setOtherPublicKey(new BigInteger(publicKeyStr));
    }

    public void encrypt(EndToEndEncryption endToEndEncryption, BigInteger sharedKey) {
        this.publicKeyStr = endToEndEncryption.encrypt(publicKeyStr, sharedKey);
    }

    public void decrypt(EndToEndEncryption endToEndEncryption) {
        this.publicKeyStr = endToEndEncryption.decrypt(publicKeyStr, endToEndEncryption.getSharedKey());
    }

}

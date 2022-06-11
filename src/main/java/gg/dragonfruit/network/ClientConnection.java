package gg.dragonfruit.network;

import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.concurrent.CompletableFuture;

import org.snf4j.core.session.IDatagramSession;

import gg.dragonfruit.network.encryption.RSAEncryption;

public class ClientConnection extends Connection {
    boolean waitingForKeyNumber = true;
    PublicKey serverRSAPublicKey = null;
    PrivateKey serverRSAPrivateKey = null;
    String toEncrypt;
    SecureRandom SECURE_RANDOM = new SecureRandom();

    public ClientConnection(InetAddress address, int port, IDatagramSession session) {
        super(address, port, session);
    }

    @Override
    public PublicKey getNewRSAPublicKey() {
        KeyPair keyPair = RSAEncryption.generateKeyPair();
        serverRSAPrivateKey = keyPair.getPrivate();
        return serverRSAPublicKey = keyPair.getPublic();
    }

    public void randomStr() {
        byte[] array = new byte[64];
        SECURE_RANDOM.nextBytes(array);
        toEncrypt = new String(array, Charset.forName("UTF-8"));
    }

    public String getStringForVerification() {
        return toEncrypt;
    }

    @Override
    public void setKeyNumber(BigInteger numberOfKeys) {
        waitingForKeyNumber = false;
        super.setKeyNumber(numberOfKeys);
    }

    CompletableFuture<Void> awaitKeyNumber() {
        return CompletableFuture.runAsync(() -> {
            while (waitingForKeyNumber) {

            }
        });
    }

    @Override
    public PrivateKey getRSAPrivateKey() {
        return serverRSAPrivateKey;
    }
}

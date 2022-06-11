package gg.dragonfruit.network;

import java.math.BigInteger;
import java.net.InetAddress;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.CompletableFuture;

import org.snf4j.core.session.IDatagramSession;

import gg.dragonfruit.network.encryption.RSAEncryption;

public class ClientConnection extends Connection {
    boolean waitingForKeyNumber = true;
    PublicKey serverRSAPublicKey = null;
    PrivateKey serverRSAPrivateKey = null;

    public ClientConnection(InetAddress address, int port, IDatagramSession session) {
        super(address, port, session);
    }

    @Override
    public PublicKey getNewRSAPublicKey() {
        KeyPair keyPair = RSAEncryption.generateKeyPair();
        this.serverRSAPrivateKey = keyPair.getPrivate();
        this.serverRSAPublicKey = keyPair.getPublic();
        keyStorage.storeServerRSAPublicKey(serverRSAPublicKey);
        keyStorage.storeServerRSAPrviateKey(serverRSAPrivateKey);
        return this.serverRSAPublicKey;
    }

    @Override
    public void initializeKeyStorage(KeyStorage<?> keyStorage) {
        super.initializeKeyStorage(keyStorage);
        this.serverRSAPublicKey = keyStorage.getServerRSAPublicKey();
        this.serverRSAPrivateKey = keyStorage.getServerRSAPrivateKey();
    }

    @Override
    public void setKeyNumber(BigInteger numberOfKeys) {
        this.waitingForKeyNumber = false;
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

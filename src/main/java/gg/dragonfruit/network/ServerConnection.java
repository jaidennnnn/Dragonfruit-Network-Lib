package gg.dragonfruit.network;

import java.net.InetAddress;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.snf4j.core.session.IDatagramSession;

import gg.dragonfruit.network.util.BigIntegerCache;

public class ServerConnection extends Connection {

    public ServerConnection(InetAddress address, int port, IDatagramSession session) {
        super(address, port, session);
        this.numberOfKeys = BigIntegerCache.NUMBER_OF_KEYS;
    }

    @Override
    public PublicKey getNewRSAPublicKey() {
        return ClientHandler.getNewRSAPublicKey();
    }

    @Override
    public PrivateKey getRSAPrivateKey() {
        return ClientHandler.RSA_PRIVATE_KEY;
    }
}

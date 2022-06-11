package gg.dragonfruit.network;

import java.security.PrivateKey;
import java.security.PublicKey;

public abstract class KeyStorage<T> {

    T object;

    public KeyStorage(T object) {
        this.object = object;
    }

    public abstract void storeServerRSAPublicKey(PublicKey key);

    public abstract void storeServerRSAPrviateKey(PrivateKey key);

    public abstract void storeRSAPublicKey(PublicKey key);

    public abstract PublicKey getRSAPublicKey();

    public abstract PublicKey getServerRSAPublicKey();

    public abstract PrivateKey getServerRSAPrivateKey();
}

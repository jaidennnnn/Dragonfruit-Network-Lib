package gg.dragonfruit.network;

import java.math.BigInteger;

public abstract class KeyStorage<T> {

    T object;

    public KeyStorage(T object) {
        this.object = object;
    }

    public abstract void storeOtherPublicKey(BigInteger key);

    public abstract void storePrivateKey(BigInteger key);

    public abstract void storeKeyNumber(BigInteger keyNumber);

    public abstract BigInteger getOtherPublicKey();

    public abstract BigInteger getPrivateKey();

    public abstract BigInteger getKeyNumber();
}

package gg.dragonfruit.network.util;

import java.math.BigInteger;
import java.security.SecureRandom;

public class BigIntegerCache {
    public static final BigInteger NUMBER_OF_KEYS = BigInteger.probablePrime(6144, new SecureRandom());
    public static final BigInteger SMALL_PRIME = new BigInteger("11");
}

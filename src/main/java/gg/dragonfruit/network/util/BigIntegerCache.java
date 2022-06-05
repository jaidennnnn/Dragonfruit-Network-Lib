package gg.dragonfruit.network.util;

import java.math.BigInteger;
import java.security.SecureRandom;

public class BigIntegerCache {
    public static final BigInteger NUMBER_OF_KEYS = BigInteger.probablePrime(6144, new SecureRandom());
    public static final BigInteger SMALL_PRIME = new BigInteger("11");
    public static final BigInteger BIG_INT_2 = BigInteger.valueOf(2);
    public static final BigInteger BIG_INT_3 = BigInteger.valueOf(3);
    public static final BigInteger BIG_INT_4 = BigInteger.valueOf(4);
    public static final BigInteger BIG_INT_5 = BigInteger.valueOf(5);
    public static final BigInteger BIG_INT_6 = BigInteger.valueOf(6);
    public static final BigInteger BIG_INT_7 = BigInteger.valueOf(7);
}

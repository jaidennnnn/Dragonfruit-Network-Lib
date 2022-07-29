package gg.dragonfruit.network.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import gg.dragonfruit.network.packet.Packet;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4SafeDecompressor;

public class PacketUtil {

    static LZ4Factory factory = LZ4Factory.fastestInstance();

    public static byte[] serializePacket(Packet packet) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(packet);
        byte[] data = out.toByteArray();
        os.close();
        out.close();
        final int decompressedLength = data.length;
        LZ4Compressor compressor = factory.fastCompressor();
        int maxCompressedLength = compressor.maxCompressedLength(decompressedLength);
        byte[] compressed = new byte[maxCompressedLength];
        compressor.compress(data, 0, decompressedLength, compressed, 0, maxCompressedLength);
        return data;
    }

    public static Packet deserializePacket(byte[] data)
            throws IOException, ClassNotFoundException {
        LZ4SafeDecompressor decompressor = factory.safeDecompressor();
        byte[] restored = new byte[0];
        decompressor.decompress(data, restored);
        ByteArrayInputStream in = new ByteArrayInputStream(restored);
        ObjectInputStream is = new ObjectInputStream(in);
        Packet packet = (Packet) is.readObject();
        is.close();
        return packet;
    }
}

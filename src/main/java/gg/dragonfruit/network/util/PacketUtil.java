package gg.dragonfruit.network.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import gg.dragonfruit.network.packet.Packet;

public class PacketUtil {

    public static byte[] serializePacket(Packet packet) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FSTObjectOutput os = new FSTObjectOutput(out);
        os.writeObject(packet);
        byte[] data = out.toByteArray();
        os.close();
        out.close();
        return data;
    }

    public static Packet deserializePacket(byte[] data)
            throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        FSTObjectInput is = new FSTObjectInput(in);
        Packet packet = (Packet) is.readObject();
        is.close();
        return packet;
    }
}

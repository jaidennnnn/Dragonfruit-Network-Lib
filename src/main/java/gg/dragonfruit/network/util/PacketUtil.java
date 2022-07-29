package gg.dragonfruit.network.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import gg.dragonfruit.network.packet.Packet;

public class PacketUtil {

    public static byte[] serializePacket(Packet packet) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(packet);
        byte[] data = out.toByteArray();
        os.close();
        out.close();
        return data;
    }

    public static Packet deserializePacket(byte[] data)
            throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        Packet packet = (Packet) is.readObject();
        is.close();
        return packet;
    }
}

package gg.dragonfruit.network.util;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;

public class DatagramChannelBuilder {
    public static DatagramChannel openChannel() throws IOException {
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);
        return datagramChannel;
    }

    public static DatagramChannel bindChannel(SocketAddress local) throws IOException {
        return openChannel().bind(local);
    }

    public static DatagramChannel connect(SocketAddress remote) throws IOException {
        return openChannel().connect(remote);
    }
}

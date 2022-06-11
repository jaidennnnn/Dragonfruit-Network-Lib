package gg.dragonfruit.network.listener;

import java.util.Arrays;
import java.util.List;

import gg.dragonfruit.network.ClientConnection;
import gg.dragonfruit.network.collection.GlueList;

public abstract class ConnectionListener {
    static List<ConnectionListener> listeners = new GlueList<ConnectionListener>();

    public static void addListeners(ConnectionListener... listener) {
        listeners.addAll(Arrays.asList(listener));
    }

    public static List<ConnectionListener> getListeners() {
        return listeners;
    }

    public abstract void connected(ClientConnection connection);

    public abstract void disconnected(ClientConnection connection);
}

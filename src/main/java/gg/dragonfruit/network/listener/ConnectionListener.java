package gg.dragonfruit.network.listener;

import java.util.Arrays;
import java.util.List;

import gg.dragonfruit.network.Connection;
import gg.dragonfruit.network.collection.GlueList;

public abstract class ConnectionListener {
    static List<ConnectionListener> listeners = new GlueList<ConnectionListener>();

    public static void addListeners(ConnectionListener... listener) {
        listeners.addAll(Arrays.asList(listener));
    }

    public static List<ConnectionListener> getListeners() {
        return listeners;
    }

    public abstract void connected(Connection connection);

    public abstract void disconnected(Connection connection);
}

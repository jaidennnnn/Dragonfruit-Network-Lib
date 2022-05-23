package gg.dragonfruit.network.packet;

import java.io.Serializable;

import gg.dragonfruit.network.Connection;

public abstract class Packet implements Serializable {

    public abstract void received(Connection connection);
}

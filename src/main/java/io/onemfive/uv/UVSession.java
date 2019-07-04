package io.onemfive.uv;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class UVSession {

    private static final Logger LOG = Logger.getLogger(UVSession.class.getName());

    private Destination localDestination;
    private List<UVSessionListener> listeners = new ArrayList<>();

    Destination getLocalDestination() {
        return localDestination;
    }

    Destination lookupDestination(String address) {
        Destination dest = null;

        return dest;
    }

    boolean sendMessage(Destination toDestination, UVDatagram datagram, Properties options) {
        LOG.warning("RadioSession.sendMessage() not implemented.");
        return false;
    }

    byte[] receiveMessage(int msgId) {
        return null;
    }

    boolean connect() {
        return false;
    }

    void addSessionListener(UVSessionListener listener) {
        listeners.add(listener);
    }

    void removeSessionListener(UVSessionListener listener) {
        listeners.remove(listener);
    }
}

package io.onemfive.lifi;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class LiFiSession {

    private static final Logger LOG = Logger.getLogger(LiFiSession.class.getName());

    private Destination localDestination;
    private List<LiFiSessionListener> listeners = new ArrayList<>();

    Destination getLocalDestination() {
        return localDestination;
    }

    Destination lookupDestination(String address) {
        Destination dest = null;

        return dest;
    }

    boolean sendMessage(Destination toDestination, LiFiDatagram datagram, Properties options) {
        LOG.warning("LiFISession.sendMessage() not implemented.");
        return false;
    }

    byte[] receiveMessage(int msgId) {
        return null;
    }

    boolean connect() {
        return false;
    }

    void addSessionListener(LiFiSessionListener listener) {
        listeners.add(listener);
    }

    void removeSessionListener(LiFiSessionListener listener) {
        listeners.remove(listener);
    }
}

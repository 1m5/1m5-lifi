package io.onemfive.lifi;

import io.onemfive.data.Addressable;
import io.onemfive.data.DID;
import io.onemfive.data.JSONSerializable;
import io.onemfive.data.NetworkPeer;

/**
 * A peer on the LiFi network.
 */
public class LiFiPeer extends NetworkPeer implements Addressable, JSONSerializable {

    public LiFiPeer() {
        this(null, null);
    }

    public LiFiPeer(String username, String passphrase) {
        super(NetworkPeer.Network.LIFI.name(), username, passphrase);
    }

    @Override
    public Object clone() {
        LiFiPeer clone = new LiFiPeer();
        clone.did = (DID)did.clone();
        clone.network = network;
        return clone;
    }
}

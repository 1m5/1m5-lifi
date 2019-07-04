package io.onemfive.uv;

import io.onemfive.data.Addressable;
import io.onemfive.data.DID;
import io.onemfive.data.JSONSerializable;
import io.onemfive.data.NetworkPeer;

/**
 * A peer on the UV network.
 */
public class UVPeer extends NetworkPeer implements Addressable, JSONSerializable {

    public UVPeer() {
        this(null, null);
    }

    public UVPeer(String username, String passphrase) {
        super(NetworkPeer.Network.UV.name(), username, passphrase);
    }

    @Override
    public Object clone() {
        UVPeer clone = new UVPeer();
        clone.did = (DID)did.clone();
        clone.network = network;
        return clone;
    }
}

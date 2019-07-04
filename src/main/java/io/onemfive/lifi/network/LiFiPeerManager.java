package io.onemfive.lifi.network;

import io.onemfive.data.NetworkPeer;
import io.onemfive.sensors.PeerManager;

import java.util.Map;

public class LiFiPeerManager implements PeerManager {

    @Override
    public NetworkPeer getLocalPeer() {
        return null;
    }

    @Override
    public void savePeer(NetworkPeer networkPeer) {

    }

    @Override
    public Map<String, NetworkPeer> getAllPeers(NetworkPeer networkPeer) {
        return null;
    }

    @Override
    public Integer totalKnownPeers(NetworkPeer networkPeer) {
        return null;
    }

    @Override
    public NetworkPeer getRandomPeer(NetworkPeer networkPeer) {
        return null;
    }
}

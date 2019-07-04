package io.onemfive.uv;

public class UVDatagramExtractor {

    private byte[] payload;
    private Destination sender;

    void extractUVDatagram(byte[] datagram) {
        // Extract payload
        this.payload = null;
        // Extract sender
        this.sender = null;
    }

    byte[] getPayload() {
        return payload;
    }

    Destination getSender() {
        return sender;
    }
}

package io.onemfive.uv;

public class UVDatagramBuilder {

    public static final int DATAGRAM_MAX_SIZE = 32768;
    private UVSession session;

    public UVDatagramBuilder(UVSession session) {
        this.session = session;
    }

    public UVDatagram makeUVDatagram(byte[] payload) {
        return null;
    }
}

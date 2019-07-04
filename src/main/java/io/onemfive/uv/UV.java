package io.onemfive.uv;

/**
 * Interface to use for all Radio calls.
 */
public interface UV {

    int sendMessage(UVDatagram message);
}

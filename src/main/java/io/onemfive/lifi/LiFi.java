package io.onemfive.lifi;

/**
 * Interface to use for all LiFi calls.
 */
public interface LiFi {

    int sendMessage(LiFiDatagram message);
}

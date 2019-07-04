package io.onemfive.uv;

public interface UVSessionListener {
    void messageAvailable(UVSession session, int var2, long var3);

    void disconnected(UVSession session);

    void errorOccurred(UVSession session, String message, Throwable throwable);
}

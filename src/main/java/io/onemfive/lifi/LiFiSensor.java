package io.onemfive.lifi;

import io.onemfive.core.ServiceRequest;
import io.onemfive.core.notification.NotificationService;
import io.onemfive.data.DID;
import io.onemfive.data.Envelope;
import io.onemfive.data.EventMessage;
import io.onemfive.data.NetworkPeer;
import io.onemfive.data.util.DLC;
import io.onemfive.data.util.DataFormatException;
import io.onemfive.sensors.*;

import java.io.*;
import java.util.Properties;
import java.util.logging.Logger;

public class LiFiSensor extends BaseSensor implements LiFiSessionListener {

    private static final Logger LOG = Logger.getLogger(LiFiSensor.class.getName());

    private LiFiSession session;
    private LiFiPeer localNode;

    public LiFiSensor(SensorManager sensorManager, Envelope.Sensitivity sensitivity, Integer priority) {
        super(sensorManager, sensitivity, priority);
    }

    @Override
    public String[] getOperationEndsWith() {
        return new String[]{".lifi"};
    }

    @Override
    public String[] getURLBeginsWith() {
        return new String[]{"lifi"};
    }

    @Override
    public String[] getURLEndsWith() {
        return new String[]{".lifi"};
    }

    /**
     * Sends UTF-8 content to a Destination using LiFi.
     * @param envelope Envelope containing SensorRequest as data.
     *                 To DID must contain base64 encoded LiFi destination key.
     * @return boolean was successful
     */
    @Override
    public boolean send(Envelope envelope) {
        LOG.info("Sending LiFi Message...");
        SensorRequest request = (SensorRequest) DLC.getData(SensorRequest.class,envelope);
        if(request == null){
            LOG.warning("No SensorRequest in Envelope.");
            request.errorCode = ServiceRequest.REQUEST_REQUIRED;
            return false;
        }
        NetworkPeer toPeer = request.to.getPeer(NetworkPeer.Network.LIFI.name());
        if(toPeer == null) {
            LOG.warning("No Peer for LiFi found in toDID while sending to LiFi.");
            request.errorCode = SensorRequest.TO_PEER_REQUIRED;
            return false;
        }
        if(!NetworkPeer.Network.LIFI.name().equals((toPeer.getNetwork()))) {
            LOG.warning("LiFi requires a LiFiPeer.");
            request.errorCode = SensorRequest.TO_PEER_WRONG_NETWORK;
            return false;
        }
        LOG.info("Content to send: "+request.content);
        if(request.content == null) {
            LOG.warning("No content found in Envelope while sending to LiFi.");
            request.errorCode = SensorRequest.NO_CONTENT;
            return false;
        }
        if(request.content.length() > LiFiDatagramBuilder.DATAGRAM_MAX_SIZE) {
            // Just warn for now
            // TODO: Split into multiple serialized datagrams
            LOG.warning("Content longer than "+ LiFiDatagramBuilder.DATAGRAM_MAX_SIZE+". May have issues.");
        }

        Destination toDestination = session.lookupDestination(toPeer.getAddress());
        if(toDestination == null) {
            LOG.warning("LiFi Peer To Destination not found.");
            request.errorCode = SensorRequest.TO_PEER_NOT_FOUND;
            return false;
        }
        LiFiDatagramBuilder builder = new LiFiDatagramBuilder(session);
        LiFiDatagram datagram = builder.makeLiFIDatagram(request.content.getBytes());
        Properties options = new Properties();
        if(session.sendMessage(toDestination, datagram, options)) {
            LOG.info("LiFi Message sent.");
            return true;
        } else {
            LOG.warning("LiFi Message sending failed.");
            request.errorCode = SensorRequest.SENDING_FAILED;
            return false;
        }
    }

    /**
     * Incoming
     * @param envelope
     * @return
     */
    @Override
    public boolean reply(Envelope envelope) {
//        sensorManager.sendToBus(envelope);
        return true;
    }

    /**
     * Will be called only if you register via
     * setSessionListener() or addSessionListener().
     * And if you are doing that, just use LiFiSessionListener.
     *
     * @param session session to notify
     * @param msgId message number available
     * @param size size of the message - why it's a long and not an int is a mystery
     */
    @Override
    public void messageAvailable(LiFiSession session, int msgId, long size) {
        LOG.info("Message received by LiFi Sensor...");
        byte[] msg = session.receiveMessage(msgId);

        LOG.info("Loading LiFi Datagram...");
        LiFiDatagramExtractor d = new LiFiDatagramExtractor();
        d.extractLiFiDatagram(msg);
        LOG.info("LiFi Datagram loaded.");
        byte[] payload = d.getPayload();
        String strPayload = new String(payload);
        LOG.info("Getting sender as LiFi Destination...");
        Destination sender = d.getSender();
        String address = sender.toBase64();
        String fingerprint = null;
        try {
            fingerprint = sender.getHash().toBase64();
        } catch (DataFormatException e) {
            LOG.warning(e.getLocalizedMessage());
        } catch (IOException e) {
            LOG.warning(e.getLocalizedMessage());
        }
        LOG.info("Received LiFi Message:\n\tFrom: " + address +"\n\tContent: " + strPayload);

        Envelope e = Envelope.eventFactory(EventMessage.Type.TEXT);
        NetworkPeer from = new NetworkPeer(NetworkPeer.Network.LIFI.name());
        from.setAddress(address);
        from.setFingerprint(fingerprint);
        DID did = new DID();
        did.addPeer(from);
        e.setDID(did);
        EventMessage m = (EventMessage) e.getMessage();
        m.setName(fingerprint);
        m.setMessage(strPayload);
        DLC.addRoute(NotificationService.class, NotificationService.OPERATION_PUBLISH, e);
        LOG.info("Sending Event Message to Notification Service...");
//        sensorManager.sendToBus(e);
    }

    /**
     * Notify the service that the session has been terminated.
     * All registered listeners will be called.
     *
     * @param session session to report disconnect to
     */
    @Override
    public void disconnected(LiFiSession session) {
        LOG.warning("LiFi Session reporting disconnection.");
        routerStatusChanged();
    }

    /**
     * Notify the client that some throwable occurred.
     * All registered listeners will be called.
     *
     * @param session session to report error occurred
     * @param message message received describing error
     * @param throwable throwable thrown during error
     */
    @Override
    public void errorOccurred(LiFiSession session, String message, Throwable throwable) {
        LOG.severe("Router says: "+message+": "+throwable.getLocalizedMessage());
        routerStatusChanged();
    }

    public void checkRouterStats() {
        LOG.info("LiFiSensor stats:" +
                "\n\t...");
    }

    private void routerStatusChanged() {
        String statusText;
        switch (getStatus()) {
            case NETWORK_CONNECTING:
                statusText = "Testing LiFi Network...";
                break;
            case NETWORK_CONNECTED:
                statusText = "Connected to LiFi Network.";
                restartAttempts = 0; // Reset restart attempts
                break;
            case NETWORK_STOPPED:
                statusText = "Disconnected from LiFi Network.";
                restart();
                break;
            default: {
                statusText = "Unhandled LiFi Network Status: "+getStatus().name();
            }
        }
        LOG.info(statusText);
    }

    /**
     * Sets up a {@link LiFiSession}, using the LiFi Destination stored on disk or creating a new LiFi
     * destination if no key file exists.
     */
    private void initializeSession() throws Exception {
        LOG.info("Initializing LiFi Session....");
        updateStatus(SensorStatus.INITIALIZING);

        Properties sessionProperties = new Properties();
        session = new LiFiSession();
        session.connect();

        Destination localDestination = session.getLocalDestination();
        String address = localDestination.toBase64();
        String fingerprint = localDestination.getHash().toBase64();
        LOG.info("LiFiSensor Local destination key in base64: " + address);
        LOG.info("LiFiSensor Local destination fingerprint (hash) in base64: " + fingerprint);

        session.addSessionListener(this);

        NetworkPeer np = new NetworkPeer(NetworkPeer.Network.LIFI.name());
        np.getDid().getPublicKey().setFingerprint(fingerprint);
        np.getDid().getPublicKey().setAddress(address);

        DID localDID = new DID();
        localDID.addPeer(np);

        // Publish local LiFi address
        LOG.info("Publishing LiFi Network Peer's DID...");
        Envelope e = Envelope.eventFactory(EventMessage.Type.STATUS_DID);
        EventMessage m = (EventMessage) e.getMessage();
        m.setName(fingerprint);
        m.setMessage(localDID);
        DLC.addRoute(NotificationService.class, NotificationService.OPERATION_PUBLISH, e);
//        sensorManager.sendToBus(e);
    }

    public LiFiPeer getLocalNode() {
        return localNode;
    }

    @Override
    public boolean start(java.util.Properties properties) {
        return false;
    }

    @Override
    public boolean pause() {
        return false;
    }

    @Override
    public boolean unpause() {
        return false;
    }

    @Override
    public boolean restart() {
        return false;
    }

    @Override
    public boolean shutdown() {
        return false;
    }

    @Override
    public boolean gracefulShutdown() {
        return false;
    }
}

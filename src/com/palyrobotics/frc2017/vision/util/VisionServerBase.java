package com.palyrobotics.frc2017.vision.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Controls managing client and server sockets.
 *
 * @author Quintin Dwight
 */
public abstract class VisionServerBase extends VisionThreadBase {

    public enum ServerState {
        PRE_INIT, ATTEMPTING_CONNECTION, OPEN
    }

    protected int m_port = 0;
    protected boolean m_isRestartingSocket;
    protected ServerSocket m_server;
    protected Socket m_client = new Socket();
    protected ServerState m_serverState = ServerState.PRE_INIT;

    protected VisionServerBase(final String k_threadName) {
        super(k_threadName);
    }

    @Override
    @Deprecated
    public void start(final int k_updateRate) {

    }

    /**
     * Starts the server thread
     *
     * @param k_updateRate Update rate of the thread
     * @param k_port The port to connect the server to
     * @param k_isRestartingSocket Whether or not this socket constantly restarts
     */
    public void start(final int k_updateRate, final int k_port, final boolean k_isRestartingSocket)
    {
        super.start(k_updateRate);

        m_port = k_port;
        m_isRestartingSocket = k_isRestartingSocket;
    }

    @Override
    protected void init() {

        if (m_serverState != ServerState.PRE_INIT) {
            log("Thread has already been initialized. Aborting...");
            return;
        }

        setServerState(ServerState.ATTEMPTING_CONNECTION);
    }

    /**
     * Check if the client socket is connected
     *
     * @return New server state, depends on whether or not we should attempt conncetion
     */
    protected ServerState checkConnection() {

        final boolean notConnected = !m_client.isConnected(), closed = m_client.isClosed(), shouldRetry = notConnected || closed;

//        if (notConnected) log("[Warning] Lost connection to port: " + Integer.toString(m_port));
//        if (closed) log("[Warning] Connection was closed on port: " + Integer.toString(m_port));

    	return shouldRetry ? ServerState.ATTEMPTING_CONNECTION : ServerState.OPEN;
    }

    /**
     * Sets the state of the server
     *
     * @param state State of the server
     */
    protected void setServerState(ServerState state) {
        m_serverState = state;
    }

    /**
     * Pauses the thread until a connection is established
     *
     * @return The state after execution
     */
    private ServerState acceptConnection(){

        try {

            //if (!m_isRestartingSocket) log("Trying to connect to client on port: " + Integer.toString(m_port) + "...");

            if (m_server != null) m_server.close();
            m_server = new ServerSocket(m_port);
            m_server.setReuseAddress(true);

            // Pause thread until we accept from the client
            m_client = m_server.accept();

            //if (!m_isRestartingSocket) log("Connected to client on port: " + m_client.getPort() + "!");

            return ServerState.OPEN;

        } catch (IOException e) {

            e.printStackTrace();
            return ServerState.ATTEMPTING_CONNECTION;
        }
    }

    protected void closeClient()
    {
        try {
            m_client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void update()
    {
        switch (m_serverState){
            case PRE_INIT:
//                log("Thread is not initialized while in update.");
                break;
            case ATTEMPTING_CONNECTION:
                setServerState(acceptConnection());
                break;
			case OPEN:
				setServerState(checkConnection());
				break;
        }

        afterUpdate();
    }

    protected abstract void afterUpdate();
}

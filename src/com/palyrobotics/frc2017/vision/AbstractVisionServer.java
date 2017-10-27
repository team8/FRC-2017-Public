package com.palyrobotics.frc2017.vision;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Controls managing client and server sockets.
 *
 * @author Quintin Dwight
 */
public abstract class AbstractVisionServer extends AbstractVisionThread {

    public enum ServerState {
        PRE_INIT, ATTEMPTING_CONNECTION, OPEN
    }

    protected int m_port = 0;
    protected ServerSocket m_server;
    protected Socket m_client = new Socket();
    protected ServerState m_serverState = ServerState.PRE_INIT;

    protected AbstractVisionServer(final String k_threadName) {
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
     */
    public void start(final int k_updateRate, final int k_port)
    {
        super.start(k_updateRate);

        m_port = k_port;
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

    	final boolean connectedAndOpen = !m_client.isConnected() || m_client.isClosed();
    	
    	if (!connectedAndOpen) {
    		log("Lost connection to socket");
    	}
    	
    	return connectedAndOpen ? ServerState.OPEN : ServerState.ATTEMPTING_CONNECTION;
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

            log("Trying to connect to client on port: " + Integer.toString(m_port) + "...");

            if (m_server != null) m_server.close();
            m_server = new ServerSocket(m_port);
            m_server.setReuseAddress(true);

            // Pause thread until we accept from the client
            m_client = m_server.accept();

            log("Connected to client on port: " + m_client.getPort() + "!");

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
                log("Thread is not initialized while in update.");
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

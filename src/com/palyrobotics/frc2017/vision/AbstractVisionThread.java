package com.palyrobotics.frc2017.vision;

/**
 * Base class for vision threads
 *
 * @author Quintin Dwight
 */
public abstract class AbstractVisionThread implements Runnable {

    protected double m_timeAlive;
    protected final int k_updateRate;
    protected final String k_threadName;
    protected boolean m_isRunning;

    public double getTimeAlive() { return m_timeAlive; }
    public boolean isRunning() { return m_isRunning; }

    protected AbstractVisionThread(final int k_updateRate, final String k_threadName) {
        this.k_updateRate = k_updateRate;
        this.k_threadName = k_threadName;
    }

    /**
     * Starts the thread
     */
    public void start() {

        if (m_isRunning) {
            System.out.println("[Error] Thread " + k_threadName + " is already running! Aborting...");
            return;
        }

        init();

        System.out.println("[Info] Starting thread " + k_threadName + "...");
        m_isRunning = true;
        new Thread(this).start();
    }

    /**
     * Called by {@link #start} after it has been verified that the thread can run
     */
    protected abstract void init();

    @Override
    public void run() {

        while (m_isRunning) {

            update();

            try {
                Thread.sleep(k_updateRate);
                m_timeAlive += k_updateRate / 1000.0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Called by {@link #run} every time the thread updates
     */
    protected abstract void update();

    /**
     * Handles destroying the thread
     */
    protected void destroy() {

        m_isRunning = false;
        tearDown();
    }

    protected void log(String message) {

        System.out.println("[" + k_threadName + "] " + message);
    }

    /**
     * Called by {@link #destroy} whenever the thread should stop running
     */
    protected abstract void tearDown();
}

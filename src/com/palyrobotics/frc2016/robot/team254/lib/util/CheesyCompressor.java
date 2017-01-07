package com.palyrobotics.frc2016.robot.team254.lib.util;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;

public class CheesyCompressor {
    public static int UPDATE_HZ = 25;
    protected Relay m_compressor_relay;
    protected DigitalInput m_pressure_switch;

    Loop updater = new Loop() {
        @Override
        public void update() {
            boolean turn_on = !m_pressure_switch.get();
            m_compressor_relay.set(turn_on ? Relay.Value.kOn : Relay.Value.kOff);
        }
        @Override
        public void onStop() {
			// TODO Auto-generated method stub
        }
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
		}
    };

    protected Looper looper = new Looper();

    public CheesyCompressor(Relay compressor_relay, DigitalInput pressure_switch) {
        m_compressor_relay = compressor_relay;
        m_pressure_switch = pressure_switch;
        m_compressor_relay.setDirection(Relay.Direction.kForward);
        looper.start();
    }

    public void start() {
        looper.start();
    }

    public void stop() {
        looper.stop();
        m_compressor_relay.set(Relay.Value.kOff);
    }
}

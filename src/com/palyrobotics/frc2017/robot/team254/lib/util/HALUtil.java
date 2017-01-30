package com.palyrobotics.frc2017.robot.team254.lib.util;

import java.nio.ByteBuffer;

public class HALUtil extends JNIWrapper {
	public static final int NULL_PARAMETER = -1005;
	public static final int SAMPLE_RATE_TOO_HIGH = 1001;
	public static final int VOLTAGE_OUT_OF_RANGE = 1002;
	public static final int LOOP_TIMING_ERROR = 1004;

	public HALUtil() {
	}

	public static native ByteBuffer initializeMutexNormal();

	public static native void deleteMutex(ByteBuffer paramByteBuffer);

	public static native byte takeMutex(ByteBuffer paramByteBuffer);

	public static native ByteBuffer initializeMultiWait();

	public static native void deleteMultiWait(ByteBuffer paramByteBuffer);

	public static native byte takeMultiWait(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2, int paramInt);

	public static native short getFPGAVersion(java.nio.IntBuffer paramIntBuffer);

	public static native int getFPGARevision(java.nio.IntBuffer paramIntBuffer);

	public static native long getFPGATime(java.nio.IntBuffer paramIntBuffer);

	public static native boolean getFPGAButton(java.nio.IntBuffer paramIntBuffer);

	public static native String getHALErrorMessage(int paramInt);

	public static native int getHALErrno();

	public static native String getHALstrerror(int paramInt);

	public static String getHALstrerror() {
		return getHALstrerror(getHALErrno());
	}

	public static final int INCOMPATIBLE_STATE = 1015;
	public static final int ANALOG_TRIGGER_PULSE_OUTPUT_ERROR = -1011;

	public static void checkStatus(java.nio.IntBuffer status) {
		int s = status.get(0);
		if (s < 0) {
			String message = getHALErrorMessage(s);
			throw new RuntimeException(" Code: " + s + ". " + message);
		}
		if (s > 0) {
			String message = getHALErrorMessage(s);
			edu.wpi.first.wpilibj.DriverStation.reportError(message, true);
		}
	}

	public static final int NO_AVAILABLE_RESOURCES = -104;
	public static final int PARAMETER_OUT_OF_RANGE = -1028;
}

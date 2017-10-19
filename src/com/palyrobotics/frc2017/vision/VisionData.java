package com.palyrobotics.frc2017.vision;

import com.palyrobotics.frc2017.vision.util.DataExistsCallback;
import com.palyrobotics.frc2017.vision.util.SnychronizedVisionDataUnit;
import com.palyrobotics.frc2017.vision.util.VisionDataUnit;

public class VisionData {
	private static class DoubleExistsCallback extends DataExistsCallback<Double> {
		@Override
		public boolean exists(Double data) {
			return !(data == null || data.isNaN() || data.isInfinite());
		}
	}
	private static VisionDataUnit<Double> x_data = new SnychronizedVisionDataUnit<Double>("x_dist", Double.NaN, null, new DoubleExistsCallback());
	private static VisionDataUnit<Double> z_data = new SnychronizedVisionDataUnit<Double>("z_dist", Double.NaN, null, new DoubleExistsCallback());
	
	public static double getXData() {
		return x_data.get();
	}
	
	public static double getZData() {
		return z_data.get();
	}
	
	public static void setXData(Double x) {
		x_data.set(x);
	}
	
	public static void setZData(Double z) {
		z_data.set(z);
	}
	
}

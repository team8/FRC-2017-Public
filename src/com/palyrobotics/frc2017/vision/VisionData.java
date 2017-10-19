package com.palyrobotics.frc2017.vision;

import com.palyrobotics.frc2017.vision.util.DataExistsCallback;
import com.palyrobotics.frc2017.vision.util.SnychronizedVisionDataUnit;
import com.palyrobotics.frc2017.vision.util.VisionDataUnit;

public class VisionData {
	private class DoubleExistsCallback extends DataExistsCallback<Double> {
		@Override
		public boolean exists(Double data) {
			return !(data == null || data.isNaN() || data.isInfinite());
		}
	}
	public static VisionDataUnit<Double> x_data = new SnychronizedVisionDataUnit<Double>("x_dist", Double.NaN, null, new DoubleExistsCallback());
	public static VisionDataUnit<Double> z_data = new SnychronizedVisionDataUnit<Double>("z_dist", Double.NaN, null, new DoubleExistsCallback());
	
}

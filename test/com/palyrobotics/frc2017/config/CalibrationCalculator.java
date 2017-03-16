package com.palyrobotics.frc2017.config;

import org.junit.Test;

public class CalibrationCalculator {	
	@Test
	public void calculateLoadingSide() {
		System.out.println("Loading side");
		double left = Constants.kSidePegDistanceLoadingStationInches * Constants.kDriveTicksPerInch;
		double right = left;
		System.out.println("Forward: "+left+","+right);
		left += (60 * Constants.kDriveInchesPerDegree * Constants.kDriveTicksPerInch);
		right -= (60 * Constants.kDriveInchesPerDegree * Constants.kDriveTicksPerInch);
		System.out.println("Turn: "+left+","+right);
		left += Constants.kSidePegDistanceToAirshipLoadingStationInches * Constants.kDriveTicksPerInch;
		right += Constants.kSidePegDistanceToAirshipLoadingStationInches * Constants.kDriveTicksPerInch;
		System.out.println("Airship: "+left+","+right);
		System.out.println("");
	}
	
	@Test
	public void calculateBoilerSide() {
		System.out.println("Boiler side");
		double left = Constants.kSidePegDistanceBoilerInches * Constants.kDriveTicksPerInch;
		double right = left;
		System.out.println("Forward: "+left+","+right);
		left += (60 * Constants.kDriveInchesPerDegree * Constants.kDriveTicksPerInch);
		right -= (60 * Constants.kDriveInchesPerDegree * Constants.kDriveTicksPerInch);
		System.out.println("Turn: "+left+","+right);
		left += Constants.kSidePegDistanceToAirshipBoilerInches * Constants.kDriveTicksPerInch;
		right += Constants.kSidePegDistanceToAirshipBoilerInches * Constants.kDriveTicksPerInch;
		System.out.println("Airship: "+left+","+right);
		System.out.println("");
	}
	
	@Test
	public void calculateBaseline() {
		System.out.println("Base line");
		System.out.println(Constants.kBaseLineDistanceInches * Constants.kDriveTicksPerInch);
		System.out.println("");
	}
	
	@Test
	public void calculateCenterPeg() {
		System.out.println("Center peg");
		System.out.println(Constants.kCenterPegDistanceInches * Constants.kDriveTicksPerInch);
		System.out.println("");
	}
	
	@Test
	public void generalCalibration() {
		System.out.println("Inches to ticks: " + Constants.kDriveTicksPerInch);
		System.out.println("Inches per degree: " + Constants.kDriveInchesPerDegree);
		System.out.println("");
	}
}

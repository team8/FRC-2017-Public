package com.palyrobotics.frc2017.robot;

import com.ctre.CANTalon;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
import com.palyrobotics.frc2017.robot.team254.lib.util.ADXRS453_Gyro;

import edu.wpi.first.wpilibj.*;
/**
 * Represents all hardware components of the robot.
 * Singleton class. Should only be used in robot package, and 254lib.
 * Subdivides hardware into subsystems.
 * Example call: HardwareAdapter.getInstance().getDrivetrain().getLeftMotor()
 *
 * @author Nihar
 */
public class HardwareAdapter {
	// Hardware components at the top for maintenance purposes, variables and getters at bottom
	/* 
	 * DRIVETRAIN - 6 CANTalons
	 */
	public static class DrivetrainHardware {
		private static DrivetrainHardware instance = new DrivetrainHardware();

		protected static DrivetrainHardware getInstance() {
			return instance;
		}
		public final CANTalon leftSlave1Talon;
		public final CANTalon leftMasterTalon;
		public final CANTalon leftSlave2Talon;
		public final CANTalon rightSlave1Talon;
		public final CANTalon rightMasterTalon;
		public final CANTalon rightSlave2Talon;

		// If encoders are wired directly to RIO use the following objects
//		public final Encoder leftEncoder;
//		public final Encoder rightEncoder;
		public final ADXRS453_Gyro gyro;

		private DrivetrainHardware() {
			if(Constants.kRobotName == Constants.RobotName.DERICA) {
				leftMasterTalon = new CANTalon(Constants2016.kDericaLeftDriveMasterDeviceID);
				leftSlave1Talon = new CANTalon(Constants2016.kDericaLeftDriveSlaveDeviceID);
				leftSlave2Talon = null;
				rightMasterTalon = new CANTalon(Constants2016.kDericaRightDriveMasterDeviceID);
				rightSlave1Talon = new CANTalon(Constants2016.kDericaRightDriveSlaveDeviceID);
				rightSlave2Talon = null;
				gyro = new ADXRS453_Gyro();
			} else {
				leftMasterTalon = new CANTalon(Constants.kSteikLeftDriveMasterDeviceID);
				leftSlave1Talon = new CANTalon(Constants.kSteikLeftDriveSlaveDeviceID);
				leftSlave2Talon = new CANTalon(Constants.kSteikLeftDriveOtherSlaveDeviceID);
				rightMasterTalon = new CANTalon(Constants.kSteikRightDriveMasterDeviceID);
				rightSlave1Talon = new CANTalon(Constants.kSteikRightDriveSlaveDeviceID);
				rightSlave2Talon = new CANTalon(Constants.kSteikRightDriveOtherSlaveDeviceID);
				ADXRS453_Gyro tempGyro = new ADXRS453_Gyro();
				if (tempGyro.isSPINull()) {
					gyro = null;
				} else {
					gyro = tempGyro;
				}
			}
		}
	}

	/**
	 * FLIPPERS - 2 double solenoids
	 */
	public static class FlippersHardware {
		private static FlippersHardware instance = new FlippersHardware();
		public static FlippersHardware getInstance() {
			return instance;
		}
		public final DoubleSolenoid leftSolenoid, rightSolenoid;

		private FlippersHardware() {
			if (Constants.kRobotName == Constants.RobotName.STEIK) {
				leftSolenoid = new DoubleSolenoid(
						Constants.kSteikLeftFlipperPortExtend, Constants.kSteikLeftFlipperPortRetract);
				rightSolenoid = new DoubleSolenoid(
						Constants.kSteikRightFlipperPortExtend, Constants.kSteikRightFlipperPortRetract);
			} else {
				leftSolenoid = null;
				rightSolenoid = null;
			}
		}
	}

	/**
	 * SLIDER - 1 CANTalon
	 */
	public static class SliderHardware {
		private static SliderHardware instance = new SliderHardware();
		
		protected static SliderHardware getInstance() {
			return instance;
		}
		public final CANTalon sliderTalon;
		public final AnalogInput sliderPotentiometer;
		
		private SliderHardware() {
			if (Constants.kRobotName == Constants.RobotName.STEIK){
				sliderTalon = new CANTalon(Constants.kSteikSliderMotorDeviceID);
				sliderPotentiometer = new AnalogInput(Constants.kSteikSliderPotentiometerPort);
			}
			else {
				sliderTalon = null;
				sliderPotentiometer = null;
			}
		}
	}

	/**
	 * SPATULA - 1 double solenoid
	 */
	public static class SpatulaHardware {
		private static SpatulaHardware instance = new SpatulaHardware();
		public static SpatulaHardware getInstance() {
			return instance;
		}
		public final DoubleSolenoid spatulaSolenoid;

		private SpatulaHardware() {
			if (Constants.kRobotName == Constants.RobotName.STEIK) {
				spatulaSolenoid = new DoubleSolenoid(Constants.kSteikSpatulaPortExtend, Constants.kSteikSpatulaPortRetract);
			} else {
				spatulaSolenoid = null;
			}
		}
	}
	
	/*
	 * INTAKE - 2 VictorSPs
	 */
	public static class IntakeHardware {
		private static IntakeHardware instance = new IntakeHardware();

		protected static IntakeHardware getInstance() {
			return instance;
		}
		public final VictorSP leftIntakeMotor;
		public final VictorSP rightIntakeMotor;

		private IntakeHardware() {
			if (Constants.kRobotName == Constants.RobotName.STEIK) {
				leftIntakeMotor = new VictorSP(Constants.kSteikLeftIntakeMotorDeviceID);
				rightIntakeMotor = new VictorSP(Constants.kSteikRightIntakeMotorDeviceID);
			} else {
				leftIntakeMotor = null;
				rightIntakeMotor = null;
			}
		}
	}

	/*
	 * CLIMBER - 1 CANTalon
	 */
	public static class ClimberHardware {
		private static ClimberHardware instance = new ClimberHardware();
		
		protected static ClimberHardware getInstance(){
			return instance;
		}
		public final CANTalon climberTalon;
		
		private ClimberHardware() {
			if (Constants.kRobotName == Constants.RobotName.STEIK) {
				climberTalon = new CANTalon(Constants.kSteikClimberMotorDeviceID);
			} else {
				climberTalon = null;
			}
		}
	}

	// Joysticks for operator interface
	protected static class Joysticks {
		private static Joysticks instance = new Joysticks();

		public static Joysticks getInstance() {
			return instance;
		}

		public final Joystick driveStick = new Joystick(0);
		public final Joystick turnStick = new Joystick(1);
		public final Joystick sliderStick = new Joystick(2);
		public final Joystick climberStick = new Joystick(3);

		private Joysticks() {
		}
	}

	// Wrappers to access hardware groups
	public DrivetrainHardware getDrivetrain() {
		return DrivetrainHardware.getInstance();
	}
	public FlippersHardware getFlippers() {
		return FlippersHardware.getInstance();
	}
	public SliderHardware getSlider() {
		return SliderHardware.getInstance();
	}
	public SpatulaHardware getSpatula() {
		return SpatulaHardware.getInstance();
	}
	public IntakeHardware getIntake() {
		return IntakeHardware.getInstance();
	}
	public ClimberHardware getClimber() {
		return ClimberHardware.getInstance();
	}
	public Joysticks getJoysticks() {
		return Joysticks.getInstance();
	}
	
	public final PowerDistributionPanel kPDP = new PowerDistributionPanel();

	// Singleton set up
	private static final HardwareAdapter instance = new HardwareAdapter();

	public static HardwareAdapter getInstance() {
		return instance;
	}
}
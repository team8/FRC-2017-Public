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
	 * DRIVETRAIN
	 */
	public static class DrivetrainHardware {
		private static DrivetrainHardware instance = new DrivetrainHardware();

		protected static DrivetrainHardware getInstance() {
			return instance;
		}
		public final CANTalon leftSlave1Talon;
		public final CANTalon leftMasterTalon;
		public final CANTalon leftSlave2Talon;
		public final CANTalon rightSlaveTalon;
		public final CANTalon rightMasterTalon;
		public final CANTalon secondRightSlaveTalon;

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
				rightSlaveTalon = new CANTalon(Constants2016.kDericaRightDriveSlaveDeviceID);
				secondRightSlaveTalon = null;
				gyro = new ADXRS453_Gyro();
				// no shifter solenoid
			} else if (Constants.kRobotName == Constants.RobotName.AEGIR) {
				leftMasterTalon = new CANTalon(Constants.kAegirLeftDriveMasterDeviceID);
				leftSlave1Talon = new CANTalon(Constants.kAegirLeftDriveSlaveDeviceID);
				leftSlave2Talon = new CANTalon(Constants.kAegirLeftDriveOtherSlaveDeviceID);
				rightMasterTalon = new CANTalon(Constants.kAegirRightDriveMasterDeviceID);
				rightSlaveTalon = new CANTalon(Constants.kAegirRightDriveSlaveDeviceID);
				secondRightSlaveTalon = new CANTalon(Constants.kAegirRightDriveOtherSlaveDeviceID);
				
				gyro = null;
			} else {
				leftMasterTalon = new CANTalon(Constants.kSteikLeftDriveMasterDeviceID);
				leftSlave1Talon = new CANTalon(Constants.kSteikLeftDriveSlaveDeviceID);
				leftSlave2Talon = new CANTalon(Constants.kSteikLeftDriveOtherSlaveDeviceID);
				rightMasterTalon = new CANTalon(Constants.kSteikRightDriveMasterDeviceID);
				rightSlaveTalon = new CANTalon(Constants.kSteikRightDriveSlaveDeviceID);
				secondRightSlaveTalon = new CANTalon(Constants.kSteikRightDriveOtherSlaveDeviceID);
				gyro = null;
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
			} else if (Constants.kRobotName == Constants.RobotName.AEGIR) {
				leftSolenoid = new DoubleSolenoid(
						Constants.kAegirLeftFlipperPortExtend, Constants.kAegirLeftFlipperPortRetract);
				rightSolenoid = new DoubleSolenoid(
						Constants.kAegirRightFlipperPortExtend, Constants.kAegirRightFlipperPortRetract);
			} else {
				leftSolenoid = null;
				rightSolenoid = null;
			}
		}
	}

	/**
	 * SLIDER - 1 TalonSRX
	 */
	public static class SliderHardware {
		private static SliderHardware instance = new SliderHardware();
		
		protected static SliderHardware getInstance() {
			return instance;
		}
		public final CANTalon sliderTalon;
		public final AnalogInput sliderPotentiometer;
		
		private SliderHardware() {
			if(Constants.kRobotName == Constants.RobotName.STEIK) {
				sliderTalon = new CANTalon(Constants.kSteikSliderMotorDeviceID);
				sliderPotentiometer = new AnalogInput(Constants.kSteikSliderPotentiometer);
			}
			else if (Constants.kRobotName == Constants.RobotName.AEGIR){
				sliderTalon = new CANTalon(Constants.kAegirSliderMotorDeviceID);
				sliderPotentiometer = new AnalogInput(Constants.kAegirSliderPotentiometerPort);
			}
			else {
				sliderTalon = null;
				sliderPotentiometer = null;
			}
		}
	}

	/**
	 * SPATULA - one double solenoid
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
			} else if (Constants.kRobotName == Constants.RobotName.AEGIR) {
				spatulaSolenoid = new DoubleSolenoid(Constants.kAegirSpatulaPortExtend, Constants.kAegirSpatulaPortRetract);
			} else {
				spatulaSolenoid = null;
			}
		}
	}
	
	/*
	 * INTAKE - two SD540C motors
	 */
	public static class IntakeHardware {
		private static IntakeHardware instance = new IntakeHardware();

		protected static IntakeHardware getInstance() {
			return instance;
		}
		public final VictorSP leftIntakeMotor;
		public final VictorSP rightIntakeMotor;

		private IntakeHardware() {
			if (Constants.kRobotName == Constants.RobotName.AEGIR) {
				leftIntakeMotor = new VictorSP(Constants.kAegirLeftIntakeMotorDeviceID);
				rightIntakeMotor = new VictorSP(Constants.kAegirRightIntakeMotorDeviceID);
			} else if (Constants.kRobotName == Constants.RobotName.STEIK) {
				leftIntakeMotor = new VictorSP(Constants.kSteikLeftIntakeMotorDeviceID);
				rightIntakeMotor = new VictorSP(Constants.kSteikRightIntakeMotorDeviceID);
			} else {
				leftIntakeMotor = null;
				rightIntakeMotor = null;
			}
		}
	}

	/*
	 * CLIMBER - one SD540C motor
	 */
	public static class ClimberHardware {
		private static ClimberHardware instance = new ClimberHardware();
		
		protected static ClimberHardware getInstance(){
			return instance;
		}
		public final CANTalon climberTalon;
		
		private ClimberHardware() {
			if (Constants.kRobotName == Constants.RobotName.AEGIR) {
				climberTalon = new CANTalon(Constants.kAegirClimberMotorDeviceID);
			} else if (Constants.kRobotName == Constants.RobotName.STEIK) {
				climberTalon = new CANTalon(Constants.kSteikClimberMotorDeviceID);
			} else {
				climberTalon = null;
			}
		}
	}
	public final PowerDistributionPanel kPDP = new PowerDistributionPanel();

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

	// Singleton set up
	private static final HardwareAdapter instance = new HardwareAdapter();

	public static HardwareAdapter getInstance() {
		return instance;
	}
}
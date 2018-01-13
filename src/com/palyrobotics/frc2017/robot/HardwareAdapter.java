package com.palyrobotics.frc2017.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.Constants2016;
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
	 * DRIVETRAIN - 6 WPI_TalonSRX's
	 */
	public static class DrivetrainHardware {
		private static DrivetrainHardware instance = new DrivetrainHardware();

		protected static DrivetrainHardware getInstance() {
			return instance;
		}
		public final WPI_TalonSRX leftSlave1Talon;
		public final WPI_TalonSRX leftMasterTalon;
		public final WPI_TalonSRX leftSlave2Talon;
		public final WPI_TalonSRX rightSlave1Talon;
		public final WPI_TalonSRX rightMasterTalon;
		public final WPI_TalonSRX rightSlave2Talon;

		// If encoders are wired directly to RIO use the following objects
//		public final ADXRS453_Gyro gyro;
		public AHRS gyro;

		public static void resetSensors() {
			instance.gyro.zeroYaw();
			instance.leftMasterTalon.setSelectedSensorPosition(0, 0, 0);
			instance.rightMasterTalon.setSelectedSensorPosition(0, 0, 0);
		}

		private DrivetrainHardware() {
			if(Constants.kRobotName == Constants.RobotName.DERICA) {
				leftMasterTalon = new WPI_TalonSRX(Constants2016.kDericaLeftDriveMasterDeviceID);
				leftSlave1Talon = new WPI_TalonSRX(Constants2016.kDericaLeftDriveSlaveDeviceID);
				leftSlave2Talon = null;
				rightMasterTalon = new WPI_TalonSRX(Constants2016.kDericaRightDriveMasterDeviceID);
				rightSlave1Talon = new WPI_TalonSRX(Constants2016.kDericaRightDriveSlaveDeviceID);
				rightSlave2Talon = null;
				gyro = new AHRS(SerialPort.Port.kMXP);
			} else {
				leftMasterTalon = new WPI_TalonSRX(Constants.kSteikLeftDriveMasterDeviceID);
				leftSlave1Talon = new WPI_TalonSRX(Constants.kSteikLeftDriveSlaveDeviceID);
				leftSlave2Talon = new WPI_TalonSRX(Constants.kSteikLeftDriveOtherSlaveDeviceID);
				rightMasterTalon = new WPI_TalonSRX(Constants.kSteikRightDriveMasterDeviceID);
				rightSlave1Talon = new WPI_TalonSRX(Constants.kSteikRightDriveSlaveDeviceID);
				rightSlave2Talon = new WPI_TalonSRX(Constants.kSteikRightDriveOtherSlaveDeviceID);
				gyro = new AHRS(SPI.Port.kMXP);
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
		public final WPI_TalonSRX sliderTalon;
		public final AnalogInput sliderPotentiometer;

		public static void resetEncoder() {
			instance.sliderTalon.setSelectedSensorPosition(0, 0, 0);
		}

		private SliderHardware() {
			if (Constants.kRobotName == Constants.RobotName.STEIK){
				sliderTalon = new WPI_TalonSRX(Constants.kSteikSliderMotorDeviceID);
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
	 * INTAKE - 1 VictorSP
	 */
	public static class IntakeHardware {
		private static IntakeHardware instance = new IntakeHardware();

		protected static IntakeHardware getInstance() {
			return instance;
		}
		public final VictorSP intakeMotor;

		private IntakeHardware() {
			if (Constants.kRobotName == Constants.RobotName.STEIK) {
				intakeMotor = new VictorSP(Constants.kSteikIntakeMotorDeviceID);
			} else {
				intakeMotor = null;
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
		public final WPI_TalonSRX climberTalon;
		
		private ClimberHardware() {
			if (Constants.kRobotName == Constants.RobotName.STEIK) {
				climberTalon = new WPI_TalonSRX(Constants.kSteikClimberMotorDeviceID);
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
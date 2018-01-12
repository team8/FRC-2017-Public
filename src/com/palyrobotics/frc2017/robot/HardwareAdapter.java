package com.palyrobotics.frc2017.robot;

import com.ctre.CANTalon;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
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
	 * DRIVETRAIN - 6 TalonSRX's
	 */
	public static class DrivetrainHardware {
		private static DrivetrainHardware instance = new DrivetrainHardware();

		protected static DrivetrainHardware getInstance() {
			return instance;
		}
		public final TalonSRX leftSlave1Talon;
		public final TalonSRX leftMasterTalon;
		public final TalonSRX leftSlave2Talon;
		public final TalonSRX rightSlave1Talon;
		public final TalonSRX rightMasterTalon;
		public final TalonSRX rightSlave2Talon;

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
				leftMasterTalon = new TalonSRX(Constants2016.kDericaLeftDriveMasterDeviceID);
				leftSlave1Talon = new TalonSRX(Constants2016.kDericaLeftDriveSlaveDeviceID);
				leftSlave2Talon = null;
				rightMasterTalon = new TalonSRX(Constants2016.kDericaRightDriveMasterDeviceID);
				rightSlave1Talon = new TalonSRX(Constants2016.kDericaRightDriveSlaveDeviceID);
				rightSlave2Talon = null;
				gyro = new AHRS(SerialPort.Port.kMXP);
			} else {
				leftMasterTalon = new TalonSRX(Constants.kSteikLeftDriveMasterDeviceID);
				leftSlave1Talon = new TalonSRX(Constants.kSteikLeftDriveSlaveDeviceID);
				leftSlave2Talon = new TalonSRX(Constants.kSteikLeftDriveOtherSlaveDeviceID);
				rightMasterTalon = new TalonSRX(Constants.kSteikRightDriveMasterDeviceID);
				rightSlave1Talon = new TalonSRX(Constants.kSteikRightDriveSlaveDeviceID);
				rightSlave2Talon = new TalonSRX(Constants.kSteikRightDriveOtherSlaveDeviceID);
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
		public final TalonSRX sliderTalon;
		public final AnalogInput sliderPotentiometer;

		public static void resetEncoder() {
			instance.sliderTalon.setSelectedSensorPosition(0, 0, 0);
		}

		private SliderHardware() {
			if (Constants.kRobotName == Constants.RobotName.STEIK){
				sliderTalon = new TalonSRX(Constants.kSteikSliderMotorDeviceID);
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
		public final TalonSRX climberTalon;
		
		private ClimberHardware() {
			if (Constants.kRobotName == Constants.RobotName.STEIK) {
				climberTalon = new TalonSRX(Constants.kSteikClimberMotorDeviceID);
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
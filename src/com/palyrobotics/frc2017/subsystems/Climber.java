package com.palyrobotics.frc2017.subsystems;

import com.mindsensors.CANSD540;
import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.util.Subsystem;
import com.palyrobotics.frc2017.util.SubsystemLoop;

import edu.wpi.first.wpilibj.DoubleSolenoid;
/**
 * STEIK CLIMBER
 * @author Jason
 * Consists of a motor that winches up the robot to climb at the end of the match.
 * The winch/climber is controlled by a  CANSD540
 */
public class Climber extends Subsystem implements SubsystemLoop{
    //Speed at which the robot should climb
    public double kClimberSpeed = 0.5;
    //Speed at which the robot is currently climbing, changes if ClimberState is CLIMBING
    public double climberSpeed = 0;
    
    public Climber() {
        super("Climber");
    }

    private static Climber instance = new Climber();
    public static Climber getInstance() {
        return instance;
    }
    public enum ClimberState{CLIMBING, IDLE}
    public ClimberState climberState;
    @Override
    public void start() {
        // TODO Auto-generated method stub
        
    }

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
	/**
	 * Updates climber subsystem, and sets the climberSpeed to wantedClimberSpeed
	 */
	@Override
	public void update(Commands commands, RobotState robotState) {
		climberState = commands.wantedClimberState;
		
		switch(climberState){
		case CLIMBING:
			climberSpeed = kClimberSpeed;
			break;
		case IDLE:
			climberSpeed = 0;
			break;
			
		}
	}
	/**
	 * @return climberSpeed
	 */
	public double getOutput() {
		return climberSpeed;
	}
}
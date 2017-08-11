package com.palyrobotics.frc2017.util.archive.team254.trajectory;

import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.robot.Robot;
import com.team254.lib.trajectory.Trajectory;

/**
 * PID + Feedforward controller for following a Trajectory.
 *
 * @author Jared341
 */
public class LegacyTrajectoryFollower {

    private double kp_;
    private double ki_;  // Not currently used, but might be in the future.
    private double kd_;
    private double kv_;
    private double ka_;
    private double last_error_;

    private double current_heading = 0;
    private int current_segment;
    private Trajectory profile_;
    public String name;
    
    private double last_calc_velocity_error;
    private double last_pos_;
    
    private String canTableString;

    public LegacyTrajectoryFollower(String name) {
        this.name = name;
    }

    public void configure(double kp, double ki, double kd, double kv, double ka) {
        kp_ = kp;
        ki_ = ki;
        kd_ = kd;
        kv_ = kv;
        ka_ = ka;
    }

    public void reset() {
        last_error_ = 0.0;
        last_pos_ = 0.0;
        current_segment = 0;
    }

    public void setTrajectory(Trajectory profile) {
        profile_ = profile;
    }

    public double calculate(double distance_so_far) {

        if (current_segment < profile_.getNumSegments()) {
            Trajectory.Segment segment = profile_.getSegment(current_segment);
            double error = segment.pos - distance_so_far;
            
            double calc_velocity_error;
            double speed;

            if(this.name.equals("left")) {
                speed = (Robot.getRobotState().drivePose.leftSpeed/(12.0*Constants.kDriveSpeedUnitConversion));
            } else if(this.name.equals("right")) {
                speed = (Robot.getRobotState().drivePose.rightSpeed/(12.0*Constants.kDriveSpeedUnitConversion));
            } else {
                speed = 0.0;
                System.err.println("Wrong name?");
            }

            calc_velocity_error = speed - segment.vel;

            double output = kp_ * error + kd_ * calc_velocity_error + kv_ * segment.vel + ka_ * segment.acc;
            setCanTableString(new double[] {
            		output,
            		segment.pos,
            		segment.vel,
            		segment.acc,
            		distance_so_far,
                    last_pos_,
                    speed,
            		error,
                    (distance_so_far - last_pos_)/segment.dt - segment.vel,
                    calc_velocity_error
            });
            
            last_error_ = error;
            last_pos_ = distance_so_far;
            last_calc_velocity_error = calc_velocity_error;
            
            current_heading = segment.heading;
            current_segment++;
            return output;
            
        } else {
            return 0;
        }
    }

    public double getHeading() {
        return current_heading;
    }

    public boolean isFinishedTrajectory() {
        return current_segment >= profile_.getNumSegments();
    }

    public Trajectory.Segment getCurrentSegment() {
        return profile_.getSegment(current_segment);
    }

    public int getCurrentSegmentNumber() {
        return current_segment;
    }

    public int getNumSegments() {
        return profile_.getNumSegments();
    }
    
    private void setCanTableString(double[] a) {
    	canTableString = "";
    	for(int i = 0; i < a.length-1; i++) {
    		canTableString = canTableString + Double.toString(a[i]) + ", ";
    	}
    	canTableString = canTableString + Double.toString(a[a.length-1]);
    }
    
    public String getCanTableString() {
    	return this.canTableString;
    }
}

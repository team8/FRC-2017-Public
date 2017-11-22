package com.palyrobotics.frc2017.robot;

import com.palyrobotics.frc2017.config.Constants;
import com.palyrobotics.frc2017.config.RobotState;
import com.palyrobotics.frc2017.subsystems.Drive;
import com.team254.lib.trajectory.Kinematics;
import com.team254.lib.trajectory.RigidTransform2d;
import com.team254.lib.trajectory.Rotation2d;
import com.palyrobotics.frc2017.robot.team254.lib.util.Loop;
import edu.wpi.first.wpilibj.Timer;

/**
 * Periodically estimates the state of the robot using the robot's distance
 * traveled (compares two waypoints), gyroscope orientation, and velocity, among
 * various other factors. Similar to a car's odometer.
 */
public class RobotStateEstimator implements Loop {

    static RobotStateEstimator instance_ = new RobotStateEstimator();

    public static RobotStateEstimator getInstance() {
        return instance_;
    }

    RobotStateEstimator() {
    }

    RobotPosition robot_state_ = RobotPosition.getInstance();
    double left_encoder_prev_distance_ = 0;
    double right_encoder_prev_distance_ = 0;

    @Override
    public void onStart() {
        RobotState state = Robot.getRobotState();
        left_encoder_prev_distance_  = state.drivePose.leftEnc ;
        right_encoder_prev_distance_ = state.drivePose.rightEnc;
    }

    @Override
    public void update() {

        RobotState state = Robot.getRobotState();
        double time = Timer.getFPGATimestamp();
        double left_distance  = state.drivePose.leftEnc;
        double right_distance = state.drivePose.rightEnc;
        System.out.println("left distance: " + (left_distance - left_encoder_prev_distance_) + "\nright distance: " + (right_distance - right_encoder_prev_distance_));
        Rotation2d gyro_angle = Rotation2d.fromRadians(right_distance - left_distance * Constants.kTrackScrubFactor / Constants.kTrackEffectiveDiameter);
        System.out.println("gyro angle: " + gyro_angle);
        RigidTransform2d odometry = robot_state_.generateOdometryFromSensors(
            left_distance - left_encoder_prev_distance_, right_distance - right_encoder_prev_distance_, gyro_angle);
        System.out.println("Odometry is: " + odometry);
        RigidTransform2d.Delta velocity = Kinematics.forwardKinematics(
            state.drivePose.leftEncVelocity,
            state.drivePose.rightEncVelocity
        );
        robot_state_.addObservations(time, odometry, velocity);
        left_encoder_prev_distance_ = left_distance;
        right_encoder_prev_distance_ = right_distance;
    }

    @Override
    public void onStop() {
        // no-op
    }

}
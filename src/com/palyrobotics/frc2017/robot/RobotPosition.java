package com.palyrobotics.frc2017.robot;

import com.team254.lib.trajectory.*;

import java.util.Map;

/**
 * RobotState keeps track of the poses of various coordinate frames throughout
 * the match. A coordinate frame is simply a point and direction in space that
 * defines an (x,y) coordinate system. Transforms (or poses) keep track of the
 * spatial relationship between different frames.
 * 
 * Robot frames of interest (from parent to child):
 * 
 * 1. Field frame: origin is where the robot is turned on
 * 
 * 2. Vehicle frame: origin is the center of the robot wheelbase, facing
 * forwards
 * 
 * 3. Turret fixed frame: origin is the center of the turret when the turret is
 * at 0 degrees rotation relative to the vehicle frame
 * 
 * 4. Turret rotating frame: origin is the center of the turret as it rotates
 * 
 * 5. Camera frame: origin is the center of the camera imager as it rotates with
 * the turret
 * 
 * 6. Goal frame: origin is the center of the goal (note that orientation in
 * this frame is arbitrary). Also note that there can be multiple goal frames.
 * 
 * As a kinematic chain with 6 frames, there are 5 transforms of interest:
 * 
 * 1. Field-to-vehicle: This is tracked over time by integrating encoder and
 * gyro measurements. It will inevitably drift, but is usually accurate over
 * short time periods.
 * 
 * 2. Vehicle-to-turret-fixed: This is a constant.
 * 
 * 3. Vehicle-to-turret-rotating: This is a pure rotation, and is tracked over
 * time using the turret encoder.
 * 
 * 4. Turret-rotating-to-camera: This is a constant.
 * 
 * 5. Camera-to-goal: This is a pure translation, and is measured by the vision
 * system.
 */

public class RobotPosition {
    private static RobotPosition instance_ = new RobotPosition();

    public static RobotPosition getInstance() {
        return instance_;
    }
    
    public static void resetInstance() {
    	instance_.reset(0, new RigidTransform2d(), new Rotation2d());
    }

    public static final int kObservationBufferSize = 100;
    public static final double kMaxTargetAge = 0.4;


    // FPGATimestamp -> RigidTransform2d or Rotation2d
    protected RigidTransform2d.Delta vehicle_velocity_;
    protected double differential_height_;
    protected InterpolatingTreeMap<InterpolatingDouble, RigidTransform2d> field_to_vehicle_;


    protected RobotPosition() {
        reset(0, new RigidTransform2d(), new Rotation2d());
    }

    public synchronized void reset(double start_time, RigidTransform2d initial_field_to_vehicle,
            Rotation2d initial_turret_rotation) {
        field_to_vehicle_ = new InterpolatingTreeMap<>(kObservationBufferSize);
        field_to_vehicle_.put(new InterpolatingDouble(start_time), initial_field_to_vehicle);
        vehicle_velocity_ = new RigidTransform2d.Delta(0, 0, 0);
    }

    public synchronized RigidTransform2d getFieldToVehicle(double timestamp) {
        return field_to_vehicle_.getInterpolated(new InterpolatingDouble(timestamp));
    }

    public synchronized Map.Entry<InterpolatingDouble, RigidTransform2d> getLatestFieldToVehicle() {
        return field_to_vehicle_.lastEntry();
    }

    public synchronized RigidTransform2d getPredictedFieldToVehicle(double lookahead_time) {
        return getLatestFieldToVehicle().getValue().transformBy(
                RigidTransform2d.fromVelocity(new RigidTransform2d.Delta(vehicle_velocity_.dx * lookahead_time,
                        vehicle_velocity_.dy * lookahead_time, vehicle_velocity_.dtheta * lookahead_time)));
    }

    public synchronized void addFieldToVehicleObservation(double timestamp, RigidTransform2d observation) {
        field_to_vehicle_.put(new InterpolatingDouble(timestamp), observation);
        System.out.println("YOU DID IT! number of observations is " + field_to_vehicle_.size());
        System.out.println("Observation is " + observation);
    }

    public synchronized void addObservations(double timestamp, RigidTransform2d field_to_vehicle,
            RigidTransform2d.Delta velocity) {
        addFieldToVehicleObservation(timestamp, field_to_vehicle);
        vehicle_velocity_ = velocity;
    }

    public RigidTransform2d generateOdometryFromSensors(double left_encoder_delta_distance,
                                                        double right_encoder_delta_distance, Rotation2d current_gyro_angle) {
        RigidTransform2d last_measurement = getLatestFieldToVehicle().getValue();
        return Kinematics.integrateForwardKinematics(last_measurement, left_encoder_delta_distance,
                right_encoder_delta_distance, current_gyro_angle);
    }
    
    public int getNumObservations() {
    	return field_to_vehicle_.size();
    }
}

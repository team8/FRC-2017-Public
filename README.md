Team 8 Paly Robotics - FRC 2017 Robot Software
================================================
## Robot description 
Robot code for Team 8's 2017 Steamworks FRC robot

### Robot subsystems

#### Spatula
The mechanism for accumulating gears from the ground, 
and for scoring gears by passively placing them on the 
lift.

#### Slider
Rail mechanism to move the spatula horizontally in order to
score gears after aligning.

#### Intake
Roller tube to roll gears into the spatula.

#### Climber
Winch mechanism to grab the rope and climb

#### Drivetrain
A 8 wheel 4-CIM west coast drivetrain


### Package Functions

* com.palyrobotics.frc2017.auto

	Controls the robot’s autonomous behavior and autonomous mode selection interface. 

* com.palyrobotics.frc2017.auto.modes

	Contains all of the robot’s autonomous modes and possible paths a robot can take during the autonomous 	period. 
	
* com.palyrobotics.frc2017.behavior

	Contains code needed to execute automatically triggered routines. 

* com.palyrobotics.frc2017.behavior.routines

	Contains miscellaneous routines for robot which can be automatically triggered. 

* com.palyrobotics.frc2017.behavior.routines.drive

	Contains robot routines for driving which can be automatically triggered.
     
* com.palyrobotics.frc2017.behavior.routines.scoring

	Contains robot routines for scoring, specifically the slider, which can be automatically triggered. 
    
* com.palyrobotics.frc2017.config

	Contains robot constants such as auto distances, sensor values, gains, and robot states.
     
* com.palyrobotics.frc2017.config.dashboard

	Configures CANTables and communication with the dashboard.
    
* com.palyrobotics.frc2017.robot

	Holds the main Robot class as well as classes for hardware-software integration.
    
* com.palyrobotics.frc2017.robot.team254.lib.util

	A collection of assorted utilities classes used in the robot code. This includes custom classes for hardware 	devices     (encoders, gyroscopes, etc.) as well as mathematical helper functions, especially regarding 	translations and           rotations. Check each .java file for more information.
    
* com.palyrobotics.frc2017.subsystems

	Contains all of the robot’s subsystems.
    
* com.palyrobotics.frc2017.subsystems.controllers

	Contains all of the controllers for the robot’s subsystems.
    
* com.palyrobotics.frc2017.util

	Contains helper code used in other classes such as CANTalonOutput.java, DoubleClickTimer.java, Pose.java,
	and WaitTimer.java.
    
* com.palyrobotics.frc2017.util.archive

	This package has functions not used anymore. 
    
* com.palyrobotics.frc2017.util.archive.team254

	Reads in texts files to be used in other packages. 
    
* com.palyrobotics.frc2017.util.archive.team254.controllers.team254

	Control loops used in auto. 
    
* com.palyrobotics.frc2017.util.archive.team254.trajectory

	Contains controllers used in auto robot trajectory followers. 
    
* com.palyrobotics.frc2017.util.logger

	Configures log messages. 
    
* com.palyrobotics.frc2017.vision

	Contains utility classes for networking with the 2017 vision app. 
    
* com.team254.lib.trajectory

	Used to generate trajectories for the robot to follow during Autonomous mode. Trajectories are calculated 	offboard on     a laptop then deployed to the robot with the main robot JAR.
	
* com.team254.lib.trajectory.io

	Deals with input/output and serialization of text files containing motion-profiling paths 
    
* com.team254.lib.util

	A collection of assorted utilities classes used in the robot code. This includes custom classes for hardware 	devices     (encoders, gyroscopes, etc.) as well as mathematical helper functions, especially regarding 	translations and           rotations. Check each .java file for more information.
    
## Acknowledgements
Reuses code from Team 8 2016 offseason robot software [Lady Derica repository](https://github.com/team8/lady-derica)
Built off of Team 254's 2014/2015 FRC robot code.
Also uses suggestions from Austin Schuh (Team 971) and Kelly Ostrom (Team 1678)
Uses the library RIODroid by Team Spectrum 3847 to program the Nexus 5X
A huge thank you to all the mentors and sponsors of Team 8 who made this robot possible.
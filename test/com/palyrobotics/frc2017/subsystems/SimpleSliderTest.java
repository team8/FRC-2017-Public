package com.palyrobotics.frc2017.subsystems;

import org.junit.Test;

import com.palyrobotics.frc2017.config.Commands;
import com.palyrobotics.frc2017.robot.Robot;
	

public class SimpleSliderTest{
		
		@Test
		public void testOutput(){
			Commands commands = Robot.getCommands();
			SimpleSlider simpleSlider = SimpleSlider.getInstance();
		
			SimpleSlider.SimpleSliderState desired = SimpleSlider.SimpleSliderState.IDLE;
		}

	
	}



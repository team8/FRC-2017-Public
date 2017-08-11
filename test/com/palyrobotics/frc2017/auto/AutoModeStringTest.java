package com.palyrobotics.frc2017.auto;

import com.palyrobotics.frc2017.auto.modes.SidePegAutoMode;
import com.palyrobotics.frc2017.auto.modes.VisionSidePegAutoMode;
import org.junit.Test;

public class AutoModeStringTest {
	@Test
	public void printName() {
		AutoModeBase auto = new VisionSidePegAutoMode(SidePegAutoMode.SideAutoVariant.RED_BOILER);
		auto.prestart();
		System.out.println(auto.toString());
		System.out.println(auto.getRoutine().toString());
	}
}

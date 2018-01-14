package com.palyrobotics.frc2017.util.archive.team254.trajectory;

import com.palyrobotics.frc2018.robot.team254.lib.util.BinaryInputDecoder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BinaryInputDecoderTest {

	@Test
	public void testBits() {
		assertEquals(0L, (long) BinaryInputDecoder.decode(false, false, false));
		assertEquals(1L, (long) BinaryInputDecoder.decode(true, false, false));
		assertEquals(2L, (long) BinaryInputDecoder.decode(false, true, false));
		assertEquals(3L, (long) BinaryInputDecoder.decode(true, true, false));
		assertEquals(4L, (long) BinaryInputDecoder.decode(false, false, true));
		assertEquals(5L, (long) BinaryInputDecoder.decode(true, false, true));
		assertEquals(6L, (long) BinaryInputDecoder.decode(false, true, true));
		assertEquals(7L, (long) BinaryInputDecoder.decode(true, true, true));
	}
}

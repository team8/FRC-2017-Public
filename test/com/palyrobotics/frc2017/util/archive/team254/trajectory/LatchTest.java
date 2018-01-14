package com.palyrobotics.frc2017.util.archive.team254.trajectory;

import com.palyrobotics.frc2018.robot.team254.lib.util.Latch;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LatchTest {

  @Test
  public void testTrueHold() {
    Latch l = new Latch();
    boolean result = l.update(false);
    assertFalse("Latch should be false when first updated", result);
    result = l.update(true);
    assertTrue("Latch should be true when first updated with true", result);
    result = l.update(true);
    assertFalse("Latch should be false when updated with true again", result);
    result = l.update(false);
    assertFalse("Latch should be false when updated with false again", result);
  }

}

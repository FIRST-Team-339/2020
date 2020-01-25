package frc.Utils;

import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.Hardware.*;

public class IntakeControl {
    public IntakeControl() {

    }

    public void intake(JoystickButton intakeButton) {
        if (intakeButton.get()) {
            // set motor to suck some balls
        }
    }

    public boolean intake(int seconds) {
        return false;
    }

    public void outtake(JoystickButton outtakeButton) {
        if (outtakeButton.get()) {
            // set motor to vomit up the balls it sucked
        }
    }

    public boolean outtake() {
        // until empty
        return false;
    }
}

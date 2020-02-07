package frc.Utils;

import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.Hardware.Hardware;

public class CameraServo
    {

    public CameraServo()
        {

        }

    public boolean setCameraAngleUp(JoystickButton button)
    {
        if (button.get())
            {
            if (Hardware.rotateServo.getAngle() >= 125)
                {
                return true;
                }
            else
                {
                Hardware.rotateServo.setAngle(125);
                }
            }
        //if servo is not at up position move up
        return false;
    }

    public boolean setCameraAngleUp()
    {
        if (Hardware.rotateServo.getAngle() >= 124)
            {
            return true;
            }
        else
            {
            Hardware.rotateServo.setAngle(125);
            }
        //if servo is not at up position move up
        return false;
    }

    public boolean setCameraAngleDown(JoystickButton button)
    {
        //if servo is not at down position move down
        if (button.get())
            {
            if (Hardware.rotateServo.getAngle() <= 56)
                {
                return true;
                }
            else
                {
                Hardware.rotateServo.setAngle(55);
                }
            }
        return false;
    }

    public boolean setCameraAngleDown()
    {
        if (Hardware.rotateServo.getAngle() <= 56)
            {
            return true;
            }
        else
            {
            Hardware.rotateServo.setAngle(55);
            }
        //if servo is not at down position move down
        return false;
    }

    }

package frc.Utils;

import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.Hardware.Hardware;

/**
 * Code for raising and lowering te camera servo for the 2020 season
 * @author Conner McKevitt
 */
public class CameraServo
    {

    public CameraServo()
        {

        }

    /**
    * sets the camera servo to the up position based off a button
    *
    * @param button button to push
    * @return is at up angle
    */
    public boolean setCameraAngleUp(JoystickButton button)
    {
        if (button.get())
            {
            if (Hardware.rotateServo.getAngle() >= UP_POSITION - ACCEPTABLE_OFFSET)
                {
                return true;
                }
            else
                {
                Hardware.rotateServo.setAngle(UP_POSITION);
                }
            }
        //if servo is not at up position move up
        return false;
    }

    /**
    * sets the camera servo to the up position. Note: for use in auto otherwise use a button constructor
    *
    * @param button button to push
    * @return is at up angle
    */
    public boolean setCameraAngleUp()
    {
        if (Hardware.rotateServo.getAngle() >= UP_POSITION - ACCEPTABLE_OFFSET)
            {
            return true;
            }
        else
            {
            Hardware.rotateServo.setAngle(UP_POSITION);
            }
        //if servo is not at up position move up
        return false;
    }

    /**
    * sets the camera servo to the down position based off a button
    *
    * @param button button to push
    * @return is at down angle
    */
    public boolean setCameraAngleDown(JoystickButton button)
    {
        //if servo is not at down position move down
        if (button.get())
            {
            if (Hardware.rotateServo.getAngle() <= DOWN_POSITION + ACCEPTABLE_OFFSET)
                {
                return true;
                }
            else
                {
                Hardware.rotateServo.setAngle(DOWN_POSITION);
                }
            }
        return false;
    }

    /**
    * sets the camera servo to the down position. Note: for use in auto otherwise use a button constructor
    *
    * @param button button to push
    * @return is at down angle
    */
    public boolean setCameraAngleDown()
    {
        if (Hardware.rotateServo.getAngle() <= DOWN_POSITION + ACCEPTABLE_OFFSET)
            {
            return true;
            }
        else
            {
            Hardware.rotateServo.setAngle(DOWN_POSITION);
            }
        //if servo is not at down position move down
        return false;
    }

    //up angle
    final double UP_POSITION = 125;

    //down angle
    final double DOWN_POSITION = 55;

    //position that is ok but not really what you want(just like your grades)
    final double ACCEPTABLE_OFFSET = 1;
    }

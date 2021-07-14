package frc.HardwareInterfaces.Transmission;

import edu.wpi.first.wpilibj.SpeedControllerGroup;

/**
 * Like the traction drive system, except with four motors, usually all as
 * omni-wheels.
 * Or, tank drive with paired motors. Each joystick controls one side of the
 * robot.
 *
 * @author Ryan McGee
 * @written 7/21/17
 */
public class TankTransmission extends TransmissionBase
    {
    /**
     * Creates the Transmission object.
     *
     * @param leftSide
     *            the grouped motor controllers on the left side of the robot
     *
     * @param rightSide
     *            the grouped motor controllers on the right side of the robot
     */
    public TankTransmission(SpeedControllerGroup leftSide, SpeedControllerGroup rightSide)
        {
            super(leftSide, rightSide);

            super.type = TransmissionType.TANK;
        }

    /**
     * Drives the transmission based on a Tank drive system,
     * where left controls the left wheels, and right controls the right wheels.
     *
     * Uses joystick deadbands and gear ratios.
     *
     * @param leftJoystick
     *            Percentage, (-1.0 to 1.0)
     * @param rightJoystick
     *            Percentage, (-1.0 to 1.0)
     */
    public void drive(double leftJoystick, double rightJoystick)
    {
        double leftOut = super.scaleJoystickForDeadband(leftJoystick) * super.getCurrentGearRatio();
        double rightOut = super.scaleJoystickForDeadband(rightJoystick) * super.getCurrentGearRatio();
        if (super.getSpeedLimit() < leftOut)
            {
            leftOut = super.getSpeedLimit();
            }
        if (super.getSpeedLimit() < rightOut)
            {
            rightOut = super.getSpeedLimit();
            }
        if ((-1 * super.getSpeedLimit()) > leftOut)
            {
            leftOut = -1 * super.getSpeedLimit();
            }
        if ((-1 * super.getSpeedLimit()) > rightOut)
            {
            rightOut = -1 * super.getSpeedLimit();
            }
        super.driveRaw(leftOut, rightOut);
    }
    }

package frc.Utils;

import frc.Hardware.Hardware;
import frc.HardwareInterfaces.LightSensor;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Launcher
    {

    // private motortype shootingMotor = null;
    // private motortype intakeMotor = null;

    public Launcher()
        {

        }

    private enum ShootState
        {
        PASSIVE, CHARGE, LAUNCH,
        }

    public ShootState shootState = ShootState.PASSIVE;

    /**
     * in case you could guess this function will shoot balls at whatever you
     * desire. whether it be the target or pesky those builders who have yet to
     * finish the actual laucher
     */
    public void shootBalls(JoystickButton shootButton, JoystickButton overrideButton)
    {
        switch (shootState)
            {

            }

    }

    public void shootBalls(JoystickButton shootButton, JoystickButton overrideButton, int distance)
    {

    }

    public boolean shootBallsAuto(JoystickButton overrideButton)
    {
        return false;
    }

    public int getRPMPerDistance(int distance)
    {
        return 0;
    }

    public boolean launching = false;
    }

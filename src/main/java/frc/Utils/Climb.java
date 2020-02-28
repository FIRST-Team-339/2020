package frc.Utils;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.Hardware.Hardware;
import frc.HardwareInterfaces.KilroyEncoder;
import frc.HardwareInterfaces.KilroyServo;

public class Climb
    {
    public enum upDown
        {
        UP, DOWN, NEIN
        }

    public upDown climbState = upDown.NEIN;

    public Climb(SpeedControllerGroup climbMotors, KilroyServo climbServo, KilroyEncoder climbEncoder)
        {

            this.climbMotors = climbMotors;
            this.climbServo = climbServo;
            this.climbEncoder = climbEncoder;
            this.climbEncoder.reset();

        }

    public void prepareToClimb(JoystickButton prepareButton)
    {

        if (prepareButton.get())
            {
            System.out.println("Distance: " + this.climbEncoder.getDistance());
            this.climbServo.set(unlachedDegree);
            climbState = upDown.UP;
            dogo = true;
            // System.out.println("Preparing to Climb");

            if (this.climbEncoder.getDistance() < EncoderTopLimit)
                {
                this.climbMotors.set(.3);
                }
            else
                {
                dogo = false;
                climbState = upDown.NEIN;
                this.climbMotors.set(0);

                }

            }
        else
            {
            // set motors to not move
            if (!dogo)
                {
                this.climbMotors.set(0);
                }

            // ensure unlatch servo

            }
        if (prepareButton.get() && Hardware.leftDriver.getRawButton(11))
            ;

    }

    public void climb(JoystickButton climbDown)
    {
        if (climbDown.get())
            {
            climbState = upDown.DOWN;

            this.climbServo.set(latchedDegree);
            // bring motors down
            if (this.climbEncoder.getDistance() > 0)
                {
                this.climbMotors.set(-.3);
                }
            // latch servo

            }
        else
            {
            if (!dogo)
                {
                climbState = upDown.NEIN;
                this.climbMotors.set(0);
                }
            }

    }

    private boolean dogo = false;
    private int EncoderTopLimit = 26;
    // private int EncoderBotLimit = 0;
    private double latchedDegree = 50;
    public double unlachedDegree = 0;
    private SpeedControllerGroup climbMotors = null;
    private Servo climbServo = null;
    private KilroyEncoder climbEncoder = null;

    }

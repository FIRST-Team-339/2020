package frc.Utils;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.Hardware.Hardware;
import frc.HardwareInterfaces.KilroyEncoder;
import frc.HardwareInterfaces.KilroyServo;

public class Climb
    {

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
            this.climbServo.set(unlatchedDegree);
            dogo = true;
            }
        else

            // System.out.println("Distance: " + this.climbEncoder.getDistance());
            //this.climbServo.set(unlachedDegree);
            if (reachedTop && !Hardware.climbMotorDownButton.get())
            {
            this.climbMotors.set(0);
            }

        if (dogo == true)
            {
            // System.out.println("Preparing to Climb");

            if (this.climbEncoder.getDistance() < EncoderTopLimit && reachedTop == false)
                {

                // System.out.println("climb set +");
                this.climbMotors.set(.3);
                }
            else if (this.climbEncoder.getDistance() >= EncoderTopLimit)
                {
                reachedTop = true;
                }
            else
                {
                dogo = false;

                // System.out.println("climb set 0");
                this.climbMotors.set(0);

                }

            }
        else
            {
            // set motors to not move

            // System.out.println("climb set 0");
            this.climbMotors.set(0);
            this.climbServo.set(latchedDegree);
            // ensure unlatch servo

            }
        if (prepareButton.get() && Hardware.leftDriver.getRawButton(11))
            ;

    }

    public void climb(JoystickButton climbDown)
    {

        if (climbDown.get())
            {
            dogo = true;
            this.climbServo.set(latchedDegree);
            // bring motors down
            if (this.climbEncoder.getDistance() > 0)
                {
                dogo = false;
                //System.out.println("climb set -");
                this.climbMotors.set(-.3);
                }
            // latch servo

            }
        else
            {

            if (!dogo)
                {
                //System.out.println("climb set 0");

                this.climbMotors.set(0);
                }
            }

    }

    private boolean dogo = false;
    private boolean reachedTop = false;
    private int EncoderTopLimit = 27;
    // private int EncoderBotLimit = 0;
    private double latchedDegree = 50;
    public double unlatchedDegree = 0;
    private SpeedControllerGroup climbMotors = null;
    private Servo climbServo = null;
    private KilroyEncoder climbEncoder = null;

    }

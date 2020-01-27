package frc.Utils;

import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.Timer;

public class BallCounter
    {

    Timer timer = null;

    public BallCounter(Timer timer)
        {
            this.timer = timer;
        }

    private int ballCount = 0;

    public int getBallCount()
    {
        return ballCount;
    }

    public void addBall()
    {

    }

    public void addBall(JoystickButton button)
    {
        if (button.get() && this.timer.get() > .1 || firstTime == true)
            {
            timer.reset();
            firstTime = false;
            ballCount++;
            timer.start();

            }
    }

    public void subtractBall()
    {

    }

    private boolean firstTime = true;

    public void subtractBall(JoystickButton button)
    {
        if (button.get() && this.timer.get() > .1 || firstTime == true)
            {
            timer.reset();
            firstTime = false;
            ballCount--;
            timer.start();

            }
    }

    public void clearCount()
    {

    }

    }

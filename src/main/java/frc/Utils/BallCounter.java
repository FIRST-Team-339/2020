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
        if (ballCount < MAX_BALLS)
            {
            ballCount++;
            }
    }

    public void addBall(JoystickButton button)
    {
        if (button.get() && this.timer.get() > .1 || firstTime == true)
            {
            timer.reset();
            firstTime = false;
            if (ballCount < MAX_BALLS)
                {
                ballCount++;
                }
            timer.start();

            }
    }

    public void subtractBall()
    {
        if (ballCount > MIN_BALLS)
            {
            ballCount--;
            }
    }

    private boolean firstTime = true;

    public void subtractBall(JoystickButton button)
    {
        if (button.get() && this.timer.get() > .1 || firstTime == true)
            {
            timer.reset();
            firstTime = false;
            if (ballCount > MIN_BALLS)
                {
                ballCount--;
                }
            timer.start();

            }
    }

    public void clearCount()
    {
        ballCount = 0;
    }

    public void clearCount(JoystickButton button1, JoystickButton button2)
    {
        if (button1.get() && button2.get())
            {
            ballCount = 0;
            }
    }

    private final int MAX_BALLS = 5;
    private final int MIN_BALLS = 0;
    }

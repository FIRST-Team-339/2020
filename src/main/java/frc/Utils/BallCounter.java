package frc.Utils;

import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.Timer;

/**
 * Code to count the balls stored inside the conveyor system for the 2020 season
 * @author Conner McKevitt
 */
public class BallCounter
    {

    Timer timer = null;

    public BallCounter(Timer timer)
        {
            this.timer = timer;
        }

    private int ballCount = 0;

    /**
     *
     * @return the current amount of balls in the system
     */
    public int getBallCount()
    {
        return ballCount;
    }

    /**
     * adds a ball to ballcount
     */
    public void addBall()
    {
        if (ballCount < MAX_BALLS)
            {
            ballCount++;
            }
    }

    /**
     *adds a ball based if button is pushed
     * @param button button to push
     */
    public void addBall(JoystickButton button)
    {
        //starts a timer the first time the button is pressed. this prevents the count from changing to fast
        if (button.get() && this.timer.get() > .2 || firstTime == true)
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

    /**
     * subtracts a ball from ballcount
     */
    public void subtractBall()
    {
        if (ballCount > MIN_BALLS)
            {
            ballCount--;
            }
    }

    /**
     * subtracts from ballCount once the button is pushed
     * @param button
     */
    public void subtractBall(JoystickButton button)
    {
        //starts a timer the first time the button is pressed. this prevents the count from changing to fast
        if (button.get() && this.timer.get() > .2 || firstTime == true)
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

    /**
     * sets the count to 0 for use in auto
     */
    public void clearCount()
    {
        ballCount = 0;
    }

    /**
     * set the count to zero if both buttons are true
     * @param button1
     * @param button2
     */
    public void clearCount(JoystickButton button1, JoystickButton button2)
    {
        if (button1.get() && button2.get())
            {
            ballCount = 0;
            }
    }

    //for the add/sutract by button to start the timer
    private boolean firstTime = true;

    private final int MAX_BALLS = 5;
    private final int MIN_BALLS = 0;
    }

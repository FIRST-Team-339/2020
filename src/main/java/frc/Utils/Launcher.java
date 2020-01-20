package frc.Utils;

import frc.Hardware.Hardware;
import frc.HardwareInterfaces.LightSensor;
import edu.wpi.first.wpilibj.SpeedController;

public class Launcher{

    private LightSensor intake = null;
    private LightSensor shooting = null;
    private LightSensor  upper = null;
    private LightSensor lower = null;
    //private motortype shootingMotor = null;
    //private motortype intakeMotor = null;


    public Launcher(LightSensor intake, LightSensor shooting, LightSensor upper, LightSensor lower, SpeedController intakeMotor, SpeedController shootingMotor){

        this.intake = intake;
        this.shooting = shooting;
        this.upper = upper;
        this.lower = lower;
       // this.shootingMotor = shootingMotor
      //  this.intakeMotor = intakeMotor
    }

    enum LauncherState{
        INIT, INJECTION, LAUNCH, EJECTION, PREPARE_LAUNCH, OVERRIDE, PASSIVE
    }

    public LauncherState state = LauncherState.INIT;


    public void launcherUpdater(){

        //inject/eject balls

        switch(state){
            case INIT: 
                break;

            case PASSIVE:  

            if(Hardware.launchButton.get()){
                teleopLaunch(); 
                }
            if(Hardware.intakeButton.get()){
                intake();
                }         
                break;

            case INJECTION:   
                
                //if ball count != MAX_BALL_COUNT

                break;

            case EJECTION:  
                break;

            case PREPARE_LAUNCH:  
                break;

            case LAUNCH:  

              //maybe not use this case                
                break;

            case OVERRIDE:  
                break;

             default:
                state = LauncherState.PASSIVE;
            break;

        }

    }


    public boolean autoLaunch(){

            //make motor go vroom but automagically
        return false;
    }

    private void teleopLaunch(){

                //make motor go vroom
    }

    private void intake(){

        //make intake motor go vroom vroom
    }

    //variable===============
    public int currentBallCount = 0;


    private final int MAX_BALL_COUNT = 5;


}
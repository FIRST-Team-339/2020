package frc.Utils;

import frc.Hardware.Hardware;

public class Launcher{

    public Launcher(){


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
           
                break;

            case INJECTION:    
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

}
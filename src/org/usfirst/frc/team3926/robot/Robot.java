
package org.usfirst.frc.team3926.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Robot extends IterativeRobot {
	
	CANTalon talonSRX_FR; //Front Right
    CANTalon talonSRX_FL; //Front Left
    CANTalon talonSRX_BR; //Back Right
    CANTalon talonSRX_BL; //Back Left
    CANTalon talonSRX_Roller;
    CANTalon talonSRX_RollerArm;
    CANTalon talonSRX_WedgeArm;
    
    public void robotInit() {
        
    }
    
    
    public void testPeriodic() {
    	for (int i = 0; i < 6; ++i){
    		if (i == 0) {
    			talonSRX_FR.set(1);
    			Timer.delay(1);
    		} else if (i == 1) {
    			talonSRX_FR.set(0);
    			talonSRX_FL.set(1);
    			Timer.delay(1);
    		} else if (i == 2) {
    			talonSRX_FL.set(0);
    			talonSRX_BR.set(1);
    			Timer.delay(1);
    		} else if (i ==3) {
    			talonSRX_BR.set(0);
    			talonSRX_BL.set(1);
    			Timer.delay(1);
    		} else if (i == 4) {
    			talonSRX_BL.set(0);
    			talonSRX_Roller.set(1);
    			Timer.delay(1);
    		} else if (i == 5) {
    			talonSRX_Roller.set(0);
    			talonSRX_RollerArm.set(1);
    			Timer.delay(1);
    		} else if (i == 6) {
    			talonSRX_RollerArm.set(0);
    			talonSRX_WedgeArm.set(1);
    			Timer.delay(1);
    			i = 0;
    		} else {
    			SmartDashboard.putString("Halp", "Broken");
    		}
    	}//For loop
    }//testPeriodic
}//class

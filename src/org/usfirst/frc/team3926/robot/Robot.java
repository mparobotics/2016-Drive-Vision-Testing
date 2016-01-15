
package org.usfirst.frc.team3926.robot;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ShapeMode;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;


public class Robot extends IterativeRobot {
	CANTalon talonSRX_FR; //Front Right
	CANTalon talonSRX_FL; //Front Left
	CANTalon talonSRX_BR; //Back Right
	CANTalon talonSRX_BL; //Back Left

	RobotDrive driveSystem;
	Joystick leftStick;
	Joystick rightStick;
	
	int session;
    Image frame;
	
    CameraServer server;
    
    double leftInput;
	double rightInput;
    
	
    public void robotInit() {
    	talonSRX_FR = new CANTalon(2); //CAN ID (not position in loop)
    	talonSRX_FL = new CANTalon(1);
    	talonSRX_BR = new CANTalon(3);
    	talonSRX_BL = new CANTalon(11);
    	
    	driveSystem = new RobotDrive(talonSRX_FL, talonSRX_BL, talonSRX_FR, talonSRX_BR);
    	
    	leftStick = new Joystick(0); //USB 0
    	rightStick = new Joystick(1); //USB 1
    	
    	//server = CameraServer.getInstance();
        //server.setQuality(60);
        //server.startAutomaticCapture("cam0");
        
        frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);

        // the camera name (ex "cam0") can be found through the roborio web interface
        session = NIVision.IMAQdxOpenCamera("cam0",
                NIVision.IMAQdxCameraControlMode.CameraControlModeController);
        NIVision.IMAQdxConfigureGrab(session);
        
    }

    
    public void autonomousPeriodic() {

    }


    public void teleopPeriodic() {
    	cameraThing();
    	leftInput = leftStick.getY() * -1; //leftInput = left Y
    	rightInput = rightStick.getY() * -1; //rightInput = right Y
    		
        if (leftStick.getRawButton(1)) { //Saftey mode
        	leftInput /= 2;
        	rightInput /= 2;
        }
        if (rightStick.getRawButton(1)) leftInput = rightInput; //Forward mode
        
        driveSystem.tankDrive(leftInput, rightInput);

        Timer.delay(0.005);		// wait for a motor update time
    } //End teleopPeriodic
    

    public void testPeriodic() {
        
        
    }
    
    public void cameraThing() {
    	NIVision.Rect rect = new NIVision.Rect(200, 250, 100, 100);


        NIVision.IMAQdxGrab(session, frame, 1);
        NIVision.imaqDrawShapeOnImage(frame, frame, rect,
                DrawMode.DRAW_VALUE, ShapeMode.SHAPE_OVAL, 0.0f);
        
        CameraServer.getInstance().setImage(frame);
    }
    
}


package org.usfirst.frc.team3926.robot;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.ShapeMode;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


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
    
    Encoder distanceEncoder;
    
    double leftInput;
	double rightInput;
	
	int autoStart = 1; //the number of the defense the robot is starting in front of
	int autoDrive1 = 0; //Values used (set in autoInit) to determine how far to drive
	int autoDrive2 = 0;
	int autoDrive3 = 0;
	int autoDrive4 = 0;
	int autoRotate1 = 0; //Amount (in degrees) that the robot needs to rotate to turn to the low goal
	
	double autoSpeed = 0;
	double autoDirection = 0;
	double autoRotate = 0;
	int rotateCounter = 0;
	
	boolean rotateDone = false;
	double deltaTime = 0;
    
    public void robotInit() {
    	talonSRX_FR = new CANTalon(2); //CAN ID (not position in loop)
    	talonSRX_FL = new CANTalon(1);
    	talonSRX_BR = new CANTalon(3);
    	talonSRX_BL = new CANTalon(11);
    	
    	driveSystem = new RobotDrive(talonSRX_FL, talonSRX_BL, talonSRX_FR, talonSRX_BR); //Setup the driveSystem
    	
    	leftStick = new Joystick(0); //USB 0
    	rightStick = new Joystick(1); //USB 1
        
    	session = NIVision.IMAQdxOpenCamera("cam0",
                 NIVision.IMAQdxCameraControlMode.CameraControlModeController);
        NIVision.IMAQdxConfigureGrab(session);
    	
        frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);

        distanceEncoder = new Encoder(0, 1, 2, true);
        
        distanceEncoder.setMaxPeriod(.1); //Maximum period (in seconds) where the encoder is still considered moving
        distanceEncoder.setDistancePerPulse(4/48); //4 inches per pulse 
        
        cameraThing();
    } //End robotInit()
    
    public void autonomousPeriodic() {
    	if (autoStart ==1) {
    		if (distanceEncoder.getDistance() < 116) autoSpeed = .5;
    		else if (!rotateDone) {
    			if (deltaTime == 0) deltaTime = System.currentTimeMillis();
    			else if (System.currentTimeMillis() - deltaTime < 1.5) autoRotate = .5;
    			else {
    				rotateDone = true;
    				distanceEncoder.reset();
    				autoRotate = 0;
    			}
    		}
    		else if (distanceEncoder.get() < 108) autoSpeed = .5;
    		else SmartDashboard.putString("Autonomous Status", "Done"); //Tell the user that autonomous is done
    	} //End start position 1
    	
    	else if (autoStart == 2) {
    		//TODO make code for starting position 2
    	}
    	
    	else if (autoStart == 3) {
    		//TODO make code for starting position 3
    	}
    	
    	else if (autoStart == 4) {
    		//TODO make code for starting position 4
    	}
    	
    	else if (autoStart == 5) {
    		//TODO make code for starting position 5
    	}
    	
    	else if (autoStart == 6) {
    		//TODO make code for starting position from the secret passage
    	}
    	
    	else if (autoStart == 7) {
    		//TODO make code for starting position 1 on the other side
    	}
    	
    	else if (autoStart == 8) {
    		//TODO make code for starting position 2 on the other side
    	}
    	
    	else if (autoStart == 9) {
    		//TODO make code for starting position 3 on the other side
    	}
    	
    	else if (autoStart == 10) {
    		//TODO make code for starting position 4 on the other side
    	}
    	
    	else if (autoStart == 11) {
    		//TODO make code for starting position 5 on the other side
    	}
    	
    	else if (autoStart == 12) {
    		//TODO make code for starting position from the other secret passage
    	}
    	
    	driveSystem.mecanumDrive_Polar(autoSpeed, autoDirection, autoRotate); //We are using mecanum so that we can rotate
    } //end autonomousPeriodic()
    
    
    @SuppressWarnings("deprecation") //Cause they don't know how to drive station like we do
	public void teleopPeriodic() {
    	//cameraThing(); //Run the camera
    	leftInput = leftStick.getY() * -1; //leftInput = left Y
    	rightInput = (rightStick.getY() * -1); //rightInput = right Y
    		
        if (leftStick.getRawButton(1)) { //Saftey mode
        	leftInput /= 2;
        	rightInput /= 2;
        }
        
        if (rightStick.getRawButton(1)) leftInput = rightInput;
        
        if (leftStick.getRawButton(2)) {
        	leftInput = leftStick.getZ();
        	rightInput = leftInput * -1;
        }
        
        driveSystem.tankDrive(leftInput * .7, rightInput * .6, false); //1.6

        SmartDashboard.putInt("Encoder Count: ", distanceEncoder.get());
        SmartDashboard.putDouble("Encoder Distance: ", distanceEncoder.getDistance());
        SmartDashboard.putDouble("Left Speed", leftInput);
        SmartDashboard.putDouble("Right Speed", rightInput);
        Timer.delay(0.005);		// wait for a motor update time
    } //End teleopPeriodic
    
    
    public void cameraThing() { //We see things with this
    	NIVision.Rect rect = new NIVision.Rect(200, 250, 100, 100);

        NIVision.IMAQdxGrab(session, frame, 1);
        NIVision.imaqDrawShapeOnImage(frame, frame, rect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_OVAL, 0.0f);
        
        CameraServer.getInstance().setImage(frame);
        Timer.delay(0.005);
        cameraThing(); //TODO see if all this works
    } //End cameraThing()
} //End robot class

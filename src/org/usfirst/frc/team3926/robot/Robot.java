
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
	
	double autoSpeed = 0; //This stuff is used to control the robot during autonomous
	double autoDirection = 0;
	double autoRotate = 0;
	int rotateCounter = 0;
	
	boolean rotateDone = false;
	double deltaTime = 0; //This helps measure the time for rotations
	
	boolean sloppy = false;
	boolean fukit = false;
	boolean imsleep = false;
    
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
        distanceEncoder.setDistancePerPulse((4*(3*Math.PI))/48); //4pi inches per pulse
        
        cameraThing(); //TODO see if this works
    } //End robotInit()
    //////////////////^_^!!!END!!!^_^//////////////////
    
    public void autonomousPeriodic() {
    	if (autoStart ==1) { //TODO JOE: change this to a switch statement
    		if (distanceEncoder.getDistance() < 116 && !sloppy) autoSpeed = .5;
    		else if (!rotateDone) {
    			sloppy = true;
    			autoRotateX(1.5, rotateDone); //TODO Test this number
    		}
    		else if (distanceEncoder.getDistance() < 108) autoSpeed = .5;
    		else autoDone(); //Tell the user that autonomous is done
    	} //End autoStart 1
    	
    	else if (autoStart == 2) {
    		if (distanceEncoder.getDistance() < 116 && !sloppy) autoSpeed = .5;
    		else if (!rotateDone) {
    			sloppy = true;
    			autoRotateX(1.5, rotateDone); //TODO Test this rotate time
    		}
    		else if (distanceEncoder.getDistance() < 48) {
    			autoSpeed = .5;
    			rotateDone = false;
    		}
    		else if (!fukit) autoRotateX(1.75, fukit); //TODO Test this rotate time
    		else if (distanceEncoder.getDistance() <108 && !imsleep) autoSpeed = .5;
    		else autoDone();
    	} //End autoStart 1
    	
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
    //////////////////^_^!!!END!!!^_^//////////////////
    
    public void autoRotateX(double rotateTime, boolean reset) { //X corresponds to our marking on the board not the axis
    	if (deltaTime == 0) deltaTime = System.currentTimeMillis();
		else if (System.currentTimeMillis() - deltaTime < rotateTime) autoRotate = .5;
		else {
			reset = true;
			distanceEncoder.reset();
			autoRotate = 0;
		}
    }
    //////////////////^_^!!!END!!!^_^//////////////////
    
    public void autoDone() {
    	SmartDashboard.putString("Autonomous Status", "Done");;
    }
    //////////////////^_^!!!END!!!^_^//////////////////
    
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
    //////////////////^_^!!!END!!!^_^//////////////////
    
    public void cameraThing() { //We see things with this
    	NIVision.Rect rect = new NIVision.Rect(200, 250, 100, 100);

        NIVision.IMAQdxGrab(session, frame, 1);
        NIVision.imaqDrawShapeOnImage(frame, frame, rect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_OVAL, 0.0f);
        
        CameraServer.getInstance().setImage(frame);
        Timer.delay(0.005);
        cameraThing(); //TODO see if all this works
    } //End cameraThing()
} //End robot class

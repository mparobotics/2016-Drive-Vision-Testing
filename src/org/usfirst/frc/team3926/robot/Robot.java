//THIS IS STILL BETA CODE, IT IS STILL MESSY, IT MIGHT NOT ALL WORK
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
	CANTalon sketchMotor;

	RobotDrive driveSystem;
	Joystick leftStick;
	double leftInput;
	Joystick rightStick;
	double rightInput;
	
	int session;
    Image frame;
    CameraServer server;
    
    Encoder distanceEncoder;
	
	double autoSpeed = 0; //This stuff is used to control the robot during autonomous
	double autoDirection = 0;
	double autoRotate = 0;
	
	double deltaTime = 0; //This helps measure the time for rotations
	
	/**
	 * @param These eventTriggers are there to tell us if an event has happened in autonomous
	 */
	boolean eventTriggerOne = false;
	boolean eventTriggerTwo = false;
	boolean eventTriggerThree = false;
	boolean eventTriggerFour = false;
	boolean eventTriggerFive = false;
	
	static double ninetyDegreeTime = 10;
	static double magicAngleTime = 10; //This is the one angle that rotates us towards the low goal
	
	//Team colors coordinate to the defending alliance
	//These are to simplify the process of selecting the autonomous starting position
	//Because angles are reflected the starting positions are the same for both sides
	static int LowBar = 1;
	static int Two = 2;
	static int CrowdsChoice = 3;
	static int Four = 4;
	static int Five = 5;
	static int SecretPassage = 6;
	
	int autoEventCounter = 0;
		
	public int autoStart(int startPosition) { // Displays the start location on the Dash board
		String startSpot = "";
		switch (startPosition) {
			case 1: 
				startSpot = "Low Bar";
				break;
			case 2: 
				startSpot = "Spot Two";
				break;
			case 3: 
				startSpot = "Crowd Choice";
				break;
			case 4: 
				startSpot = "Defense 4";
				break;
			case 5: 
				startSpot = "Defense 5";
				break;
			case 6: 
				startSpot = "Secret Passage";
				break;
			default:
				startSpot = "broken";
				break;
		}
		
		SmartDashboard.putString("Autonomous Position: ", startSpot);
		
		return startPosition; //Set this to wherever you want the robot to start
	}
	////End autoStart()////
    
    public void robotInit() { // This is the drive system
    	talonSRX_FR = new CANTalon(2); //CAN ID (not position in loop)
    	talonSRX_FL = new CANTalon(1);
    	talonSRX_BR = new CANTalon(3);
    	talonSRX_BL = new CANTalon(11);
    	sketchMotor = new CANTalon(6);
    	
    	driveSystem = new RobotDrive(talonSRX_FL, talonSRX_BL, talonSRX_FR, talonSRX_BR); //Setup the driveSystem
    	
    	leftStick = new Joystick(0); //USB 0
    	rightStick = new Joystick(1); //USB 1
        
    	session = NIVision.IMAQdxOpenCamera("cam0", NIVision.IMAQdxCameraControlMode.CameraControlModeController);
        NIVision.IMAQdxConfigureGrab(session);
    	
        frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);
        
        /**
         * 0 Digital Input Output (DIO) port of B (B on the encoder)
         * 1 DIO port of A
         * 2 DIO port of X (the index)
         */
        distanceEncoder = new Encoder(0, 1, 2, true);
        distanceEncoder.setMaxPeriod(.1); //Maximum period (in seconds) where the encoder is still considered moving
        distanceEncoder.setDistancePerPulse((4*Math.PI)/48); //4pi inches per pulse TODO set to constant values
        
        
        SmartDashboard.putBoolean("eventTriggerOne", eventTriggerOne); // Declares which events have happened during autonomous on the Dash board 
    	SmartDashboard.putBoolean("eventTriggerTwo", eventTriggerTwo);
    	SmartDashboard.putBoolean("eventTriggerThree", eventTriggerThree);
    	SmartDashboard.putBoolean("eventTriggerFour", eventTriggerFour);
    	SmartDashboard.putBoolean("eventTriggerFive", eventTriggerFive);
    	distanceEncoder.reset();
    	SmartDashboard.putDouble("Encoder Distance: ", distanceEncoder.getDistance() *-1);
    } 
    ////End robotInit()////
    
    public void autonomousInit() {
    	//We currently do not need anything in here, but if we want it we have it
    	
    }
    
    public void autonomousPeriodic() {
    	/**
    	 * For clarification on the meaning of these numbers see the above function autoStart() and the integers above it
    	 * Set the starting position within the method call within autoStart() below
    	 * Choices:
    	 * 	LowBar
    	 * 	Two
    	 * 	CrowdsChoice
    	 * 	Four
    	 * 	Five
    	 * 	SecretPassage
    	 */
    	switch (autoStart(LowBar)) {
	    	case 1:
	    		if (!eventTriggerOne) autoDriveTo(116);
	    		else if (!eventTriggerTwo) autoRotate(magicAngleTime, true);
	    		else if (!eventTriggerThree) autoDriveTo(108);
	    		else autoDone(); //Tell the user that autonomous is done
	    		break;
	    	
	    	case 2:
	    		if (!eventTriggerOne) autoDriveTo(116);
	    		else if (!eventTriggerTwo) autoRotate(ninetyDegreeTime, false); //False == left
	    		else if (!eventTriggerThree) autoDriveTo(48);
	    		else if (!eventTriggerFour) autoRotate(magicAngleTime  - ninetyDegreeTime, true); //True == right We subtract the ninety time because we have already turned 90 degrees of the turn
	    		else if (!eventTriggerFive) autoDriveTo(108);
	    		else autoDone();
	    		break;
	    	
	    	case 3: 
	    		if (!eventTriggerOne) autoDriveTo(116);
	    		else if (!eventTriggerTwo) autoRotate(ninetyDegreeTime, false);
	    		else if (!eventTriggerThree) autoDriveTo(96);
	    		else if (!eventTriggerFour) autoRotate(magicAngleTime  - ninetyDegreeTime, true);
	    		else if (!eventTriggerFive) autoDriveTo(108);
	    		else autoDone();
	    		break;
	    	
	    	case 4:
	    		if (!eventTriggerOne) autoDriveTo(116);
	    		else if (!eventTriggerTwo) autoRotate(ninetyDegreeTime, true);
	    		else if (!eventTriggerThree) autoDriveTo(48);
	    		else if (!eventTriggerFour) autoRotate(magicAngleTime  - ninetyDegreeTime, true);
	    		else if (!eventTriggerFive) autoDriveTo(108);
	    		else autoDone();
	    		break;
	    	
	    	case 5:
	    		if (!eventTriggerOne) autoDriveTo(116);
	    		else if (!eventTriggerTwo) autoRotate(magicAngleTime, false);
	    		else if (!eventTriggerThree) autoDriveTo(108);
	    		else autoDone();
	    		break;
	    	
	    	case 6: //Starts behind 5
	    		if (!eventTriggerOne) autoDriveTo(66); //TODO I'll finish this function
	    		else if (!eventTriggerTwo) autoRotate(ninetyDegreeTime, false);
	    		/*
	    		 * go 66
	    		 * turn right 90
	    		 * go 170
	    		 * go 66
	    		 * do 5 - 90
	    		 */
	    		break;
    	}
    	
    	SmartDashboard.putBoolean("eventTriggerOne", eventTriggerOne);
    	SmartDashboard.putBoolean("eventTriggerTwo", eventTriggerTwo);
    	SmartDashboard.putBoolean("eventTriggerThree", eventTriggerThree);
    	SmartDashboard.putBoolean("eventTriggerFour", eventTriggerFour);
    	SmartDashboard.putBoolean("eventTriggerFive", eventTriggerFive);
    	
    	sketchMotor.set(.05);
    	SmartDashboard.putDouble("Encoder Distance: ", distanceEncoder.getDistance() *-1);
    	
    	driveSystem.mecanumDrive_Polar(autoSpeed, autoDirection, autoRotate); //We are using mecanum so that we can rotate
    	cameraThing();
    } 
    ////End autonomousPeriodic()////
    
    /** THIS FUNCTION MOVES THE ROBOT IN AUTONOMOUS MODE
     * @param driveDistance: The distance to drive forward (in inches)
     * @param eventHappened: boolean to use to tell the system that this took place
     */
    public void autoDriveTo(int driveDistance) {
    	if (distanceEncoder.getDistance() * -1 < driveDistance) autoSpeed = 0.25;
    	else eventCheck();
    }
    ////autoDriveTo()////
    
    /** THIS FUNCTION ROTATES THE ROBOT
     * @param rotateTime: The time (in seconds) for the robot to rotate
     * @param setTrue: The boolean to set to true when the rotate has finished, this handles events to make sure they don't repeat
     * @param rotateRight: Set to true if you're trying to rotate to the right
     * @param resetDistance: Tell it to reset the encoders distance, this is here because it makes it easier to manage the logic
     * ^ whenever we stop driving straight, we're going to rotate
     */
    public void autoRotate(double rotateTime, boolean rotateRight) { //X corresponds to our marking on the board not the axis
    	if (deltaTime == 0) deltaTime = System.currentTimeMillis();
		else if (System.currentTimeMillis() - deltaTime < rotateTime) autoRotate = 0.25;
		else {
			eventCheck();
			distanceEncoder.reset();
			autoRotate = 0; //Set the speed to 0 to prevent the robot from rotating more
		}
    	
    	if (rotateRight) autoRotate *= -1;
    	
    	SmartDashboard.putDouble("RotateTime", System.currentTimeMillis() - deltaTime);
    }
    ////End autoRotate()////
    
    public void eventCheck() {
    	switch(autoEventCounter) { //Ordered like this so that they don't chain
		case 4:
			eventTriggerFive = true;
			++autoEventCounter;
			break;
		case 3:
			eventTriggerFour = true;
			++autoEventCounter;
			break;
		case 2:
			eventTriggerThree = true;
			++autoEventCounter;
			break;
		case 1:
			eventTriggerTwo = true;
			++autoEventCounter;
			break;
		case 0:
			eventTriggerOne = true;
			++autoEventCounter;
			break;
		}
    	autoSpeed = 0;
    }
    
    public void autoDone() {
    	SmartDashboard.putString("Autonomous Status", "Done");;
    }
    ////End autoDone()////
    
    @SuppressWarnings("deprecation") //Cause they don't know how to drive station like we do
	public void teleopPeriodic() {
    	//cameraThing(); //Run the camera TODO test new recursive function before using this
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
        
        driveSystem.tankDrive(leftInput * .7, rightInput * .6, false); //These multipliers are to attempt to compensate for the wacky gearbox

        SmartDashboard.putInt("Encoder Count: ", distanceEncoder.get());
        SmartDashboard.putDouble("Encoder Distance: ", distanceEncoder.getDistance());
        SmartDashboard.putDouble("Left Speed", leftInput);
        SmartDashboard.putDouble("Right Speed", rightInput);
        Timer.delay(0.005);		// wait for a motor update time
        
        cameraThing();
    } 
    ////End teleopPeriodic()////
    
    public void cameraThing() { //We see things with this
    	NIVision.Rect rect = new NIVision.Rect(200, 250, 100, 100);

        NIVision.IMAQdxGrab(session, frame, 1);
        NIVision.imaqDrawShapeOnImage(frame, frame, rect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_OVAL, 0.0f);
        
        CameraServer.getInstance().setImage(frame);
        Timer.delay(0.005);
    } 
    ////End cameraThing()////
} //End robot class
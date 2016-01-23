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

	RobotDrive driveSystem;
	Joystick leftStick;
	Joystick rightStick;
	
	int session;
    Image frame;
    CameraServer server;
    
    Encoder distanceEncoder;
    
    double leftInput;
	double rightInput;
	
	double autoSpeed = 0; //This stuff is used to control the robot during autonomous
	double autoDirection = 0;
	double autoRotate = 0;
	int rotateCounter = 0;
	
	double deltaTime = 0; //This helps measure the time for rotations
	
	/**
	 * @param These eventTriggers are there to tell us if an event has happened in autonomous
	 */
	boolean eventTriggerOne = false;
	boolean eventTriggerTwo = false;
	boolean eventTriggerThree = false;
	boolean eventTriggerFour = false;
	boolean eventTriggerFive = false;
	
	static double ninetyDegreeTime = 1.75;
	static double magicAngleTime = 1.5; //This is the one angle that rotates us towards the low goal
	
	//Team colors cordinate to the defending allience
	//These are to simplify the process of selecting the autonomous starting position
	//Because angles are reflected the starting positions are the same for both sides
	static int LowBar = 1;
	static int Two = 2;
	static int CrowdsChoice = 3;
	static int Four = 4;
	static int Five = 5;
	static int SecretPassage = 6;
		
	public int autoStart(int startPosition) {
		return startPosition; //Set this to wherever you want the robot to start
	}
    
    public void robotInit() {
    	talonSRX_FR = new CANTalon(2); //CAN ID (not position in loop)
    	talonSRX_FL = new CANTalon(1);
    	talonSRX_BR = new CANTalon(3);
    	talonSRX_BL = new CANTalon(11);
    	
    	driveSystem = new RobotDrive(talonSRX_FL, talonSRX_BL, talonSRX_FR, talonSRX_BR); //Setup the driveSystem
    	
    	leftStick = new Joystick(0); //USB 0
    	rightStick = new Joystick(1); //USB 1
    	
    	if (leftStick.getRawButton(1)) SmartDashboard.putBoolean("Left Stick Trigger", true);
    	else SmartDashboard.putBoolean("Left Stick Trigger", false);
    	
    	
    	if (leftStick.getRawButton(2)) SmartDashboard.putBoolean("Left Stick Button 2", true); 
    	else SmartDashboard.putBoolean("Left Stick Button 2", false);
    		
    	
    	if (leftStick.getRawButton(3)) SmartDashboard.putBoolean("Left Stick Button 3", true);
    	else SmartDashboard.putBoolean("Left Stick Button 3", false);
    			
    	
    	if (leftStick.getRawButton(4)) SmartDashboard.putBoolean("Left Stick Button 4", true);
    	else SmartDashboard.putBoolean("Left Stick Button 3", false);
    			
    	if (leftStick.getRawButton(5)) SmartDashboard.putBoolean("Left Stick Button 5", true);
    	else SmartDashboard.putBoolean("Left Stick Button 5", false);
    	
    	if (leftStick.getRawButton(6)) SmartDashboard.putBoolean("Left Stick Button 6", true);
    	else SmartDashboard.putBoolean("Left Stick Button 6", false);
    	
    	if (leftStick.getRawButton(7)) SmartDashboard.putBoolean("Left Stick Button 7", true);
    	else SmartDashboard.putBoolean("Left Stick Button 7", false);
    	
    	if (leftStick.getRawButton(8)) SmartDashboard.putBoolean("Left Stick Button 8", true);
    	else SmartDashboard.putBoolean("Left Stick Button 8", false);
    	
    	if (leftStick.getRawButton(9)) SmartDashboard.putBoolean("Left Stick Button 9", true);
    	else SmartDashboard.putBoolean("Left Stick Button 9", false);
    	
    	if (leftStick.getRawButton(10)) SmartDashboard.putBoolean("Left Stick Button 10", true);
    	else SmartDashboard.putBoolean("Left Stick Button 10", false);
    	
    	if (leftStick.getRawButton(11)) SmartDashboard.putBoolean("Left Stick Button 11", true);
    	else SmartDashboard.putBoolean("Left Stick Button 11", false);
    	
    	if (leftStick.getRawButton(12)) SmartDashboard.putBoolean("Left Stick Button 12", true);
    	else SmartDashboard.putBoolean("Left Stick Button 12", false);
    	
    	
    	
  
        
    	session = NIVision.IMAQdxOpenCamera("cam0", NIVision.IMAQdxCameraControlMode.CameraControlModeController);
        NIVision.IMAQdxConfigureGrab(session);
    	
        frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);

        distanceEncoder = new Encoder(0, 1, 2, true);
        
        distanceEncoder.setMaxPeriod(.1); //Maximum period (in seconds) where the encoder is still considered moving
        distanceEncoder.setDistancePerPulse((4*(3*Math.PI))/48); //4pi inches per pulse TODO set to constant values
        
        cameraThing(); //TODO see if this works
    } //End robotInit()
    //////////////////END//////////////////
    
    public void autonomousInit() {
    	
    }
    
    public void autonomousPeriodic() {
    	/**
    	 * For clarification on the meaning of these numbers see the above function autoStart() and the integers above it
    	 * Set the starting position within the method call here
    	 */
    	switch (autoStart(LowBar)) {
	    	case 1:
	    		if (!eventTriggerOne) autoDriveTo(116, eventTriggerOne);
	    		else if (!eventTriggerTwo) autoRotate(magicAngleTime, eventTriggerTwo, true, true);
	    		else if (!eventTriggerThree) autoDriveTo(108, eventTriggerThree);
	    		else autoDone(); //Tell the user that autonomous is done
	    		break;
	    	
	    	case 2: //Starting Position 2, located 1 from the bottom of team 1 start 
	    		if (!eventTriggerOne) autoDriveTo(116, eventTriggerOne);
	    		else if (!eventTriggerTwo) autoRotate(ninetyDegreeTime, eventTriggerTwo, false, true); //False == left
	    		else if (!eventTriggerThree) autoDriveTo(48, eventTriggerThree);
	    		else if (!eventTriggerFour) autoRotate(magicAngleTime  - ninetyDegreeTime, eventTriggerFour, true, true); //True == right We subtract the ninety time because we have already turned 90 degrees of the turn
	    		else if (!eventTriggerFive) autoDriveTo(108, eventTriggerFive);
	    		else autoDone();
	    		break;
	    	
	    	case 3: 
	    		if (!eventTriggerOne) autoDriveTo(116, eventTriggerOne);
	    		else if (!eventTriggerTwo) autoRotate(ninetyDegreeTime, eventTriggerTwo, false, true);
	    		else if (!eventTriggerThree) autoDriveTo(96, eventTriggerThree);
	    		else if (!eventTriggerFour) autoRotate(magicAngleTime  - ninetyDegreeTime, eventTriggerFour, true, true);
	    		else if (!eventTriggerFive) autoDriveTo(108, eventTriggerFive);
	    		else autoDone();
	    		break;
	    	
	    	case 4:
	    		if (!eventTriggerOne) autoDriveTo(116, eventTriggerOne);
	    		else if (!eventTriggerTwo) autoRotate(ninetyDegreeTime, eventTriggerTwo, true, true);
	    		else if (!eventTriggerThree) autoDriveTo(48, eventTriggerThree);
	    		else if (!eventTriggerFour) autoRotate(magicAngleTime  - ninetyDegreeTime, eventTriggerFour, true, true);
	    		else if (!eventTriggerFive) autoDriveTo(108, eventTriggerFive);
	    		else autoDone();
	    		break;
	    	
	    	case 5:
	    		if (!eventTriggerOne) autoDriveTo(116, eventTriggerOne);
	    		else if (!eventTriggerTwo) autoRotate(magicAngleTime, eventTriggerTwo, false, true);
	    		else if (!eventTriggerThree) autoDriveTo(108, eventTriggerThree);
	    		else autoDone();
	    		break;
	    	
	    	case 6: //Starts behind 5
	    		if (!eventTriggerOne) autoDriveTo (66, eventTriggerOne); //TODO I'll finish this function
	    		/*
	    		 * go 66
	    		 * turn right 90
	    		 * go 170
	    		 * go 66
	    		 * do 5 - 90
	    		 */
	    		break;
    	}
    	
    	driveSystem.mecanumDrive_Polar(autoSpeed, autoDirection, autoRotate); //We are using mecanum so that we can rotate
    } //end autonomousPeriodic()
    //////////////////END///////////////
    /** THIS FUNCTION ROTATES THE ROBOT
     * @param rotateTime: The time (in seconds) for the robot to rotate
     * @param setTrue: The boolean to set to true when the rotate has finished, this handles events to make sure they don't repeat
     * @param rotateRight: Set to true if you're trying to rotate to the right
     * @param resetDistance: Tell it to reset the encoders distance, this is here because it makes it easier to manage the logic
     * ^ whenever we stop driving straight, we're going to rotate
     */
    public void autoRotate(double rotateTime, boolean setTrue, boolean rotateRight, boolean resetDistance) { //X corresponds to our marking on the board not the axis
    	if (deltaTime == 0) deltaTime = System.currentTimeMillis();
		else if (System.currentTimeMillis() - deltaTime < rotateTime) autoRotate = .5;
		else {
			setTrue = true;
			if (resetDistance) distanceEncoder.reset();
			autoRotate = 0; //Set the speed to 0 to prevent the robot from rotating more
		}

    	if (rotateRight) autoRotate *= -1;
    }
    //////////////////END////////////////
    /** THIS FUNCTION MOVES THE ROBOT IN AUTONOMOUS MODE
     * @param driveDistance: The distance to drive forward (in inches)
     * @param eventHappened: boolean to use to tell the system that this took place
     */
    public void autoDriveTo(int driveDistance, boolean eventHappened) {
    	if (distanceEncoder.getDistance() < driveDistance) autoSpeed = 0.5;
    	else eventHappened = true;
    	
    }
    //////////////////END////////////////
    
    public void autoDone() {
    	SmartDashboard.putString("Autonomous Status", "Done");;
    }
    //////////////////END////////////////
    
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
        
        driveSystem.tankDrive(leftInput * .7, rightInput * .6, false); //These multipliers are to attempt to compensate for the wacky gearbox

        SmartDashboard.putInt("Encoder Count: ", distanceEncoder.get());
        SmartDashboard.putDouble("Encoder Distance: ", distanceEncoder.getDistance());
        SmartDashboard.putDouble("Left Speed", leftInput);
        SmartDashboard.putDouble("Right Speed", rightInput);
        Timer.delay(0.005);		// wait for a motor update time
    } //End teleopPeriodic
    //////////////////END////////////////
    
    public void cameraThing() { //We see things with this
    	NIVision.Rect rect = new NIVision.Rect(200, 250, 100, 100);

        NIVision.IMAQdxGrab(session, frame, 1);
        NIVision.imaqDrawShapeOnImage(frame, frame, rect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_OVAL, 0.0f);
        
        CameraServer.getInstance().setImage(frame);
        Timer.delay(0.005);
        cameraThing(); //TODO see if all this works
    } //End cameraThing()
} //End robot class

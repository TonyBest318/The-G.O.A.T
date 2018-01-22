/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team6907.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import com.kauailabs.navx.frc.AHRS;
//import edu.wpi.first.wpilibj.SPI;
//import edu.wpi.first.wpilibj.PIDController;
//import edu.wpi.first.wpilibj.PIDOutput;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	private DifferentialDrive m_robotDrive
			= new DifferentialDrive(new Spark(0), new Spark(1));
	private Timer m_timer = new Timer();
	private Timer testTimer=new Timer();
	//private PIDOutput pout;
	//private AHRS ahrs=new AHRS(SPI.Port.kMXP);
	//private PIDController turnController = new PIDController(0.03, 0.0, 0.0, 0.0, ahrs, pout);
	private XboxController m_xbox=new XboxController(0);
	private double LeftPower, RightPower, throttle, turn, objspeed;
	private static final double G1=1.0;
	private static final double G2=0.6;
	private double gear = G1;
	private static final double acceleration_time=0.1;	//ReadOnly
	private static final double T1=0.4;
	private static final double T2=0.8;
	private double turn_modify=T1;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
	}

	/**
	 * This function is run once each time the robot enters autonomous mode.
	 */
	@Override
	public void autonomousInit() {
		m_timer.reset();
		m_timer.start();
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		// Drive for 2 seconds
		if (m_timer.get() < 2.0) {
			m_robotDrive.arcadeDrive(0.5, 0.0); // drive forwards half speed
		} else {
			m_robotDrive.stopMotor(); // stop robot
		}
	}

	/**
	 * This function is called once each time the robot enters teleoperated mode.
	 */
	@Override
	public void teleopInit() {
		testTimer.start();
		throttle=0.0;
		objspeed=0.0;
	}

	/**
	 * This function is called periodically during teleoperated mode.
	 */
	@Override
	public void teleopPeriodic() {
		SmartDashboard.putNumber("Left Hand Y", m_xbox.getY(GenericHID.Hand.kLeft));
		SmartDashboard.putNumber("Right Hand X", m_xbox.getX(GenericHID.Hand.kRight));
		SmartDashboard.putNumber("throttle", throttle);
		SmartDashboard.putNumber("turn", turn);
		if (m_xbox.getRawButtonPressed(5)) { 
			gear = (gear == G1) ? G2 : G1;
			turn_modify = (turn_modify == T1) ? T2 : T1;
		}
		
		
		objspeed = m_xbox.getY(GenericHID.Hand.kLeft) - throttle;
		throttle += objspeed/(acceleration_time/0.02);
		turn = m_xbox.getX(GenericHID.Hand.kRight) * turn_modify;
		
		LeftPower =  throttle + turn;
		LeftPower = (LeftPower>0) ? LeftPower+0.28 : LeftPower-0.28;
		RightPower = throttle - turn;
		RightPower = (RightPower>0) ? RightPower+0.28 : RightPower-0.28;
		
		m_robotDrive.tankDrive(LeftPower * gear, RightPower * gear, true);
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}

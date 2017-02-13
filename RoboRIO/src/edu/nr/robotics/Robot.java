
package edu.nr.robotics;

import java.util.ArrayList;

import edu.nr.lib.commandbased.DoNothingCommand;
import edu.nr.lib.interfaces.Periodic;
import edu.nr.lib.interfaces.SmartDashboardSource;
import edu.nr.lib.network.TCPServer;
import edu.nr.lib.network.TCPServer.NetworkingDataType;
import edu.nr.lib.network.TCPServer.Num;
import edu.nr.robotics.subsystems.shooter.Shooter;
import edu.nr.robotics.subsystems.turret.TurretMobileAngleCorrectionCommand;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	Command autonomousCommand;
	SendableChooser<Command> chooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		CameraServer.getInstance().startAutomaticCapture();

		autoChooserInit();
		tcpServerInit();
		OI.init();
	}
	
	/**
	 * Initialize an autonomous mode selector. The selector is sent to SmartDashboard to be set.
	 * 
	 * Set a default choice by calling {@link SendableChooser#addDefault} and set other choices by calling {@link SendableChooser#addObject}
	 */
	public void autoChooserInit() {
		chooser.addDefault("Do Nothing", new DoNothingCommand());
		SmartDashboard.putData("Auto mode", chooser);
	}
	
	/**
	 * Initialize the {@link TCPServer}
	 */
	public void tcpServerInit() {
		ArrayList<NetworkingDataType> turret_cam_types = new ArrayList<>();
		turret_cam_types.add(new NetworkingDataType('a', "angle")); //TODO: Coprocessor: Get angle sign and units
		turret_cam_types.add(new NetworkingDataType('d', "distance")); //TODO: Coprocessor: Get distance unit
		turret_cam_types.add(new NetworkingDataType('t', "time")); //TODO: Coprocessor: Code timestamps
		TCPServer.init(Num.turret, turret_cam_types, TCPServer.defaultPort);
		
		ArrayList<NetworkingDataType> gear_cam_types = new ArrayList<>();
		gear_cam_types.add(new NetworkingDataType('a', "angle")); //TODO: Coprocessor: Get angle sign and units
		gear_cam_types.add(new NetworkingDataType('d', "distance")); //TODO: Coprocessor: Get distance unit
		gear_cam_types.add(new NetworkingDataType('t', "time")); //TODO: Coprocessor: Code timestamps
		TCPServer.init(Num.gear, gear_cam_types, TCPServer.defaultPort + 1);
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	/**
	 * This function is called once each time the robot enters Autonomous mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot begins autonomous and to start the autonomous command.
	 */
	@Override
	public void autonomousInit() {
		autonomousCommand = chooser.getSelected();

		// schedule the autonomous command (example)
		if (autonomousCommand != null)
			autonomousCommand.start();
	}

	/**
	 * This function is called once each time the robot enters Teleop mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot begins teleop and to end the autonomous command.
	 */
	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running.
		if (autonomousCommand != null)
			autonomousCommand.cancel();
		
		new TurretMobileAngleCorrectionCommand().start();
	}

	/**
	 * This function is called periodically while the robot is disabled
	 */
	@Override
	public void disabledPeriodic() {
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		LiveWindow.run();
	}
	
	/**
	 * This function is called periodically during all robot modes
	 */
	@Override
	public void robotPeriodic() {
		Scheduler.getInstance().run();
		
		Periodic.runAll();
		SmartDashboardSource.runAll();
	}
}

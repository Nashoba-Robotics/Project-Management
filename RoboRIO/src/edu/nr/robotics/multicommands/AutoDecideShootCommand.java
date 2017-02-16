package edu.nr.robotics.multicommands;

import edu.nr.lib.commandbased.NRCommand;
import edu.nr.robotics.RobotMap;
import edu.nr.robotics.subsystems.hood.Hood;
import edu.nr.robotics.subsystems.hood.HoodStationaryAngleCorrectionCommand;
import edu.nr.robotics.subsystems.loader.Loader;
import edu.nr.robotics.subsystems.loader.LoaderRunCommand;
import edu.nr.robotics.subsystems.shooter.Shooter;
import edu.nr.robotics.subsystems.shooter.ShooterStationarySpeedCorrectionCommand;
import edu.nr.robotics.subsystems.turret.Turret;
import edu.nr.robotics.subsystems.turret.TurretStationaryAngleCorrectionCommand;

public class AutoDecideShootCommand extends NRCommand {
	
	public AutoDecideShootCommand() {
		super();
	}
	
	@Override
	public void onExecute() {
		if (Math.abs(AutoTrackingCalculationCommand.getHoodAngle() - Hood.getInstance().getPosition()) < RobotMap.SHOOT_HOOD_THRESHOLD / RobotMap.DEGREES_PER_ROTATION
				&& Math.abs(AutoTrackingCalculationCommand.getShooterSpeed() - Shooter.getInstance().getSpeed()) < RobotMap.SHOOT_SHOOTER_THRESHOLD
				&& Math.abs(AutoTrackingCalculationCommand.getTurretAngle() - Turret.getInstance().getPosition()) < RobotMap.SHOOT_TURRET_THRESHOLD / RobotMap.DEGREES_PER_ROTATION) {
			if (!Loader.getInstance().isRunning()) {
				new LoaderRunCommand().start();
			}
		} else if (Math.abs(HoodStationaryAngleCorrectionCommand.getHoodAngle() - Hood.getInstance().getPosition()) < RobotMap.SHOOT_HOOD_THRESHOLD / RobotMap.DEGREES_PER_ROTATION
				&& Math.abs(ShooterStationarySpeedCorrectionCommand.getShooterSpeed() - Shooter.getInstance().getSpeed()) < RobotMap.SHOOT_SHOOTER_THRESHOLD
				&& Math.abs(TurretStationaryAngleCorrectionCommand.getTurretAngle() - Turret.getInstance().getPosition()) < RobotMap.SHOOT_TURRET_THRESHOLD / RobotMap.DEGREES_PER_ROTATION) {
			if (!Loader.getInstance().isRunning()) {
				new LoaderRunCommand().start();
			}
		}
	}
	
	@Override
	public boolean isFinishedNR() {
		return false;
	}
}
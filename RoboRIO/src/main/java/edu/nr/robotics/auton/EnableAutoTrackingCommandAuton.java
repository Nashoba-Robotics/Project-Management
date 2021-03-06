package edu.nr.robotics.auton;

import edu.nr.lib.commandbased.NRCommand;
import edu.nr.lib.commandbased.NRSubsystem;
import edu.nr.lib.units.Angle;
import edu.nr.robotics.AutoTrackingCalculation;
import edu.nr.robotics.auton.AutoMoveMethods;
import edu.nr.robotics.auton.ShootAlignMode;
import edu.nr.robotics.subsystems.hood.Hood;
import edu.nr.robotics.subsystems.hood.HoodAutoAlignCommand;
import edu.nr.robotics.subsystems.hood.HoodStationaryAngleCorrectionCommand;
import edu.nr.robotics.subsystems.shooter.Shooter;
import edu.nr.robotics.subsystems.shooter.ShooterAutoAlignCommand;
import edu.nr.robotics.subsystems.shooter.ShooterStationarySpeedCorrectionCommand;
import edu.nr.robotics.subsystems.turret.Turret;
import edu.nr.robotics.subsystems.turret.TurretAutoAlignCommand;
import edu.nr.robotics.subsystems.turret.TurretStationaryAngleCorrectionCommand;


public class EnableAutoTrackingCommandAuton extends NRCommand{

	private static final Angle TURRET_CAMERA_RANGE = new Angle(10, Angle.Unit.DEGREE); 
	
	NRCommand hoodAlignCommand;
	NRCommand shooterAlignCommand;
	NRCommand turretAlignCommand;
	
	public EnableAutoTrackingCommandAuton() {
		//super(new NRSubsystem[] {Hood.getInstance(), Turret.getInstance(), Shooter.getInstance()});
		
		hoodAlignCommand = new HoodStationaryAngleCorrectionCommand();
		shooterAlignCommand = new ShooterStationarySpeedCorrectionCommand();
		turretAlignCommand = new TurretStationaryAngleCorrectionCommand();
	}
	
	@Override
	public void onExecute() {
		if (!AutoTrackingCalculation.getInstance().canSeeTarget()) {
			Turret.getInstance().setMotorSpeedInPercent(Turret.MAX_TRACKING_PERCENTAGE*Turret.getInstance().turretTrackDirection);
		}
	}
	
	@Override
	public void onEnd(boolean interrupted) {
		if(!interrupted) {
			Turret.getInstance().setPosition(Turret.getInstance().getPosition().add(TURRET_CAMERA_RANGE.mul(Turret.getInstance().turretTrackDirection)));
			Turret.getInstance().disable();
			if (AutoMoveMethods.shootAlignMode == ShootAlignMode.autonomous) {
			}
			hoodAlignCommand.start();
			shooterAlignCommand.start();
			turretAlignCommand.start();
		}
	}
	
	@Override
	public boolean isFinishedNR() {
		return AutoTrackingCalculation.getInstance().canSeeTarget();
	}
}

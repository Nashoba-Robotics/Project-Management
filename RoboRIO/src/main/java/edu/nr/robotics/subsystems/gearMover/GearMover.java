package edu.nr.robotics.subsystems.gearMover;

import edu.nr.lib.commandbased.DoNothingJoystickCommand;
import edu.nr.lib.commandbased.NRSubsystem;
import edu.nr.robotics.RobotMap;
import edu.nr.robotics.subsystems.EnabledSubsystems;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class GearMover extends NRSubsystem {
	public static GearMover singleton;

	public DoubleSolenoid GearMover;
	public DoubleSolenoid GearGetPosition;

	public enum GearMoverState {
		DEPLOYED, RETRACTED;
		
		public static Value DEPLOYED_VALUE = Value.kForward;
		public static Value RETRACTED_VALUE = Value.kReverse;
	}

	public enum GearGetPositionState {
		DEPLOYED, RETRACTED;
		
		public static Value DEPLOYED_VALUE = Value.kForward;
		public static Value RETRACTED_VALUE = Value.kReverse;

	}
	
	private GearMover() {
		if (EnabledSubsystems.GEAR_MOVER_ENABLED) {
			GearMover = new DoubleSolenoid(RobotMap.GEAR_MOVER_PCM_PORT, RobotMap.GEAR_MOVER_FORWARD,
					RobotMap.GEAR_MOVER_REVERSE);
			GearGetPosition = new DoubleSolenoid(RobotMap.GEAR_GET_POSITION_PCM_PORT,
					RobotMap.GEAR_GET_POSITION_FORWARD, RobotMap.GEAR_GET_POSITION_REVERSE);
		}
	}

	public static void init() {
		if (singleton == null) {
			singleton = new GearMover();
			singleton.setJoystickCommand(new DoNothingJoystickCommand(singleton));
		}
	}
	
	public GearMoverState currentGearMoverState() {
		if(GearMover != null) {
			if(GearMover.get() == GearMoverState.DEPLOYED_VALUE) {
				return GearMoverState.DEPLOYED;
			} else {
				return GearMoverState.RETRACTED;
			}
		} else {
			return GearMoverState.RETRACTED;
		}
	}
	
	public GearGetPositionState currentGearGetPositionState() {
		if(GearGetPosition != null) {
			if(GearGetPosition.get() == GearGetPositionState.DEPLOYED_VALUE) {
				return GearGetPositionState.DEPLOYED;
			} else {
				return GearGetPositionState.RETRACTED;
			}
		} else {
			return GearGetPositionState.RETRACTED;
		}
	}

	public void deployGearMover() {
		if(GearMover != null) {
			GearMover.set(GearMoverState.DEPLOYED_VALUE);
		}
	}

	public void retractGearMover() {
		if(GearMover != null) {
			GearMover.set(GearMoverState.RETRACTED_VALUE);
		}
	}

	public void outGearGetPosition() {
		if(GearGetPosition != null) {
			GearGetPosition.set(GearGetPositionState.DEPLOYED_VALUE);
		}
	}

	public void inGearGetPosition() {
		if(GearGetPosition != null) {
			GearGetPosition.set(GearGetPositionState.RETRACTED_VALUE);
		}
	}

	public static GearMover getInstance() {
		init();
		return singleton;
	}

	@Override
	public void smartDashboardInfo() {
		if(EnabledSubsystems.GEAR_MOVER_SMARTDASHBOARD_BASIC_ENABLED) {
			SmartDashboard.putString("Gear Mover Position", currentGearMoverState().toString());
			SmartDashboard.putString("Get Get Position", currentGearGetPositionState().toString());

		}
		if(EnabledSubsystems.GEAR_MOVER_SMARTDASHBOARD_COMPLEX_ENABLED) {
			
		}
	}

	@Override
	public void periodic() {

	}

	@Override
	public void disable() {
		if(GearMover != null)
			GearMover.set(Value.kOff);
		if(GearGetPosition != null)
			GearGetPosition.set(Value.kOff);
	}

	public boolean gearMoverIsDeployed() {
		return currentGearMoverState() == GearMoverState.DEPLOYED;
	}

	public boolean gearMoverIsRetracted() {
		return currentGearMoverState() == GearMoverState.RETRACTED;
	}

	public boolean gearGetPositionIsDeployed() {
		return currentGearGetPositionState() == GearGetPositionState.DEPLOYED;
	}

	public boolean gearGetPositionIsRetracted() {
		return currentGearGetPositionState() == GearGetPositionState.RETRACTED;
	}

}
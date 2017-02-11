package edu.nr.robotics.subsystems.gearMover;

import edu.nr.lib.commandbased.NRCommand;

/**
 * Deploy the gear mechanism.
 * 
 * This should be done at the beginning of autonomous and then hopefully never touched again. It will also be controlled by a button.
 *
 */
public class DeployGearCommand extends NRCommand {

	public DeployGearCommand() {
		super(GearMover.getInstance());
	}
	
}

package edu.nr.lib;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.command.Command;

/**
 * A modified version of the WPILib Command class that provides additional
 * lifecycle methods.
 *
 */
public class NRCommand extends Command {

	boolean forceCancel = false;
	
	ArrayList<NRSubsystem> subsystems = new ArrayList<NRSubsystem>();
	
	NRCommand(ArrayList<NRSubsystem> subsystems) {
		super();
		this.subsystems = subsystems;
		requires(subsystems);
	}

	/**
	 * Constructor used to set this commands visible name in SmartDashboard
	 * 
	 * @param name
	 */
	public NRCommand(ArrayList<NRSubsystem> subsystems, String name) {
		super(name);
		this.subsystems = subsystems;
		requires(subsystems);
	}

	public NRCommand(ArrayList<NRSubsystem> subsystems, String name, double timeout) {
		super(name, timeout);
		this.subsystems = subsystems;
		requires(subsystems);
	}

	public NRCommand(ArrayList<NRSubsystem> subsystems, double timeout) {
		super(timeout);
		this.subsystems = subsystems;
		requires(subsystems);
	}
	
	public NRCommand(NRSubsystem subsystem) {
		super();
		subsystems.add(subsystem);
		requires(subsystems);
	}

	/**
	 * Constructor used to set this commands visible name in SmartDashboard
	 * 
	 * @param name
	 */
	public NRCommand(NRSubsystem subsystem, String name) {
		super(name);
		subsystems.add(subsystem);
		requires(subsystems);
	}

	public NRCommand(NRSubsystem subsystem, String name, double timeout) {
		super(name, timeout);
		subsystems.add(subsystem);
		requires(subsystems);
	}

	public NRCommand(NRSubsystem subsystem, double timeout) {
		super(timeout);
		subsystems.add(subsystem);
		requires(subsystems);
	}
	

	
	public NRCommand() {
		super();
	}

	/**
	 * Constructor used to set this commands visible name in SmartDashboard
	 * 
	 * @param name
	 */
	public NRCommand(String name) {
		super(name);
	}

	public NRCommand(String name, double timeout) {
		super(name, timeout);
	}

	public NRCommand(double timeout) {
		super(timeout);
	}
	
	private void requires(ArrayList<NRSubsystem> subsystems) {
		for(NRSubsystem s : subsystems) {
			requires(s);
		}
	}

	private boolean reset;

	/**
	 * Called every time the command starts
	 */
	protected void onStart() {}

	/**
	 * Called every loop while the command is active
	 */
	protected void onExecute() {}

	/**
	 * Called when the command ends
	 * 
	 * @param interrupted
	 *            True if the command was interrupted
	 */
	protected void onEnd(boolean interrupted) {onEnd();}
	
	protected void onEnd() {}

	@Override
	protected void initialize() {
		onStart();
		reset = false;
	}

	@Override
	protected final void execute() {
		if (reset) {
			onStart();
			reset = false;
		}

		onExecute();
	}

	private void disableSubsystems() {
		for(NRSubsystem s : subsystems) {
			s.disable();
		}
	}
	
	@Override
	protected final void end() {
		reset = true;
		forceCancel = false;
		disableSubsystems();
		onEnd(false);
	}

	@Override
	protected final void interrupted() {
		reset = true;
		forceCancel = false;
		disableSubsystems();
		onEnd(true);
	}

	@Override
	protected final boolean isFinished() {
		return forceCancel || isFinishedNR();
	}
	
	protected boolean isFinishedNR() {return true;}
	
	protected final void makeFinish() {
		System.out.println(getName() + " was made to finish");
		if(getGroup() == null) 
			cancel();
		forceCancel = true;
	}
	
	public static void cancelCommand(Command command) {
		
		if(command == null)
			return;
		
		System.err.println("Cancelling " + command.getName());

		
		if(command instanceof NRCommand)
			((NRCommand) command).makeFinish();
		else {
			if(command.getGroup() != null)
				cancelCommand(command.getGroup());
			else { 
				command.cancel();
			}
		}
	}
}

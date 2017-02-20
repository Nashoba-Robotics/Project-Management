package edu.nr.robotics.subsystems.hood;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.nr.lib.Units;
import edu.nr.lib.commandbased.NRSubsystem;
import edu.nr.lib.sensorhistory.TalonEncoder;
import edu.nr.lib.units.Angle;
import edu.nr.lib.units.AngularSpeed;
import edu.nr.lib.units.Time;
import edu.nr.lib.units.Angle.Unit;
import edu.nr.robotics.RobotMap;
import edu.nr.robotics.subsystems.EnabledSubsystems;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Hood extends NRSubsystem {
	
	private static Hood singleton;

	private CANTalon talon;
	private TalonEncoder encoder;
	
	/**
	 * The speed that the motor is currently supposed to be running at
	 */
	private AngularSpeed speedSetpoint = AngularSpeed.ZERO;
	
	/**
	 * The position that the hood is currently supposed to be at
	 */
	private Angle positionSetpoint = Angle.ZERO;
	
	/**
	 * TODO: Hood: Find top position
	 */
	private static final Angle TOP_POSITION = Angle.ZERO;
	
	private static final Angle BOTTOM_POSITION = Angle.ZERO;

	//CANTalon PID Profile numbers
	private static final int MOTION_MAGIC = 0;
	private static final int OPERATOR_CONTROL = 1;
	
	private boolean autoAlign = false;

	/**
	 * The angle around the goal position that we can be at
	 * TODO: Hood: Find the position threshold
	 */
	public static final Angle POSITION_THRESHOLD = Angle.ZERO;

	/**
	 * The threshold the hood needs to be within to shoot
	 * TODO: Hood: Find shoot threshold
	 */
	public static final Angle SHOOT_THRESHOLD = Angle.ZERO;

	/**
	 * The max acceleration of the hood, in degrees per second per second
	 * TODO: Hood: Find max acceleration
	 */
	public static final double MAX_ACCELERATION = 0;

	/**
	 * The max speed of the hood, in degrees per second
	 * TODO: Hood: Find max speed
	 */
	public static final AngularSpeed MAX_SPEED = AngularSpeed.ZERO;
	
	//TODO: Hood: Find FPID values
	public static double F = MAX_SPEED.get(AngularSpeed.Unit.RPS) / Units.HUNDRED_MS_PER_SECOND * Units.MAGNETIC_NATIVE_UNITS_PER_REV;
	public static double P_MOTION_MAGIC = 0;
	public static double I_MOTION_MAGIC = 0;
	public static double D_MOTION_MAGIC = 0;
	public static double P_OPERATOR_CONTROL = 0;
	public static double I_OPERATOR_CONTROL = 0;
	public static double D_OPERATOR_CONTROL = 0;
	
	private Hood() { 
		if (EnabledSubsystems.HOOD_ENABLED) { 
			talon = new CANTalon(RobotMap.HOOD_TALON_PORT);
			
			if(EnabledSubsystems.HOOD_DUMB_ENABLED) {
				talon.changeControlMode(TalonControlMode.PercentVbus);
			} else {
				talon.changeControlMode(TalonControlMode.Speed);
			}
			talon.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
			talon.setPID(P_MOTION_MAGIC, I_MOTION_MAGIC, D_MOTION_MAGIC, F, (int)talon.getIZone(), talon.getCloseLoopRampRate(), MOTION_MAGIC);
			talon.setPID(P_OPERATOR_CONTROL, I_OPERATOR_CONTROL, D_OPERATOR_CONTROL, F, (int)talon.getIZone(), talon.getCloseLoopRampRate(), OPERATOR_CONTROL);
			talon.setProfile(OPERATOR_CONTROL);
			talon.setMotionMagicCruiseVelocity(MAX_SPEED.get(AngularSpeed.Unit.RPM));
			talon.setMotionMagicAcceleration(MAX_ACCELERATION / Units.DEGREES_PER_ROTATION * Units.SECONDS_PER_MINUTE);
			talon.enableBrakeMode(true);
			talon.reverseSensor(false); //TODO: Hood: Find phase
			talon.enable();
		}
	}

	public static Hood getInstance() {
		if (singleton == null)
			init();
		return singleton;
	}

	public synchronized static void init() {
		if (singleton == null) {
			singleton = new Hood();
			singleton.setJoystickCommand(new HoodJoystickCommand());
		}
	}
	
	private double addGearing(double in) {
		return in; //TODO: Hood: Gearing
	}
	
	private double removeGearing(double in) {
		return in; //TODO: Hood: Gearing
	}
	
	/**
	 * Sets motor speed of hood.
	 * 
	 * @param speed
	 *            the hood motor speed, from -1 to 1
	 */
	public void setMotorSpeedInPercent(double speed) {
		setMotorSpeedInDegreesPerSecond(MAX_SPEED.mul(speed));
	}

	/**
	 * Sets motor speed of hood.
	 * 
	 * @param speed
	 *            the hood motor speed, from -MAX_SPEED to MAX_SPEED
	 */
	public void setMotorSpeedInDegreesPerSecond(AngularSpeed speed) {
		speedSetpoint = speed;
		if (talon != null) {
			CANTalon.TalonControlMode mode = talon.getControlMode();
			if(mode == CANTalon.TalonControlMode.MotionMagic) {
				if(EnabledSubsystems.HOOD_DUMB_ENABLED) {
					talon.changeControlMode(TalonControlMode.PercentVbus);
				} else {
					talon.changeControlMode(TalonControlMode.Speed);
				}
			}
			if(mode == CANTalon.TalonControlMode.PercentVbus) {
				talon.set(addGearing(speedSetpoint.div(MAX_SPEED)));
			} else {
				talon.set(addGearing(speedSetpoint.get(AngularSpeed.Unit.RPM)));				
			}
		}
	}

	/**
	 * Gets the current speed of the talon
	 * 
	 * @return current position of talon
	 */
	public AngularSpeed getSpeed() {
		if(talon != null)
			return new AngularSpeed(removeGearing(talon.getSpeed()), AngularSpeed.Unit.RPM);
		return AngularSpeed.ZERO;
	}
	
	/**
	 * Set the goal position of the hood. 
	 * 
	 * @param position
	 * 			The goal position
	 */
	public void setPosition(Angle position) {
		positionSetpoint = position;
		if (talon != null) {
			CANTalon.TalonControlMode mode = talon.getControlMode();
			if(mode == CANTalon.TalonControlMode.Speed || mode == CANTalon.TalonControlMode.PercentVbus) {
				talon.changeControlMode(TalonControlMode.MotionMagic);
			}
			talon.set(addGearing(positionSetpoint.get(Unit.ROTATION)));
		}

	}

	/**
	 * @return Position
	 */
	public Angle getPosition() {
		if(talon != null) {
			return new Angle(removeGearing(talon.getPosition()), Unit.ROTATION);
		}
		return Angle.ZERO;
	}
	
	/**
	 * Get the position at a time in the past.
	 * @param deltaTime How long ago to look, in milliseconds
	 * @return The position
	 */
	public Angle getHistoricalPosition(Time deltaTime) {
		if (encoder != null)
			return new Angle(removeGearing(encoder.getPosition(deltaTime)), Unit.ROTATION);
		return Angle.ZERO;
	}
	
	/**
	 * Function that is periodically called once the Shooter class is initialized
	 */
	@Override
	public void periodic() {
		if(talon != null) {
			//TODO: Hood: Is forward limit switch top or bottom?
			if(talon.isFwdLimitSwitchClosed()) {
				talon.setPosition(TOP_POSITION.get(Unit.ROTATION));
			} else if(talon.isRevLimitSwitchClosed()) {
				talon.setPosition(BOTTOM_POSITION.get(Unit.ROTATION));
			} 
		}

	}

	/**
	 * Sends data to SmartDashboard
	 */
	@Override
	public void smartDashboardInfo() {
		if (talon != null) {
			if(EnabledSubsystems.HOOD_SMARTDASHBOARD_BASIC_ENABLED) {
				SmartDashboard.putNumber("Hood Current", talon.getOutputCurrent());
				SmartDashboard.putString("Hood Speed", getSpeed().get(AngularSpeed.Unit.DEGREEPERSECOND) + " : " + speedSetpoint.get(AngularSpeed.Unit.DEGREEPERSECOND));
				SmartDashboard.putString("Hood Position", getPosition().get(Angle.Unit.DEGREE) + " : " + positionSetpoint.get(Angle.Unit.DEGREE));				
			}
			if(EnabledSubsystems.HOOD_SMARTDASHBOARD_COMPLEX_ENABLED) {
				SmartDashboard.putNumber("Hood Voltage", talon.getOutputVoltage());
			}
		}
	}

	/**
	 * What subsystem does upon robot being disabled
	 */
	@Override
	public void disable() {
		setMotorSpeedInPercent(0);
		setPosition(getPosition());
	}

	public void setPID(double p, double i, double d) {
		if(talon != null) {
			talon.setPID(p, i, d);
		}
	}

	public boolean isMotionMagicMode() {
		if(talon != null) {
			return talon.getControlMode() == TalonControlMode.MotionMagic;
		}
		return false;
	}
	
	/**
	 * Used to see is the hood angle is being influenced by camera or by operator
	 * 
	 * @return is the shooter speed in autonomous mode
	 */
	public boolean isAutoAlign() {
		return autoAlign;
	}
	
	/**
	 * Sets the autoAlign mode to true or false 
	 * 
	 * @param autoAlign
	 * 		Is the subsystem going to be auto-aligned
	 */
	public void setAutoAlign(boolean autoAlign) {
		this.autoAlign = autoAlign;
	}
	
}
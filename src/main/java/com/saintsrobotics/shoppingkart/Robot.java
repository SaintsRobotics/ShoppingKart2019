package com.saintsrobotics.shoppingkart;

import java.util.function.Supplier;

import com.github.dozer.TaskRobot;
import com.github.dozer.coroutine.Task;
import com.github.dozer.coroutine.helpers.RunContinuousTask;
import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.github.dozer.input.OI.XboxInput;
import com.saintsrobotics.shoppingkart.config.OI;
import com.saintsrobotics.shoppingkart.config.Sensors;
import com.saintsrobotics.shoppingkart.config.TestSensors;
import com.saintsrobotics.shoppingkart.config.RobotMotors;
import com.saintsrobotics.shoppingkart.drive.SwerveWheel;
import com.saintsrobotics.shoppingkart.config.TestBotMotors;
import com.saintsrobotics.shoppingkart.lift.LiftControl;
import com.saintsrobotics.shoppingkart.lift.LiftInput;
import com.saintsrobotics.shoppingkart.lift.ToHeight;
import com.saintsrobotics.shoppingkart.manipulators.ArmsTask;
import com.saintsrobotics.shoppingkart.vision.DockTask;
import com.saintsrobotics.shoppingkart.manipulators.IntakeWheel;

import com.saintsrobotics.shoppingkart.manipulators.Kicker;
import com.saintsrobotics.shoppingkart.drive.SwerveControl;
import com.saintsrobotics.shoppingkart.drive.SwerveInput;
import com.saintsrobotics.shoppingkart.drive.ResetGyro;
import com.saintsrobotics.shoppingkart.drive.ToHeading;
import com.saintsrobotics.shoppingkart.util.UpdateMotors;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TaskRobot {

	private SendableChooser<Supplier<Task>> taskChooser;

	public RobotMotors motors;
	public Sensors sensors;
	public OI oi;
	public Flags flags;
	private double[] rightFrontLoc = { 12, 12 };
	private double[] leftFrontLoc = { -12, 12 };
	private double[] leftBackLoc = { -12, -12 };
	private double[] rightBackLoc = { 12, -12 };
	private double[] pivotLoc = { 0, 0 };
	private LiftControl liftControl;
	public SwerveControl swerveControl;

	public static Robot instance;

	@Override
	public void robotInit() {
		Robot.instance = this;
		taskChooser = new SendableChooser<>();
		this.oi = new OI();
		this.motors = new TestBotMotors();
		this.motors.init();
		this.sensors = new TestSensors();
		this.sensors.init();
		this.sensors.gyro.calibrate();
		this.sensors.gyro.reset();
		this.flags = new Flags();

		this.flags.pdp = new PowerDistributionPanel();

		NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(1);

	}

	@Override
	public void autonomousInit() {
	}

	@Override
	public void teleopInit() {

		RobotMotors motors = Robot.instance.motors;
		SwerveWheel rightFront = new SwerveWheel(motors.rightFront, motors.rightFrontTurner,
				Robot.instance.sensors.rightFrontTurnConfig, this.rightFrontLoc, this.pivotLoc);

		SwerveWheel leftFront = new SwerveWheel(motors.leftFront, motors.leftFrontTurner,
				Robot.instance.sensors.leftFrontTurnConfig, this.leftFrontLoc, this.pivotLoc);

		SwerveWheel leftBack = new SwerveWheel(motors.leftBack, motors.leftBackTurner,
				Robot.instance.sensors.leftBackTurnConfig, this.leftBackLoc, this.pivotLoc);

		SwerveWheel rightBack = new SwerveWheel(motors.rightBack, motors.rightBackTurner,
				Robot.instance.sensors.rightBackTurnConfig, this.rightBackLoc, this.pivotLoc);

		SwerveWheel[] wheels = { rightFront, leftFront, leftBack, rightBack };
		swerveControl = new SwerveControl(wheels, Robot.instance.sensors.gyro);
		SwerveInput swerveInput = new SwerveInput(this.oi.xboxInput, this.sensors.gyro, swerveControl, new DockTask());
		liftControl = new LiftControl(this.motors.lifter, this.sensors.liftEncoder, this.sensors.lifterUp,
				this.sensors.lifterDown);

		this.teleopTasks = new Task[] { new ResetGyro(() -> this.oi.xboxInput.Y()), swerveInput, swerveControl,
				liftControl,

				new ToHeading(() -> this.oi.xboxInput.DPAD_UP(), 0.0),
				new ToHeading(() -> this.oi.xboxInput.DPAD_RIGHT(), 90.0),
				new ToHeading(() -> this.oi.xboxInput.DPAD_DOWN(), 180.0),
				new ToHeading(() -> this.oi.xboxInput.DPAD_LEFT(), 270.0),

				// new ToHeight(() -> this.oi.xboxInput.B(), liftControl, 48.0),

				new LiftInput(this.oi.oppInput, this.liftControl),
				// new ResetLift(() -> this.oi.xboxInput.B(), this.liftControl),

				new IntakeWheel(() -> this.oi.oppInput.RB(), this.motors.intake),
				new ArmsTask(() -> this.oi.oppInput.B(), () -> this.oi.oppInput.X(), () -> this.oi.oppInput.A(),
						this.sensors.arms, this.motors.arms),

				new Kicker(() -> this.oi.oppInput.LB(), this.motors.kicker, this.sensors.kicker, 220, 109),

				new UpdateMotors(this.motors),

				new RunEachFrameTask() {
					@Override
					protected void runEachFrame() {
						// empty task for telemetries
					}
				} };

		super.teleopInit();
	}

	@Override
	public void disabledInit() {
		super.disabledInit();
	}
}

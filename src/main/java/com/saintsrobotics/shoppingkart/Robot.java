package com.saintsrobotics.shoppingkart;

import java.io.IOException;

import com.github.dozer.TaskRobot;
import com.github.dozer.coroutine.Task;
import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.saintsrobotics.shoppingkart.config.Config;
import com.saintsrobotics.shoppingkart.config.Motors;
import com.saintsrobotics.shoppingkart.config.OI;
import com.saintsrobotics.shoppingkart.config.RobotSensors;
import com.saintsrobotics.shoppingkart.config.Settings;
import com.saintsrobotics.shoppingkart.config.UpdateOperatorBoard;
import com.saintsrobotics.shoppingkart.drive.ResetGyro;
import com.saintsrobotics.shoppingkart.drive.SwerveControl;
import com.saintsrobotics.shoppingkart.drive.SwerveInput;
import com.saintsrobotics.shoppingkart.drive.SwerveWheel;
import com.saintsrobotics.shoppingkart.drive.ToHeading;
import com.saintsrobotics.shoppingkart.lift.LiftControl;
import com.saintsrobotics.shoppingkart.lift.LiftInput;
import com.saintsrobotics.shoppingkart.lift.ToHeight;
import com.saintsrobotics.shoppingkart.manipulators.IntakeWheel;
import com.saintsrobotics.shoppingkart.manipulators.Kicker;
import com.saintsrobotics.shoppingkart.tests.ClimbTest;
import com.saintsrobotics.shoppingkart.tests.SimpleLiftTask;
import com.saintsrobotics.shoppingkart.tests.TestDriveSwerveWheel;
import com.saintsrobotics.shoppingkart.tests.TestTurnSwerveWheel;
import com.saintsrobotics.shoppingkart.util.AbsoluteEncoder;
import com.saintsrobotics.shoppingkart.util.MotorRamping;
import com.saintsrobotics.shoppingkart.util.SparkMax;
import com.saintsrobotics.shoppingkart.util.UpdateMotors;
import com.saintsrobotics.shoppingkart.vision.DockTask;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TaskRobot {
	private Motors motors;
	private RobotSensors sensors;
	private Settings settings;
	private OI oi;
	private Flags flags;
	// private PowerDistributionPanel pewdiepie;

	@Override
	public void robotInit() {

		Config robotConfig;
		try {
			robotConfig = this.loadConfig();
		} catch (IOException ex) {
			DriverStation.reportError("Could not load config", false);
			return;
		}

		this.oi = new OI();
		this.motors = new Motors(robotConfig);
		this.sensors = new RobotSensors(robotConfig);
		this.settings = new Settings(robotConfig);

		this.sensors.gyro.calibrate();
		this.sensors.gyro.reset();

		this.sensors.liftEncoder.setOffset(this.settings.liftOffset);
		this.flags = new Flags();

		this.flags.pdp = new PowerDistributionPanel();

		CameraServer.getInstance().startAutomaticCapture();
		// NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(2);
		// this.pewdiepie = new PowerDistributionPanel();
		SmartDashboard.putBoolean("", true);

	}

	@Override
	public void autonomousInit() {
		teleopInit();
	}

	@Override
	public void teleopInit() {
		// MotorRamping rf = new MotorRamping(new SparkMax(0), true);
		SwerveWheel rightFront = new SwerveWheel(this.motors.rightFront, this.motors.rightFrontTurner,
				this.sensors.rightFrontEncoder, this.settings.wheelAnglePidConfig, this.settings.rightFrontLoc,
				this.settings.pivotLoc);

		SwerveWheel leftFront = new SwerveWheel(this.motors.leftFront, this.motors.leftFrontTurner,
				this.sensors.leftFrontEncoder, this.settings.wheelAnglePidConfig, this.settings.leftFrontLoc,
				this.settings.pivotLoc);

		SwerveWheel leftBack = new SwerveWheel(this.motors.leftBack, this.motors.leftBackTurner,
				this.sensors.leftBackEncoder, this.settings.wheelAnglePidConfig, this.settings.leftBackLoc,
				this.settings.pivotLoc);

		SwerveWheel rightBack = new SwerveWheel(this.motors.rightBack, this.motors.rightBackTurner,
				this.sensors.rightBackEncoder, this.settings.wheelAnglePidConfig, this.settings.rightBackLoc,
				this.settings.pivotLoc);

		SwerveWheel[] wheels = { rightFront, leftFront, leftBack, rightBack };
		SwerveControl swerveControl = new SwerveControl(wheels, this.sensors.gyro, this.settings.headingPidConfig);

		SwerveInput swerveInput = new SwerveInput(this.oi.xboxInput, this.sensors.gyro, swerveControl,
				new DockTask(this.settings.dockCargoTranslation, this.settings.dockCargoDistance,
						this.settings.dockHatchTranslation, this.settings.dockHatchDistance,
						this.settings.hatchTranslationTarget, this.settings.hatchDistanceTarget,
						this.settings.cargoTranslationTarget, this.settings.cargoDistanceTarget));

		LiftControl liftControl = new LiftControl(this.motors.lifter, this.sensors.liftEncoder, this.sensors.lifterUp,
				this.sensors.lifterDown, this.settings.liftPidConfig);

		this.teleopTasks = new Task[] {
				new ResetGyro(() -> this.oi.xboxInput.START(), this.sensors.gyro, swerveControl), swerveInput,
				swerveControl, liftControl,

				new ToHeading(() -> this.oi.xboxInput.DPAD_UP(), 0.0, swerveControl),
				new ToHeading(() -> this.oi.xboxInput.DPAD_RIGHT(), 90.0, swerveControl),
				new ToHeading(() -> this.oi.xboxInput.DPAD_DOWN(), 180.0, swerveControl),
				new ToHeading(() -> this.oi.xboxInput.DPAD_LEFT(), 270.0, swerveControl),
				new ToHeading(() -> this.oi.xboxInput.B(), 28.75, swerveControl),
				new ToHeading(() -> this.oi.xboxInput.Y(), 151.25, swerveControl),
				new ToHeading(() -> this.oi.xboxInput.X(), 208.75, swerveControl),
				new ToHeading(() -> this.oi.xboxInput.A(), 331.25, swerveControl),

				new LiftInput(this.oi.oppInput, this.sensors.liftEncoder, this.settings.liftUpperThrottle,
						this.settings.liftLowerThrottle, () -> this.oi.oppInput.liftBottom(), liftControl),
				new ToHeight(() -> this.oi.oppInput.cargo1(), liftControl, this.settings.liftCargo1),
				new ToHeight(() -> this.oi.oppInput.cargo2(), liftControl, this.settings.liftCargo2),
				new ToHeight(() -> this.oi.oppInput.cargoBall(), liftControl, this.settings.liftCargoShip),
				new ToHeight(() -> this.oi.oppInput.cargo3(), liftControl, this.settings.liftCargo3),
				new ToHeight(() -> this.oi.oppInput.hatch1(), liftControl, this.settings.liftHatch1),
				new ToHeight(() -> this.oi.oppInput.hatch2(), liftControl, this.settings.liftHatch2),
				new ToHeight(() -> this.oi.oppInput.hatch3(), liftControl, this.settings.liftHatch3),

				new IntakeWheel(() -> this.oi.oppInput.intakeIn(), this.motors.intake, 1),
				new IntakeWheel(() -> this.oi.oppInput.intakeOut(), this.motors.intake, -1),

				// new ClimbTest(this.motors.BackClimb, this.oi.oppInput, () ->
				// this.oi.oppInput.lowerLiftBack()),
				// new ClimbTest(this.motors.FrontClimb, this.oi.oppInput, () ->
				// this.oi.oppInput.lowerLift()),

				new Kicker(() -> this.oi.oppInput.kicker(), this.motors.kicker, this.sensors.kicker,
						this.settings.kickerUpperbound, this.settings.kickerLowerbound, this.settings.kickerBackpass),

				new UpdateOperatorBoard(this.oi.oppInput), new UpdateMotors(this.motors), new RunEachFrameTask() {
					// private NetworkTable limelight =
					// NetworkTableInstance.getDefault().getTable("limelight");

					@Override
					protected void runEachFrame() {
						// empty task for telemetries
						// SmartDashboard.putNumber("rf motor", rf.get());
						SmartDashboard.putNumber("gyro", sensors.gyro.getAngle());
						SmartDashboard.putNumber("kicker encoder", sensors.kicker.getRotation());
						SmartDashboard.putNumber("kicker motor", motors.kicker.get());
						SmartDashboard.putNumber("lift encoder", sensors.liftEncoder.getDistance());
						SmartDashboard.putNumber("lift motor", motors.lifter.get());

						// SmartDashboard.putNumber("tx", limelight.getEntry("tx").getDouble(0));
						// SmartDashboard.putNumber("ty", limelight.getEntry("ty").getDouble(0));
						// SmartDashboard.putNumber("ta", limelight.getEntry("ta").getDouble(0));

						SmartDashboard.putNumber("right front encoder", sensors.rightFrontEncoder.getRotation());
						SmartDashboard.putNumber("leftFront encoder", sensors.leftFrontEncoder.getRotation());
						SmartDashboard.putNumber("left back encoder", sensors.leftBackEncoder.getRotation());
						SmartDashboard.putNumber("right back encoder", sensors.rightBackEncoder.getRotation());

						SmartDashboard.putNumber("Controller x", oi.xboxInput.leftStickX());
						SmartDashboard.putNumber("Controller y", oi.xboxInput.leftStickY());

						// for (int i = 0; i < 16; i++) {
						// SmartDashboard.putNumber("pdp" + i, pewdiepie.getCurrent(i));
						// }

						double LFTemp = motors.LeftFrontSparkMax.getMotorTemperature();
						double RFTemp = motors.RightFrontSparkMax.getMotorTemperature();
						double LBTemp = motors.LeftBackSparkMax.getMotorTemperature();
						double RBTemp = motors.RightBackSparkMax.getMotorTemperature();

						double motorAverage = ((LFTemp + RFTemp + LBTemp + RBTemp) / 4);

						SmartDashboard.putNumber("Right Front Temp", motors.RightFrontSparkMax.getMotorTemperature());
						SmartDashboard.putNumber("Left Front Temp", motors.LeftFrontSparkMax.getMotorTemperature());
						SmartDashboard.putNumber("Right Back Temp", motors.RightBackSparkMax.getMotorTemperature());
						SmartDashboard.putNumber("Left Back Temp", motors.LeftBackSparkMax.getMotorTemperature());

						SmartDashboard.putNumber("Average Motor Temp", motorAverage);

						SmartDashboard.putNumber("Encoder Values",
								motors.RightFrontSparkMax.getEncoder().getPosition());

						SmartDashboard.putNumber("Kicker Encoder", sensors.kicker.getRotation());
						if (LFTemp > 70 | RFTemp > 70 | LBTemp > 70 | RBTemp > 70) {
							DriverStation.reportError("Motors are hot", false);
							SmartDashboard.putBoolean("", false);
						} else {
							SmartDashboard.putBoolean("", true);
						}
					}
				} };

		super.teleopInit();

	}

	@Override
	public void disabledInit() {
		super.disabledInit();
	}

	/**
	 * This method loads the config from disk. It will default to the competition
	 * config. To use the test config, put a jumper on digital input 10.
	 * 
	 * @return The config
	 * @throws IOException
	 */
	private Config loadConfig() throws IOException {
		DigitalInput testBotJumper = new DigitalInput(9);
		// if true is passed into fromFile(), runs test configs
		Config robotConfig = Config.fromFile(!testBotJumper.get());
		testBotJumper.close();
		return robotConfig;
	}
}

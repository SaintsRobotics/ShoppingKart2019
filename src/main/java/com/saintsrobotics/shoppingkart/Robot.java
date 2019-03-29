package com.saintsrobotics.shoppingkart;

import java.io.IOException;

import com.github.dozer.TaskRobot;
import com.github.dozer.coroutine.Task;
import com.github.dozer.coroutine.helpers.RunEachFrameTask;
import com.saintsrobotics.shoppingkart.arms.ArmsControl;
import com.saintsrobotics.shoppingkart.arms.ArmsTarget;
import com.saintsrobotics.shoppingkart.arms.ResetArms;
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
import com.saintsrobotics.shoppingkart.manipulators.DetatchPanel;
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

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Spark;
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
	private PowerDistributionPanel pewdiepie;

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
		NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(2);
		this.pewdiepie = new PowerDistributionPanel();

	}

	@Override
	public void autonomousInit() {
		// teleopInit();
	}

	@Override
	public void teleopInit() {
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
				new DockTask(this.settings.dockTranslationPidConfig, this.settings.dockDistancePidConfig,
						this.settings.hatchTranslationTarget, this.settings.hatchDistanceTarget,
						this.settings.cargoTranslationTarget, this.settings.cargoDistanceTarget));

		LiftControl liftControl = new LiftControl(this.motors.lifter, this.sensors.liftEncoder, this.sensors.lifterUp,
				this.sensors.lifterDown, this.settings.liftUpperThrottle, this.settings.liftLowerThrottle,
				this.settings.liftPidConfig);

		ArmsControl armsControl = new ArmsControl(() -> this.oi.oppInput.pidOff(), this.sensors.arms, this.motors.arms,
				this.settings.armsHardstop, this.settings.armsFullin, this.settings.armsPidConfig);

		this.teleopTasks = new Task[] {
				new ClimbTest(new MotorRamping(new SparkMax(12), false, 0.001), new AbsoluteEncoder(5, 0, false),
						this.oi.oppInput),
				// new ResetGyro(() -> this.oi.xboxInput.START(), this.sensors.gyro,
				// swerveControl),
				swerveInput, swerveControl,

				// liftControl,

				// new ToHeading(() -> this.oi.xboxInput.DPAD_UP(), 0.0, swerveControl),
				// new ToHeading(() -> this.oi.xboxInput.DPAD_RIGHT(), 90.0, swerveControl),
				// new ToHeading(() -> this.oi.xboxInput.DPAD_DOWN(), 180.0, swerveControl),
				// new ToHeading(() -> this.oi.xboxInput.DPAD_LEFT(), 270.0, swerveControl),
				// new ToHeading(() -> this.oi.xboxInput.B(), 28.75, swerveControl),
				// new ToHeading(() -> this.oi.xboxInput.Y(), 151.25, swerveControl),
				// new ToHeading(() -> this.oi.xboxInput.X(), 208.75, swerveControl),
				// new ToHeading(() -> this.oi.xboxInput.A(), 331.25, swerveControl),

				// new LiftInput(this.oi.oppInput, () -> this.oi.oppInput.lowerLift(),
				// liftControl),
				// new ToHeight(() -> this.oi.oppInput.cargo1(), liftControl,
				// this.settings.liftCargo1),
				// new ToHeight(() -> this.oi.oppInput.cargo2(), liftControl,
				// this.settings.liftCargo2),
				// new ToHeight(() -> this.oi.oppInput.cargoBall(), liftControl,
				// this.settings.liftCargoShip),
				// new ToHeight(() -> this.oi.oppInput.cargo3(), liftControl,
				// this.settings.liftCargo3),
				// new ToHeight(() -> this.oi.oppInput.hatch1(), liftControl,
				// this.settings.liftHatch1),
				// new ToHeight(() -> this.oi.oppInput.hatch2(), liftControl,
				// this.settings.liftHatch2),
				// new ToHeight(() -> this.oi.oppInput.hatch3(), liftControl,
				// this.settings.liftHatch3),
				// new ToHeight(() -> this.oi.oppInput.liftBottom(), liftControl,
				// this.settings.liftHatch0),

				// new IntakeWheel(() -> this.oi.oppInput.intakeIn(), this.motors.intake, 1),
				// new IntakeWheel(() -> this.oi.oppInput.intakeOut(), this.motors.intake, -1),

				// new Kicker(() -> this.oi.oppInput.kicker(), this.motors.kicker,
				// this.sensors.kicker,
				// this.settings.kickerUpperbound, this.settings.kickerLowerbound,
				// this.settings.kickerBackpass),

				// armsControl, new ResetArms(() -> this.oi.oppInput.armsHardstop(),
				// this.sensors.arms, armsControl),
				// new ArmsTarget(() -> this.oi.oppInput.armsRest(), this.settings.armsFullin,
				// armsControl),
				// new ArmsTarget(() -> this.oi.oppInput.armsPickUp(), this.settings.armsHatch,
				// armsControl),
				// new ArmsTarget(() -> this.oi.oppInput.armsOut(

				// ), this.settings.armsFullout, armsControl),

				// new DetatchPanel(() -> this.oi.oppInput.lowerLiftBack(), armsControl,
				// liftControl,
				// this.sensors.liftEncoder, 1.5, this.settings.armsFullin),

				// new UpdateOperatorBoard(this.oi.oppInput), new UpdateMotors(this.motors),
				// new RunEachFrameTask() {
				// @Override
				// protected void runEachFrame() {
				// empty task for telemetries
				// SmartDashboard.putNumber("gyro", sensors.gyro.getAngle());
				// SmartDashboard.putNumber("kicker encoder", sensors.kicker.getRotation());
				// SmartDashboard.putNumber("kicker motor", motors.kicker.get());
				// SmartDashboard.putNumber("arms encoder", sensors.arms.getRotation());
				// SmartDashboard.putNumber("arms motor", motors.arms.get());
				// SmartDashboard.putNumber("lift encoder", sensors.liftEncoder.getDistance());
				// SmartDashboard.putNumber("lift motor", motors.lifter.get());

				// SmartDashboard.putNumber("right front encoder",
				// sensors.rightFrontEncoder.getRotation());
				// SmartDashboard.putNumber("leftFront encoder",
				// sensors.leftFrontEncoder.getRotation());
				// SmartDashboard.putNumber("left bakc encoder",
				// sensors.leftBackEncoder.getRotation());
				// SmartDashboard.putNumber("right back encoder",
				// sensors.rightBackEncoder.getRotation());
				// for (int i = 0; i < 16; i++) {
				// SmartDashboard.putNumber("pdp" + i, pewdiepie.getCurrent(i));

				// }
		};

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

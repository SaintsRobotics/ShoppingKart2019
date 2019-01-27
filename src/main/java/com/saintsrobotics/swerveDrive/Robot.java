package com.saintsrobotics.swerveDrive;

import java.util.function.Supplier;

import com.github.dozer.TaskRobot;
import com.github.dozer.coroutine.Task;
import com.github.dozer.input.OI.XboxInput;
import com.saintsrobotics.swerveDrive.input.OI;
import com.saintsrobotics.swerveDrive.input.Sensors;
import com.saintsrobotics.swerveDrive.input.TestSensors;
import com.saintsrobotics.swerveDrive.output.RobotMotors;
import com.saintsrobotics.swerveDrive.output.SwerveWheel;
import com.saintsrobotics.swerveDrive.output.TestBotMotors;
import com.saintsrobotics.swerveDrive.tasks.teleop.DockTask;
import com.saintsrobotics.swerveDrive.tasks.teleop.SwerveControl;
import com.saintsrobotics.swerveDrive.util.Pipeline;
import com.saintsrobotics.swerveDrive.util.ResetGyro;
import com.saintsrobotics.swerveDrive.util.ToHeading;
import com.saintsrobotics.swerveDrive.util.UpdateMotors;
import com.saintsrobotics.swerveDrive.util.VisionBroker;

import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.vision.VisionThread;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Timer;
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
	private double[] rightFrontLoc = { 12, 12.75 };
	private double[] leftFrontLoc = { -12, 12.75 };
	private double[] leftBackLoc = { -12, -12.75 };
	private double[] rightBackLoc = { 12, -12.5 };
	private double[] pivotLoc = { 0, 0 };
	public SwerveControl swerveControl;
	public UsbCamera camera;
	private VisionThread visionThread;
	private VisionBroker broker;

	private double time = Timer.getFPGATimestamp();

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
		this.camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(360, 240);

		this.broker = new VisionBroker();

	}

	@Override
	public void autonomousInit() {
	}

	@Override
	public void teleopInit() {

		XboxInput c = Robot.instance.oi.xboxInput;
		RobotMotors motors = Robot.instance.motors;
		SwerveWheel rightFront = new SwerveWheel("rightFront", motors.rightFront, motors.rightFrontTurner,
				Robot.instance.sensors.rightFrontTurnConfig, this.rightFrontLoc, this.pivotLoc);
		SwerveWheel leftFront = new SwerveWheel("leftFront", motors.leftFront, motors.leftFrontTurner,
				Robot.instance.sensors.leftFrontTurnConfig, this.leftFrontLoc, this.pivotLoc);
		SwerveWheel leftBack = new SwerveWheel("leftBack", motors.leftBack, motors.leftBackTurner,
				Robot.instance.sensors.leftBackTurnConfig, this.leftBackLoc, this.pivotLoc);
		SwerveWheel rightBack = new SwerveWheel("rightBack", motors.rightBack, motors.rightBackTurner,
				Robot.instance.sensors.rightBackTurnConfig, this.rightBackLoc, this.pivotLoc);
		SwerveWheel[] wheels = {rightFront, leftFront, leftBack, rightBack};
		swerveControl = new SwerveControl(c, wheels, Robot.instance.sensors.gyro);

		visionThread = new VisionThread(camera, new Pipeline(), pipeline -> {
			
			double now = Timer.getFPGATimestamp();
			SmartDashboard.putNumber("vision thread time", now-time);
			if (pipeline.filterContoursOutput().size() == 2) {
				this.broker.setRects(Imgproc.boundingRect(pipeline.filterContoursOutput().get(0)),
						Imgproc.boundingRect(pipeline.filterContoursOutput().get(1)));
				System.out.println("2 rect");
			} 
			else if (pipeline.filterContoursOutput().size() == 1) {
				this.broker.setRects(Imgproc.boundingRect(pipeline.filterContoursOutput().get(0)), null);
				System.out.println("1 rect");
			} 
			else {
				this.broker.setRects(null, null);
				System.out.println("0 rect");
			}
			time = now;
		});

		visionThread.start();

		this.teleopTasks = new Task[] { new ResetGyro(), swerveControl,

				new ToHeading(() -> c.DPAD_UP(), 0.0), new ToHeading(() -> c.DPAD_RIGHT(), 90.0),
				new ToHeading(() -> c.DPAD_DOWN(), 180.0), new ToHeading(() -> c.DPAD_LEFT(), 270.0),

				new DockTask(this.broker, this.swerveControl),
				// new SimpleLiftTask(), new IntakeWheel(), new OuttakeWheel(),
				new UpdateMotors(this.motors) };

		super.teleopInit();
	}

	@Override
	public void disabledInit() {
		super.disabledInit();
	}
}
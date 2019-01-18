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
import com.saintsrobotics.swerveDrive.tasks.teleop.SwerveControl;
import com.saintsrobotics.swerveDrive.util.UpdateMotors;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the IterativeRobot documentation. If you change the name of this class
 * or the package after creating this project, you must also update the manifest file in the
 * resource directory.
 */
public class Robot extends TaskRobot {
	
    private SendableChooser<Supplier<Task>> taskChooser;


  public RobotMotors motors;
  public Sensors sensors;
  public OI oi;
  public Flags flags;
  private double[] rightFrontLoc = {12, 12.75};
  private double[] leftFrontLoc = {-12, 12.75};
  private double[] leftBackLoc = {-12, -12.75};
  private double[] rightBackLoc = {12, -12.5};
  private double[] pivotLoc = {0, 0};

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
    //this.temp = new SpeedController[8];
    //for(int i = 1; i < 9; i++) this.temp[i-1] = new Talon(i);
    this.flags = new Flags();

    this.flags.pdp = new PowerDistributionPanel();
    
    }

  @Override
  public void autonomousInit() {
      }

  @Override     
  public void teleopInit() {
    
    //tube1.setDirection(Relay.Direction.kReverse);
    XboxInput c = Robot.instance.oi.xboxInput;
    RobotMotors motors = Robot.instance.motors;
    SwerveWheel rightFront = new SwerveWheel("rightFront", motors.rightFront, motors.rightFrontTurner, Robot.instance.sensors.rightFrontTurnConfig, this.rightFrontLoc, this.pivotLoc);
    SwerveWheel leftFront = new SwerveWheel("leftFront", motors.leftFront, motors.leftFrontTurner, Robot.instance.sensors.leftFrontTurnConfig, this.leftFrontLoc, this.pivotLoc);
    SwerveWheel leftBack = new SwerveWheel("leftBack", motors.leftBack, motors.leftBackTurner, Robot.instance.sensors.leftBackTurnConfig, this.leftBackLoc, this.pivotLoc);
    SwerveWheel rightBack = new SwerveWheel("rightBack", motors.rightBack, motors.rightBackTurner, Robot.instance.sensors.rightBackTurnConfig, this.rightBackLoc, this.pivotLoc);
    
    this.teleopTasks = new Task[] {
        leftBack, leftFront, rightBack, rightFront,
        new SwerveControl(c, rightFront, leftFront, leftBack, rightBack),
//        new TestTurnSwerveWheel(c, motors.leftBackTurner, this.sensors.leftBackEncoder),
//        new TestDriveSwerveWheel(c, motors.leftBack)
         new UpdateMotors(this.motors)
    };
    
    super.teleopInit();
  }
  
  @Override
  public void disabledInit() {
    this.disabledTasks = new Task[] {};
    super.disabledInit();
  }
}
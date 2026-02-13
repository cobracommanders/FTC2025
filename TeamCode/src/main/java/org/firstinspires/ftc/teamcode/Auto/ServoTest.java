package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Common.HardwareConfig;
import org.firstinspires.ftc.teamcode.Common.Intake;
import org.firstinspires.ftc.teamcode.Common.MechanumDrive;
import org.firstinspires.ftc.teamcode.Common.Shooter;

@Autonomous(name = "Servo Test", group = "Robot")
public class ServoTest extends LinearOpMode {
   // private MechanumDrive drivetrain;
   // private Shooter shooter;
  //  private Intake intake;
    private Servo servo;
    @Override
    public void runOpMode() throws InterruptedException {
       // this.drivetrain = new MechanumDrive(hardwareMap);
      //  this.shooter = new Shooter(hardwareMap);
      //  this.intake = new Intake(hardwareMap);
        this.servo = hardwareMap.get(Servo.class, HardwareConfig.FEED_SERVO);
        // wait for the Auto play button to be pressed
        waitForStart();
        this.servo.setPosition(0);
        resetRuntime();
        while (opModeIsActive()) {
            this.servo.setPosition(1);
            sleep(2000);
            this.servo.setPosition(-1);
            sleep(2000);
            telemetry.addData("Status", "Running");
            telemetry.update();
        }
        this.servo.setPosition(0);
    }
}
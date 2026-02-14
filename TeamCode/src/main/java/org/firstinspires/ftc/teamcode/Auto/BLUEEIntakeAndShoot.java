package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Common.Intake;
import org.firstinspires.ftc.teamcode.Common.MechanumDrive;
import org.firstinspires.ftc.teamcode.Common.Shooter;
@Autonomous(name = "Auto: Intake and shoot, but BLUE", group = "Robot")
public class BLUEEIntakeAndShoot extends LinearOpMode {
    private MechanumDrive drivetrain;

    private Shooter shooter;

    private Intake intake;
    public void waitForShooterReady() {
        while (opModeIsActive() && !this.shooter.isReady()) {
            sleep(50);
            this.telemetry.addData("auto_shoot_enabled:", this.shooter.enableAutoShoot);
            this.telemetry.addData("left_velocity:", this.shooter.getLeftVelocity());
            this.telemetry.addData("right_velocity:", this.shooter.getRightVelocity());
            this.telemetry.addData("average_velocity:", this.shooter.getVelocity());
            this.telemetry.addData("target_velocity:", this.shooter.getTargetVelocity());
            this.telemetry.addData("shooter_ready:", this.shooter.isReady());
            this.telemetry.update();
        }
    }
@Override
public void runOpMode() throws InterruptedException {
    this.drivetrain = new MechanumDrive(hardwareMap);
    this.shooter = new Shooter(hardwareMap);
    this.intake = new Intake(hardwareMap);

    // wait for the Auto play button to be pressed
    waitForStart();

    resetRuntime();  // at Auto start, reset the stopwatch

    this.shooter.servoClose();

    this.drivetrain.stop();
    this.drivetrain.turnToHeadingPIDF(-45, 0.4, 0.1, 0, .7, 5); //Turns to face goal.
    this.drivetrain.stop();

    this.drivetrain.driveToPosition(-6, 0.5); // Drives to the front of the goal.
    while(opModeIsActive() && this.drivetrain.isBusy()){
        telemetry.addData("Status", "Moving Backward 6");
        telemetry.update();
    }
    this.drivetrain.stop();


    this.shooter.setVelocity(2075);
    this.intake.drive(0.3); // Gets ready to shoot.
    sleep(300);
    this.intake.stop();

    waitForShooterReady();
////        this.shooter.feedReverse();

    this.shooter.servoOpen();
    sleep(500);
    this.intake.drive(0.6); // Shoots ball.
    sleep(200);
    this.shooter.servoClose();
    this.intake.stop();



    this.shooter.servoOpen();
    sleep(500);
    this.intake.drive(0.6); // Shoots another ball.
    sleep(200);
    this.shooter.servoClose();
    this.intake.stop();



    this.shooter.servoOpen();
    sleep(500);
    this.intake.drive(0.6); // Shoots yet another ball.
    sleep(200);
    this.shooter.servoClose();
    this.intake.stop();

    this.shooter.stop();

    this.drivetrain.driveToPosition(6, 0.5);
    while(opModeIsActive() && this.drivetrain.isBusy()){ // Drives away from the goal 6 inches.
        telemetry.addData("Status", "Moving Forward 6");
        telemetry.update();
    }
    this.drivetrain.stop();
    this.drivetrain.turnToHeadingPIDF(45, 0.4, 0.1, 0, .7, 5);
    this.drivetrain.stop();

    resetRuntime();
    while (opModeIsActive() && getRuntime() < 3.0) {
        this.drivetrain.drive(0, 0.35, 0, 1.0);
    }
    this.drivetrain.stop();
// If you want to make it rotate, you would replace "driveToPosition" to "turn", and if you want to change it to strafe, you would replace it with "strafe[direction]
    this.drivetrain.driveToPosition(-23, 0.6);
    while(opModeIsActive() && this.drivetrain.isBusy()){
        telemetry.addData("Status", "Moving Forward");
        telemetry.update();
    }
    this.drivetrain.stop();
    // Stop everything when auto ends
    this.drivetrain.stop();
    this.intake.stop();
    this.shooter.stop();
//    this.shooter.feedStop();
}
}
// backwards towards the driver 23 inches, 48 inches left, then turn 180 degrees, 34 inches forward intake same time, go back 34 inches, turn 180 degrees, 48 inches right, forward 23 inches, shoot, backwards 23 inches, left 80 1/2 inches, forwards 34 inches while intaking, backwards 34 inches, right 80 1/2 inches, forwards 23 inches. shoot, backwards 23 inches, left 119 inches, forward 34 inches while intaking, backwards 34 inches, right 119 inches, forward 23 inches, shoot, backwards, stop auto. */
// 28 ticks per revolution on REV Robotics HD Hex motor
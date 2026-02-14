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

    while (opModeIsActive() && getRuntime() < 1.0) {  // turn for 1.5 seconds at 30% speed in CCW direction
        this.drivetrain.drive(0, 0, 0.5, 1.0);
    }
    this.drivetrain.stop();
    resetRuntime();
    while (opModeIsActive() && getRuntime() < 1.0) {
        this.drivetrain.drive(0, -0.35, 0, 1.0);
    }
    this.drivetrain.stop();

    
    this.intake.drive(0.3);
    this.shooter.setVelocity(2075);

    waitForShooterReady();

    sleep(50);
    this.intake.drive(0.6);
    sleep(200);
    this.intake.stop();
    sleep(300);
    


    sleep(1000);
    waitForShooterReady();
//    this.shooter.feedReverse();
    sleep(50);
    this.intake.drive(0.6);
    sleep(400);
    this.intake.stop();
    sleep(300);
    


    sleep(1000);
    waitForShooterReady();
//    this.shooter.feedReverse();
    sleep(50);
    this.intake.drive(1.0);
    sleep(250);
    this.intake.drive(0.6);
    sleep(2000);
    this.intake.stop();
    sleep(300);
    

    this.shooter.stop();

    

    resetRuntime();
    while (opModeIsActive() && getRuntime() < 1.0) {
        this.drivetrain.drive(0, 0.35, 0, 1.0);
    }
    this.drivetrain.stop();

    resetRuntime();

    while (opModeIsActive() && getRuntime() < 1.0) {  // turn for 1.5 seconds at 30% speed in CCW direction
        this.drivetrain.drive(0, 0, -0.5, 1.0);
    }
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
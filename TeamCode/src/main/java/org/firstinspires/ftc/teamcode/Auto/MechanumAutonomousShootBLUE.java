package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Common.Intake;
import org.firstinspires.ftc.teamcode.Common.MechanumDrive;
import org.firstinspires.ftc.teamcode.Common.Shooter;

@Autonomous(name = "Auto: BLUE Drive backwards and shoot", group = "Robot")
public class MechanumAutonomousShootBLUE extends LinearOpMode {
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

        waitForStart();
        resetRuntime(); // at Auto start, reset the stopwatch
        this.shooter.servoClose();

        this.drivetrain.stop();
        this.drivetrain.turnToHeadingPIDF(-45, 0.4, 0.1, 0, .7, 5); //Turns to face goal.
        this.drivetrain.stop();

        this.drivetrain.driveToPosition(-6, 0.5); // Drives to the front of the goal.
        while(opModeIsActive() && this.drivetrain.isBusy()){
            telemetry.addData("Status", "Moving Backward 6");
            telemetry.update();
        }
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

        this.drivetrain.driveToPosition(15, 0.5);
        while(opModeIsActive() && this.drivetrain.isBusy()){ // Drives off the line.
            telemetry.addData("Status", "Moving Forward 15");
            telemetry.update();
        }

            this.drivetrain.stop();
            this.intake.stop(); // Stops everything.
            this.shooter.stop();
    }
}

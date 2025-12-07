package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Common.MechanumDrive;
import org.firstinspires.ftc.teamcode.Common.Shooter;

@Autonomous(name = "Auto: Generic Drive Forwards", group = "Robot")
public class MechanumAutonomousDriveForwards extends LinearOpMode {
    private MechanumDrive drivetrain;
    private Shooter shooter;

    @Override
    public void runOpMode() throws InterruptedException {
        this.drivetrain = new MechanumDrive(hardwareMap);
        this.shooter = new Shooter(hardwareMap);

        // wait for the Auto play button to be pressed
        waitForStart();

        resetRuntime();  // at Auto start, reset the stopwatch

        // while Auto isn't stopped and the stopwatch is less that 1.0, drive backwards
        this.shooter.feedIdle();
        while (opModeIsActive() && getRuntime() < 3.0) {
            this.drivetrain.drive(0, -0.35, 0, 1.0);
        }
        this.drivetrain.stop();
    }
}

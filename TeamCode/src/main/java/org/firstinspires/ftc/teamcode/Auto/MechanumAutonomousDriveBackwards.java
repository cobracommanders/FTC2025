package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Common.MechanumDrive;

@Autonomous(name = "Auto: Generic Drive Backwards", group = "Robot")
public class MechanumAutonomousDriveBackwards extends LinearOpMode {
    private MechanumDrive drivetrain;

    @Override
    public void runOpMode() throws InterruptedException {
        this.drivetrain = new MechanumDrive(hardwareMap);

        // wait for the Auto play button to be pressed
        waitForStart();

        resetRuntime();  // at Auto start, reset the stopwatch

        // while Auto isn't stopped and the stopwatch is less that 1.0, drive backwards
        while (opModeIsActive() && getRuntime() < 2.0) {
            this.drivetrain.drive(0, -0.25, 0, 1.0);
        }
        this.drivetrain.stop();
    }
}

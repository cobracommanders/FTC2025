package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Common.MechanumDrive;
import org.firstinspires.ftc.teamcode.Common.Shooter;

@Autonomous(name = "Auto: Generic Drive Backwards", group = "Robot")
public class MechanumAutonomousDriveBackwards extends LinearOpMode {
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
        
        while (opModeIsActive() && getRuntime() < 3.0) {
            this.drivetrain.drive(0, 0.35, 0, 1.0);
        }
        this.drivetrain.stop();
    }
}
    // backwards towards the driver 23 inches, 48 inches left, then turn 180 degrees, 34 inches forward intake same time, go back 34 inches, turn 180 degrees, 48 inches right, forward 23 inches, shoot, backwards 23 inches, left 80 1/2 inches, forwards 34 inches while intaking, backwards 34 inches, right 80 1/2 inches, forwards 23 inches. shoot, backwards 23 inches, left 119 inches, forward 34 inches while intaking, backwards 34 inches, right 119 inches, forward 23 inches, shoot, backwards, stop auto.
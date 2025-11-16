package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Common.Intake;
import org.firstinspires.ftc.teamcode.Common.MechanumDrive;
import org.firstinspires.ftc.teamcode.Common.Shooter;

@Autonomous(name = "Auto: Drive backwards and shoot", group = "Robot")
public class MechanumAutonomousShoot extends LinearOpMode {
    private MechanumDrive drivetrain;
    private Shooter shooter;
    private Intake intake;

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

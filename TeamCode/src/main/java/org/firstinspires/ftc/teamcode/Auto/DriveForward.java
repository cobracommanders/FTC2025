package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Common.Intake;
import org.firstinspires.ftc.teamcode.Common.MechanumDrive;
import org.firstinspires.ftc.teamcode.Common.Shooter;

@Autonomous(name = "Drive Test", group = "Robot")
public class DriveForward extends LinearOpMode{
    private MechanumDrive drivetrain;



    @Override
    public void runOpMode() throws InterruptedException {
        this.drivetrain = new MechanumDrive(hardwareMap);


        // wait for the Auto play button to be pressed
        waitForStart();

        resetRuntime();  // at Auto start, reset the stopwatch

        this.drivetrain.driveToPosition(24+5, 0.5);
        while(opModeIsActive() && this.drivetrain.isBusy()){ // Nuh uh, trust me.
            telemetry.addData("Status", "Moving Forward 24");
            telemetry.update();
        }

        this.drivetrain.stop();
this.drivetrain.turnToHeadingPIDF(90, 0.4, 0.1, 0, .7, 5);
this.drivetrain.stop();















    }
}

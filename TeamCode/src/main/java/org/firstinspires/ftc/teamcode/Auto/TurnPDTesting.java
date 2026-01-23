package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Common.Intake;
import org.firstinspires.ftc.teamcode.Common.MechanumDrive;
import org.firstinspires.ftc.teamcode.Common.Shooter;

@Autonomous(name = "TurnPDTesting", group = "Robot")
public class TurnPDTesting extends LinearOpMode {
    private MechanumDrive drivetrain;
    private Shooter shooter;
    private Intake intake;


    @Override
    public void runOpMode() throws InterruptedException {
        this.drivetrain = new MechanumDrive(hardwareMap);
        this.shooter = new Shooter(hardwareMap);
        this.intake = new Intake(hardwareMap);

        // wait for the Auto play button to be pressed
        waitForStart();

        resetRuntime();  // at Auto start, reset the stopwatch

        while(opModeIsActive()) {
            drivetrain.turnToHeadingPID(90, 0.7, 5);
        }

        drivetrain.stop();
        shooter.stop();
        intake.stop();
    }
}

package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Common.AutoShoot;
import org.firstinspires.ftc.teamcode.Common.Intake;
import org.firstinspires.ftc.teamcode.Common.MechanumDrive;
import org.firstinspires.ftc.teamcode.Common.Shooter;

@Autonomous(name = "Test: Autonomous Firing", group = "Robot")
public class AutoShootTest extends LinearOpMode {

    private MechanumDrive drivetrain;

    private Shooter shooter;
    private Intake intake;
    private AutoShoot autoShooter;

    @Override
    public void runOpMode() throws InterruptedException {
        this.drivetrain = new MechanumDrive(hardwareMap);
        this.shooter = new Shooter(hardwareMap);
        this.intake = new Intake(hardwareMap);
        this.autoShooter = new AutoShoot(this, this.intake, this.shooter, Shooter.SPEED_MEDIUM_HIGH);

        waitForStart();
        resetRuntime();  // at Auto start, reset the stopwatch

        this.autoShooter.run(3);

    }
}

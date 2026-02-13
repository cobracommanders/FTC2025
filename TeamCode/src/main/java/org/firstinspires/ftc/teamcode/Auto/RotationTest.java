package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Common.Intake;
import org.firstinspires.ftc.teamcode.Common.MechanumDrive;
import org.firstinspires.ftc.teamcode.Common.Shooter;


    @Autonomous(name = "Auto: Rotation Test", group = "Robot")
    public class RotationTest extends LinearOpMode {
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

            resetRuntime();

            this.drivetrain.stop();
            this.drivetrain.turnToHeadingProportional(90, .5);
            while (opModeIsActive() && this.drivetrain.isBusy()) {
                telemetry.addData("Status", "Rotating");
                telemetry.update();
            }
        }
    }

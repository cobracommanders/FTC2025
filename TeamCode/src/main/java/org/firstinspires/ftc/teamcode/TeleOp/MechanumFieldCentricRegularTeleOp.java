package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Common.Intake;
import org.firstinspires.ftc.teamcode.Common.MechanumDrive;
import org.firstinspires.ftc.teamcode.Common.Shooter;

@TeleOp
public class MechanumFieldCentricRegularTeleOp extends LinearOpMode {

    private MechanumDrive drivetrain;

    private Shooter shooter;
    private Intake intake;



    @Override
    public void runOpMode() throws InterruptedException {
        this.drivetrain = new MechanumDrive(hardwareMap);
        this.shooter = new Shooter(hardwareMap);
        this.intake = new Intake(hardwareMap);

        this.drivetrain.resetHeadingIfNeeded();

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {

            // Get joystick values to control mechanum drivetrain
            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;
            double leftTrigger = gamepad1.left_trigger;
            this.drivetrain.drive(-x, -y, rx, leftTrigger);

            // Reset robot field orientation
            // The equivalent button is start on Xbox-style controllers
            if (gamepad1.options) {
                this.drivetrain.resetHeading();
            }

            // Button control shooter speed
            if (gamepad2.dpad_left) {
                this.shooter.setVelocity(2500);
            }
            if (gamepad2.y) {
                this.shooter.setVelocity(Shooter.SPEED_HIGH);
            } else if (gamepad2.b) {
                this.shooter.setVelocity(Shooter.SPEED_MEDIUM_HIGH);
            } else if (gamepad2.a) {
                this.shooter.setVelocity(Shooter.SPEED_MEDIUM_LOW);
            } else if (gamepad2.x) {
                this.shooter.setVelocity(Shooter.SPEED_LOW);
            } else {
                this.shooter.stop();
            }

            // Indexer button controls
            if (gamepad1.left_bumper || gamepad2.dpad_up) {
                intake.intake();
            } else if (gamepad1.right_bumper || gamepad2.dpad_down) {
                intake.reverse();
            } else {
                intake.stop();
            }

            // Below code is to control the roller before the shooter which we will call feedWheel
            // We've added an experimental mode to automatically shoot while holding the feed button
            if (gamepad2.right_bumper) {
                this.shooter.feedReverse();
            } else if (this.shooter.enableAutoShoot) {
                if (gamepad2.left_bumper && this.shooter.isReady()) {
                    this.shooter.feed();
                } else {
                    this.shooter.feedIdle();
                }
            } else {
                 if (gamepad2.left_bumper) {
                    this.shooter.feed();
                } else {
                    this.shooter.feedIdle();
                }
            }

            this.telemetry.addData("auto_shoot_enabled:", this.shooter.enableAutoShoot);
            this.telemetry.addData("left_velocity:", this.shooter.getLeftVelocity());
            this.telemetry.addData("right_velocity:", this.shooter.getRightVelocity());
            this.telemetry.addData("average_velocity:", this.shooter.getVelocity());
            this.telemetry.addData("target_velocity:", this.shooter.getTargetVelocity());
            this.telemetry.addData("shooter_ready:", this.shooter.isReady());
            this.telemetry.update();

        }
    }
}
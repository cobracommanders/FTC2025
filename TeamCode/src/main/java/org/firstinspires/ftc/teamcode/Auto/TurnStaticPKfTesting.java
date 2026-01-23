package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Common.Intake;
import org.firstinspires.ftc.teamcode.Common.MechanumDrive;
import org.firstinspires.ftc.teamcode.Common.Shooter;

@TeleOp(name = "TurnStaticPKfTesting", group = "Robot")
public class TurnStaticPKfTesting extends LinearOpMode {
    private MechanumDrive drivetrain;

    // PID tuning parameters
    private double kP = 0.5;
    private double kD = 0.1;
    private double maxPower = 0.0;
    private double targetHeading = 90.0;

    // Adjustment step sizes
    private static final double KP_STEP = 0.05;
    private static final double KD_STEP = 0.02;
    private static final double POWER_STEP = 0.01;
    private static final double HEADING_STEP = 15.0;

    // Button debouncing
    private boolean lastDpadUp = false;
    private boolean lastDpadDown = false;
    private boolean lastDpadLeft = false;
    private boolean lastDpadRight = false;
    private boolean lastLeftBumper = false;
    private boolean lastRightBumper = false;
    private boolean lastX = false;
    private boolean lastY = false;

    @Override
    public void runOpMode() throws InterruptedException {
        drivetrain = new MechanumDrive(hardwareMap);
        drivetrain.resetHeadingIfNeeded();

        telemetry.addLine("PID Tuning TeleOp");
        telemetry.addLine("D-Pad Up/Down: Adjust kP");
        telemetry.addLine("D-Pad Left/Right: Adjust kD");
        telemetry.addLine("Bumpers: Adjust Max Power");
        telemetry.addLine("X/Y: Adjust Target Heading");
        telemetry.addLine("A: Execute Turn");
        telemetry.addLine("Options: Reset IMU");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {

//            // Adjust kP with D-pad Up/Down
//            if (gamepad1.dpad_up && !lastDpadUp) {
//                kP += KP_STEP;
//            }
//            if (gamepad1.dpad_down && !lastDpadDown) {
//                kP = Math.max(0, kP - KP_STEP);
//            }

//            // Adjust kD with D-pad Left/Right
//            if (gamepad1.dpad_right && !lastDpadRight) {
//                kD += KD_STEP;
//            }
//            if (gamepad1.dpad_left && !lastDpadLeft) {
//                kD = Math.max(0, kD - KD_STEP);
//            }

            // Adjust max power with bumpers
            if (gamepad1.right_bumper && !lastRightBumper) {
                maxPower = Math.min(1.0, maxPower + POWER_STEP);
            }
            if (gamepad1.left_bumper && !lastLeftBumper) {
                maxPower = Math.max(0.0, maxPower - POWER_STEP);
            }

//            // Adjust target heading with X/Y
//            if (gamepad1.y && !lastY) {
//                targetHeading += HEADING_STEP;
//                if (targetHeading > 180) targetHeading -= 360;
//            }
//            if (gamepad1.x && !lastX) {
//                targetHeading -= HEADING_STEP;
//                if (targetHeading < -180) targetHeading += 360;
//            }
//
//            // Execute turn when A is pressed
//            if (gamepad1.a) {
//                telemetry.addLine("EXECUTING TURN...");
//                telemetry.update();
//                drivetrain.turnToHeadingPID(targetHeading, kP, kD, maxPower, 5.0);
//            }
//
//            // Reset IMU
//            if (gamepad1.options) {
//                drivetrain.resetHeading();
//                telemetry.addLine("IMU RESET");
//            }

            // Update button states
            lastDpadUp = gamepad1.dpad_up;
            lastDpadDown = gamepad1.dpad_down;
            lastDpadLeft = gamepad1.dpad_left;
            lastDpadRight = gamepad1.dpad_right;
            lastLeftBumper = gamepad1.left_bumper;
            lastRightBumper = gamepad1.right_bumper;
            lastX = gamepad1.x;
            lastY = gamepad1.y;

            this.drivetrain.drive(0, 0, maxPower, 0.0);

            // Display current parameters
            telemetry.addData("kP", "%.3f", kP);
            telemetry.addData("kD", "%.3f", kD);
            telemetry.addData("Max Power", "%.2f", maxPower);
            telemetry.addData("Target Heading", "%.1f°", targetHeading);
            telemetry.addData("Current Heading", "%.1f°", Math.toDegrees(drivetrain.getHeading()));
            telemetry.addLine();
            telemetry.addLine("Controls:");
            telemetry.addLine("A = Turn | D-Pad = kP/kD | Bumpers = Power");
            telemetry.update();
        }
    }
}

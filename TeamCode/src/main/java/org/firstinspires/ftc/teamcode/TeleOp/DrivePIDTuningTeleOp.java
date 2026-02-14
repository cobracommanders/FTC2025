package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Common.MechanumDrive;

@TeleOp(name = "Drive PID Tuning")
public class DrivePIDTuningTeleOp extends LinearOpMode {

    private MechanumDrive drivetrain;

    // PID tuning parameters
    private double kP = 0.02;
    private double kD = 0.0;
    private double kI = 0.0;
    private double maxPower = 0.5;
    private double targetDistance = 24.0;  // inches

    // Adjustment step sizes
    private static final double KP_STEP = 0.005;
    private static final double KD_STEP = 0.002;
    private static final double KI_STEP = 0.0005;
    private static final double POWER_STEP = 0.05;
    private static final double DISTANCE_STEP = 6.0;

    // Button debouncing
    private boolean lastDpadUp = false;
    private boolean lastDpadDown = false;
    private boolean lastDpadLeft = false;
    private boolean lastDpadRight = false;
    private boolean lastLeftBumper = false;
    private boolean lastRightBumper = false;
    private boolean lastX = false;
    private boolean lastY = false;
    private boolean lastA = false;
    private boolean lastB = false;

    @Override
    public void runOpMode() throws InterruptedException {
        drivetrain = new MechanumDrive(hardwareMap);
        drivetrain.resetHeading();

        telemetry.addLine("Drive PID Tuning TeleOp");
        telemetry.addLine("D-Pad Up/Down: Adjust kP");
        telemetry.addLine("D-Pad Left/Right: Adjust kD");
        telemetry.addLine("Bumpers: Adjust Max Power");
        telemetry.addLine("X/Y: Adjust Target Distance");
        telemetry.addLine("A: Drive Forward | B: Drive Backward");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {

            // Adjust kP with D-pad Up/Down
            if (gamepad1.dpad_up && !lastDpadUp) {
                kP += KP_STEP;
            }
            if (gamepad1.dpad_down && !lastDpadDown) {
                kP = Math.max(0, kP - KP_STEP);
            }

            // Adjust kD with D-pad Left/Right
            if (gamepad1.dpad_right && !lastDpadRight) {
                kD += KD_STEP;
            }
            if (gamepad1.dpad_left && !lastDpadLeft) {
                kD = Math.max(0, kD - KD_STEP);
            }

            // Adjust kI with gamepad2 A/B
            if (gamepad2.a && !lastA) {
                kI = Math.max(0, kI - KI_STEP);
            }
            if (gamepad2.b && !lastB) {
                kI += KI_STEP;
            }

            // Adjust max power with bumpers
            if (gamepad1.right_bumper && !lastRightBumper) {
                maxPower = Math.min(1.0, maxPower + POWER_STEP);
            }
            if (gamepad1.left_bumper && !lastLeftBumper) {
                maxPower = Math.max(0.1, maxPower - POWER_STEP);
            }

            // Adjust target distance with X/Y
            if (gamepad1.y && !lastY) {
                targetDistance += DISTANCE_STEP;
            }
            if (gamepad1.x && !lastX) {
                targetDistance = Math.max(DISTANCE_STEP, targetDistance - DISTANCE_STEP);
            }

            // Execute drive when A is pressed (forward)
            if (gamepad1.a) {
                telemetry.addLine("DRIVING FORWARD...");
                telemetry.update();
                drivetrain.driveStraightPIDF(targetDistance, kP, kD, kI, maxPower, 10.0);
            }

            // Execute drive when B is pressed (backward)
            if (gamepad1.b) {
                telemetry.addLine("DRIVING BACKWARD...");
                telemetry.update();
                drivetrain.driveStraightPIDF(-targetDistance, kP, kD, kI, maxPower, 10.0);
            }

            // Reset IMU
            if (gamepad1.options) {
                drivetrain.resetHeading();
                telemetry.addLine("IMU RESET");
            }

            // Update button states
            lastDpadUp = gamepad1.dpad_up;
            lastDpadDown = gamepad1.dpad_down;
            lastDpadLeft = gamepad1.dpad_left;
            lastDpadRight = gamepad1.dpad_right;
            lastLeftBumper = gamepad1.left_bumper;
            lastRightBumper = gamepad1.right_bumper;
            lastX = gamepad1.x;
            lastY = gamepad1.y;
            lastA = gamepad2.a;
            lastB = gamepad2.b;

            // Display current parameters
            telemetry.addData("kP", "%.4f", kP);
            telemetry.addData("kD", "%.4f", kD);
            telemetry.addData("kI", "%.5f", kI);
            telemetry.addData("Max Power", "%.2f", maxPower);
            telemetry.addData("Target Distance", "%.1f in", targetDistance);
            telemetry.addData("Current Heading", "%.1f°", Math.toDegrees(drivetrain.getHeading()));
            telemetry.addLine();
            telemetry.addLine("Controls:");
            telemetry.addLine("A=Fwd | B=Back | D-Pad=kP/kD | Bumpers=Power");
            telemetry.addLine("X/Y=Distance | kI=G2 A/B | Options=Reset");
            telemetry.update();
        }
    }
}

package org.firstinspires.ftc.teamcode.Common;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class MechanumDrive {

    // Slow mode reduces speed to 35% for precise positioning and scoring
    public static final double SLOW_MODE_MULTIPLIER = 0.35;

    // Static flag to persist heading across OpMode transitions (Auto -> TeleOp)
    // Prevents IMU reset when switching from autonomous to driver control
    private static boolean headingInitialized = false;

    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private IMU imu;

    public MechanumDrive(HardwareMap hardwareMap) {
        this.frontLeft = hardwareMap.dcMotor.get(HardwareConfig.FRONT_LEFT_DRIVE_MOTOR);
        this.backLeft = hardwareMap.dcMotor.get(HardwareConfig.BACK_LEFT_DRIVE_MOTOR);
        this.frontRight = hardwareMap.dcMotor.get(HardwareConfig.FRONT_RIGHT_DRIVE_MOTOR);
        this.backRight = hardwareMap.dcMotor.get(HardwareConfig.BACK_RIGHT_DRIVE_MOTOR);
        this.imu = hardwareMap.get(IMU.class, HardwareConfig.IMU);

        this.initializeDrivetrain();
        this.initializeImu();

        this.resetHeadingIfNeeded();  // zero the imu to the location of the robot when initialized after restart
    }

    private void initializeDrivetrain() {
        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        this.frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        this.backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        this.frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        this.backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        this.frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void initializeImu() {
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD));
        this.imu.initialize(parameters);
    }

    public void resetHeading() {
        this.imu.resetYaw();
        headingInitialized = true;
    }

    public void resetHeadingIfNeeded() {
        if (!headingInitialized) {
            resetHeading();
        }
    }

    public double getHeading() {
        return this.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
    }

    public void drive(double x, double y, double rx, double scaler) {
        // prevent scalar from being set to bad value
        scaler = Math.max(0, Math.min(1.0, scaler));
        double speedMultiplier = (scaler > 0.5) ? SLOW_MODE_MULTIPLIER : 1.0;

        double botHeading = this.getHeading();

        // Rotate the movement direction counter to the bot's rotation
        double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
        double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

        rotX = rotX * 1.1;  // Counteract imperfect strafing

        // Apply speed multiplier to inputs (more efficient than applying to outputs directly)
        rotX *= speedMultiplier;
        rotY *= speedMultiplier;
        rx *= speedMultiplier;

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio,
        // but only if at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
        double frontLeftPower = (rotY + rotX + rx) / denominator;
        double backLeftPower = (rotY - rotX + rx) / denominator;
        double frontRightPower = (rotY - rotX - rx) / denominator;
        double backRightPower = (rotY + rotX - rx) / denominator;

        this.frontLeft.setPower(frontLeftPower);
        this.backLeft.setPower(backLeftPower);
        this.frontRight.setPower(frontRightPower);
        this.backRight.setPower(backRightPower);

    }

    public void stop() {
        this.frontLeft.setPower(0);
        this.backLeft.setPower(0);
        this.frontRight.setPower(0);
        this.backRight.setPower(0);
    }

    public void turnToHeading(double targetHeadingDegrees, double power) {
        double targetRadians = Math.toRadians(targetHeadingDegrees);
        double currentHeading = getHeading();
        double error = targetRadians - currentHeading;

        // Normalize error to [-PI, PI]
        while (error > Math.PI) error -= 2 * Math.PI;
        while (error < -Math.PI) error += 2 * Math.PI;

        // Turn until close enough (within ~2 degrees)
        while (Math.abs(error) > Math.toRadians(2)) {
            // Determine turn direction
            double turnPower = error > 0 ? power : -power;

            this.drive(0, 0, turnPower, 1.0);

            // Update error
            currentHeading = getHeading();
            error = targetRadians - currentHeading;

            // Normalize error
            while (error > Math.PI) error -= 2 * Math.PI;
            while (error < -Math.PI) error += 2 * Math.PI;
        }

        this.stop();
    }

    public void turnToHeadingProportional(double targetHeadingDegrees, double maxPower) {
        double targetRadians = Math.toRadians(targetHeadingDegrees);
        double currentHeading = getHeading();
        double error = targetRadians - currentHeading;

        // Normalize error to [-PI, PI]
        while (error > Math.PI) error -= 2 * Math.PI;
        while (error < -Math.PI) error += 2 * Math.PI;

        while (Math.abs(error) > Math.toRadians(2)) {
            // Proportional control: power scales with error
            double turnPower = error * 0.5;  // kP = 0.5
            turnPower = Math.max(-maxPower, Math.min(maxPower, turnPower));  // Clamp to max power

            this.drive(0, 0, turnPower, 1.0);

            currentHeading = getHeading();
            error = targetRadians - currentHeading;

            while (error > Math.PI) error -= 2 * Math.PI;
            while (error < -Math.PI) error += 2 * Math.PI;
        }

        this.stop();
    }
}

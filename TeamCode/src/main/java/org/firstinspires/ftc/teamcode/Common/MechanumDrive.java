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
    public static final int TPR = 28;
    public static final double WHEEL_DIAMETER = 8.6; // Centimeters
    public static final double WHEEL_CIRCUMFERENCE = WHEEL_DIAMETER * Math.PI;
    public static final double DEGREES_TO_INCHES = 15 * Math.PI / 360;
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

        //this.frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //this.backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //this.frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //this.backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //this.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //this.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
       // this.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      //  this.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        this.frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void initializeImu() {
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
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
        this.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        // prevent scaler from being set to bad value
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

        while (Math.abs(error) > Math.toRadians(4)) {
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
    public void setMode(DcMotor.RunMode mode){
        this.frontLeft.setMode(mode);
        this.backLeft.setMode(mode);
        this.frontRight.setMode(mode);
        this.backRight.setMode(mode);
    }
    public void setPower(double power){
        this.frontLeft.setPower(power);
        this.backLeft.setPower(power);
        this.frontRight.setPower(power);
        this.backRight.setPower(power);
    }
    public boolean isBusy(){
        return frontLeft.isBusy() || backLeft.isBusy() || frontRight.isBusy() || backRight.isBusy();
    }
    public void driveToPosition(double distanceInches,double power){
        double distanceCm = distanceInches * 2.54 * 10;
        double rotations = distanceCm/WHEEL_CIRCUMFERENCE;
        int pulses = (int) (rotations * TPR);
        this.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.frontLeft.setTargetPosition(pulses);
        this.backLeft.setTargetPosition(pulses);
        this.frontRight.setTargetPosition(pulses);
        this.backRight.setTargetPosition(pulses);
        this.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.setPower(Math.abs(power));

    }
    public void strafeRight(double distanceInches,double power){
        double distanceCm = distanceInches * 2.54 * 10;
        double rotations = distanceCm/WHEEL_CIRCUMFERENCE;
        int pulses = (int) (rotations * TPR);
        this.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.frontLeft.setTargetPosition(pulses);
        this.backLeft.setTargetPosition(-pulses);
        this.frontRight.setTargetPosition(-pulses);
        this.backRight.setTargetPosition(pulses);
        this.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.setPower(Math.abs(power));
    }
    public void strafeLeft(double distanceInches, double power){
        this.strafeRight(-distanceInches, power);
    }
    public void turn(double degrees, double power){
        double distanceCm = degrees * 2.54 * DEGREES_TO_INCHES * 2 * 10; //The 2 and 10 are there for all of them because Android is wonky.
        double rotations = distanceCm/WHEEL_CIRCUMFERENCE;
        int pulses = (int) (rotations * TPR);
        this.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.frontLeft.setTargetPosition(pulses);
        this.backLeft.setTargetPosition(pulses);
        this.frontRight.setTargetPosition(-pulses);
        this.backRight.setTargetPosition(-pulses);
        this.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.setPower(Math.abs(power));
    }
}

package org.firstinspires.ftc.teamcode.Common;

import static java.lang.Math.signum;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class MechanumDrive {

    // Slow mode reduces speed to 35% for precise positioning and scoring
    public static final double SLOW_MODE_MULTIPLIER = 0.35;

    // Static flag to persist heading across OpMode transitions (Auto -> TeleOp)
    // Prevents IMU reset when switching from autonomous to driver control
    private static boolean headingInitialized = false;

    public static final double kP = 0.15;
    public static final double kI = 0;
    public static final double kD = 0.0;
    public static final double kF = 0;

    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private IMU imu;
    private PIDFCoefficients imuCoefficients;

    public MechanumDrive(HardwareMap hardwareMap) {
        this.frontLeft = hardwareMap.dcMotor.get(HardwareConfig.FRONT_LEFT_DRIVE_MOTOR);
        this.backLeft = hardwareMap.dcMotor.get(HardwareConfig.BACK_LEFT_DRIVE_MOTOR);
        this.frontRight = hardwareMap.dcMotor.get(HardwareConfig.FRONT_RIGHT_DRIVE_MOTOR);
        this.backRight = hardwareMap.dcMotor.get(HardwareConfig.BACK_RIGHT_DRIVE_MOTOR);
        this.imu = hardwareMap.get(IMU.class, HardwareConfig.IMU);

        this.imuCoefficients = new PIDFCoefficients(
                kP, kI, kD, kF
        );

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
        // prevent scalar from being set to bad value
        scaler = Math.max(0, Math.min(1.0, scaler));
        // double speedMultiplier = (scaler > 0.5) ? SLOW_MODE_MULTIPLIER : 1.0;
        double speedMultiplier = 1.0;

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

    public void turnToHeadingPID(double targetHeadingDegrees, double maxPower, double timeoutSeconds) {
        double targetRadians = Math.toRadians(targetHeadingDegrees);
        double tolerance = Math.toRadians(2);  // 2 degrees

        double previousError = 0;
        long startTime = System.currentTimeMillis();
        long timeoutMs = (long)(timeoutSeconds * 1000);
        double dt = 0.02;  // 20ms loop time in seconds

        while (true) {
            // Timeout check
            if (System.currentTimeMillis() - startTime > timeoutMs) {
                break;
            }

            // Calculate error
            double currentHeading = getHeading();
            double error = targetRadians - currentHeading;

            // Normalize error to [-PI, PI]
            while (error > Math.PI) error -= 2 * Math.PI;
            while (error < -Math.PI) error += 2 * Math.PI;

            // Exit if within tolerance
            if (Math.abs(error) < tolerance) {
                break;
            }

            // PD calculation - derivative is rate of change of error (rad/sec)
            double derivative = (error - previousError) / dt;
            double turnPower = (this.imuCoefficients.p * error) + (this.imuCoefficients.d * derivative);

            // Clamp to max power
            turnPower = Math.max(-maxPower, Math.min(maxPower, turnPower));

            // Apply power (scaler=0.0 to bypass slow mode and use full PID-calculated power)
            this.drive(0, 0, turnPower, 0.0);

            previousError = error;

            // Sleep to maintain consistent loop rate (20ms = 50Hz control loop)
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                break;
            }
        }

        this.stop();
    }

    // Tunable version - pass kP and kD directly for live tuning
    public void turnToHeadingPID(double targetHeadingDegrees, double kP, double kD, double maxPower, double timeoutSeconds) {
        double targetRadians = Math.toRadians(targetHeadingDegrees);
        double tolerance = Math.toRadians(2);

        double previousError = 0;
        long startTime = System.currentTimeMillis();
        long timeoutMs = (long)(timeoutSeconds * 1000);
        double dt = 0.02;  // 20ms loop time in seconds

        while (true) {
            // Timeout check
            if (System.currentTimeMillis() - startTime > timeoutMs) {
                break;
            }

            // Calculate error
            double currentHeading = getHeading();
            double error = targetRadians - currentHeading;

            // Normalize error to [-PI, PI]
            while (error > Math.PI) error -= 2 * Math.PI;
            while (error < -Math.PI) error += 2 * Math.PI;

            // Exit if within tolerance
            if (Math.abs(error) < tolerance) {
                break;
            }

            // PD calculation - derivative is rate of change of error (rad/sec)
            double derivative = (error - previousError) / dt;
            double turnPower = (kP * error) + (kD * derivative);

            // Clamp to max power
            turnPower = Math.max(-maxPower, Math.min(maxPower, turnPower));

            // Apply power (scaler=0.0 to bypass slow mode and use full PID-calculated power)
            this.drive(0, 0, turnPower, 0.0);

            previousError = error;

            // Sleep to maintain consistent loop rate (20ms = 50Hz control loop)
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                break;
            }
        }

        this.stop();
    }

    /**
     * Turns the robot to a target heading using PIDF control.
     *
     * <p>This method uses a full PIDF (Proportional-Integral-Derivative-Feedforward) controller
     * to rotate the robot to the specified heading. The control loop runs at 50Hz until the
     * robot is within 3 degrees of the target or the timeout is reached.</p>
     *
     * <h3>PIDF Components:</h3>
     * <ul>
     *   <li><b>P (Proportional):</b> Provides turning power proportional to the error.
     *       Higher values give faster response but may cause overshoot.</li>
     *   <li><b>I (Integral):</b> Accumulates error over time to eliminate steady-state error.
     *       Useful when friction prevents reaching the target. Clamped to [-10, 10] to prevent windup.</li>
     *   <li><b>D (Derivative):</b> Dampens the response based on how fast the error is changing.
     *       Reduces overshoot and oscillation.</li>
     *   <li><b>F (Feedforward):</b> Applies a constant power (0.09) in the direction of the error
     *       to overcome static friction and ensure the robot starts moving.</li>
     * </ul>
     *
     * <h3>Tuning Guide:</h3>
     * <ol>
     *   <li>Start with kP only (kI=0, kD=0). Increase until the robot turns and overshoots slightly.</li>
     *   <li>Add kD to reduce overshoot. Increase until oscillation stops.</li>
     *   <li>If the robot stops short of the target, add small amounts of kI.</li>
     * </ol>
     *
     * <h3>Example Usage:</h3>
     * <pre>{@code
     * // Turn to 90 degrees with tuned gains
     * drivetrain.turnToHeadingPIDF(90.0, 0.8, 0.05, 0.01, 0.6, 3.0);
     *
     * // Turn to -45 degrees with higher max power
     * drivetrain.turnToHeadingPIDF(-45.0, 0.8, 0.05, 0.01, 0.8, 2.0);
     * }</pre>
     *
     * @param targetAngle Target heading in degrees. Positive is counter-clockwise from the
     *                    initial heading (when IMU was reset). Range is typically [-180, 180].
     * @param kP          Proportional gain. Typical starting value: 0.5-1.0.
     *                    Units: power per radian of error.
     * @param kD          Derivative gain. Typical starting value: 0.01-0.1.
     *                    Units: power per (radian/second) of error change rate.
     * @param kI          Integral gain. Typical starting value: 0.0-0.05.
     *                    Units: power per (radian-second) of accumulated error.
     * @param powerMax    Maximum motor power for turning, clamped to [0, 1].
     *                    Lower values give more control, higher values give faster turns.
     * @param timeout_sec Maximum time in seconds before the method returns, even if the
     *                    target heading is not reached. Prevents infinite loops.
     */
    public void turnToHeadingPIDF(double targetAngle, double kP, double kD, double kI, double powerMax, double timeout_sec) {
        double targetRadians = Math.toRadians(targetAngle);
        double tolerance = Math.toRadians(3);
        double kF = 0.09;
        double maxIntegral = 10;

        double previousError = 0;
        double integral = 0;
        long startTime = System.currentTimeMillis();
        long previousTime = startTime;
        long timeoutMs = (long)(timeout_sec * 1000);

        while (true) {
            long currentTime = System.currentTimeMillis();

            if (currentTime - startTime > timeoutMs) {
                break;
            }

            double currentHeading = getHeading();
            double error = targetRadians - currentHeading;

            // Normalize error to [-PI, PI]
            while (error > Math.PI) error -= 2 * Math.PI;
            while (error < -Math.PI) error += 2 * Math.PI;

            if (Math.abs(error) < tolerance) {
                break;
            }

            // Calculate dt in seconds for correct unit scaling
            double dt = (currentTime - previousTime) / 1000.0;
            if (dt < 0.001) {
                dt = 0.02;  // Default to 20ms on first iteration
            }

            double p = kP * error;
            double d = kD * (error - previousError) / dt;
            integral += kI * error * dt;
            integral = Math.max(-maxIntegral, Math.min(maxIntegral, integral));
            double f = signum(error) * kF;

            double turnPower = p + integral + d + f;
            turnPower = Math.max(-powerMax, Math.min(powerMax, turnPower));

            this.drive(0, 0, turnPower, 0.0);

            previousError = error;
            previousTime = currentTime;

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                break;
            }
        }

        this.stop();
    }
}

package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp
public class MechanumFieldCentricRegularTeleOp extends LinearOpMode {

    private DcMotor frontLeftMotor;
    private DcMotor backLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backRightMotor;

    private DcMotor shooterLeftMotor;
    private DcMotor shooterRightMotor;
    private DcMotor indexMotor;
    private DcMotor feedWheel;
    private IMU imu;


    private void initializeDrivetrain() {
        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        this.frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        this.backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        this.frontRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        this.backRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
    }


    private void initializeImu() {
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
        this.imu.initialize(parameters);
    }

    private void calculateMechanumDrive(double x, double y, double rx) {
        double botHeading = this.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        // Rotate the movement direction counter to the bot's rotation
        double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
        double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

        rotX = rotX * 1.1;  // Counteract imperfect strafing

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio,
        // but only if at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
        double frontLeftPower = (rotY + rotX + rx) / denominator;
        double backLeftPower = (rotY - rotX + rx) / denominator;
        double frontRightPower = (rotY - rotX - rx) / denominator;
        double backRightPower = (rotY + rotX - rx) / denominator;

        this.frontLeftMotor.setPower(frontLeftPower);
        this.backLeftMotor.setPower(backLeftPower);
        this.frontRightMotor.setPower(frontRightPower);
        this.backRightMotor.setPower(backRightPower);
    }

    private void setShooterSpeed(double speed) {
        this.shooterRightMotor.setPower(speed);
        this.shooterLeftMotor.setPower(-speed);
    }

    @Override
    public void runOpMode() throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        this.frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        this.backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        this.frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        this.backRightMotor = hardwareMap.dcMotor.get("backRightMotor777");
        this.shooterLeftMotor = hardwareMap.dcMotor.get("shooterLeftMotor");
        this.shooterRightMotor = hardwareMap.dcMotor.get("shooterRightMotor");
        this.indexMotor = hardwareMap.dcMotor.get("indexMotor");
        this.feedWheel = hardwareMap.dcMotor.get("feedWheel");
        //Has been taken off the robot v v v
        // Servo ballProtector = hardwareMap.servo.get("ballProtector");

        this.initializeDrivetrain();

        // Retrieve the IMU from the hardware map
        this.imu = hardwareMap.get(IMU.class, "imu");
        this.initializeImu();

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {

            // set mechanum values
            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;
            this.calculateMechanumDrive(x, y, rx);

            // This button choice was made so that it is hard to hit on accident,
            // it can be freely changed based on preference.
            // The equivalent button is start on Xbox-style controllers.
            if (gamepad1.options) {
                this.imu.resetYaw();
            }

            // button control shooter speed
            if (gamepad2.a) {
                setShooterSpeed(0.45);
            } else if (gamepad2.b) {
                this.setShooterSpeed(0.55);
            } else if (gamepad2.y) {
                this.setShooterSpeed(0.65);
            } else if (gamepad2.x) {
                this.setShooterSpeed(0.35);
            } else {
                this.setShooterSpeed(0);
            }

            // Below code was for index to be controlled by operator using joystick; power by joystick position
            if (gamepad1.right_bumper) {
                indexMotor.setPower(-1);
            } else if (gamepad1.left_bumper) {
                indexMotor.setPower(1);
            } else {
                indexMotor.setPower(0);
            }

            // Below code is to control the roller before the shooter which we will call feedWheel
            if (gamepad2.right_bumper) {
                feedWheel.setPower(-1);
            } else if (gamepad2.left_bumper) {
                feedWheel.setPower(1);
            } else {
                feedWheel.setPower(0);
            }

        }
    }
}
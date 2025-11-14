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
    @Override
    public void runOpMode() throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        DcMotor shooterLeftMotor = hardwareMap.dcMotor.get("shooterLeftMotor");
        DcMotor shooterRightMotor = hardwareMap.dcMotor.get("shooterRightMotor");
        DcMotor indexMotor = hardwareMap.dcMotor.get("indexMotor");
        DcMotor feedWheel = hardwareMap.dcMotor.get("feedWheel");
        //Has been taken off the robot v v v
        // Servo ballProtector = hardwareMap.servo.get("ballProtector");

        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Retrieve the IMU from the hardware map
        IMU imu = hardwareMap.get(IMU.class, "imu");
        // Adjust the orientation parameters to match your robot
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        imu.initialize(parameters);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;

            // This button choice was made so that it is hard to hit on accident,
            // it can be freely changed based on preference.
            // The equivalent button is start on Xbox-style controllers.
            if (gamepad1.options) {
                imu.resetYaw();
            }

            double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

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

            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);
//             Original shooter settings 100% power
//            if (gamepad2.right_bumper) {
//                shooterRightMotor.setPower(-1);
//                shooterLeftMotor.setPower(1);
//            } else if (gamepad2.left_bumper) {
//                shooterRightMotor.setPower(1);
//                shooterLeftMotor.setPower(-1);
//            } else {
//                shooterRightMotor.setPower(0);
//                shooterLeftMotor.setPower(0);
//            }
            // Shooter power notes here please(
            //  )
            // Shooter Test first start with 50% then make notes here
            if (gamepad2.a) {
                //switched direction so it would run correct direction
                shooterRightMotor.setPower(0.85);
                shooterLeftMotor.setPower(-0.85);
            } else if (gamepad2.b) {
                shooterRightMotor.setPower(-0.75);
                shooterLeftMotor.setPower(0.75);
            }else if (gamepad2.y) {
                    //switched variables so it would run correct direction
                    shooterRightMotor.setPower(0.95);
                    shooterLeftMotor.setPower(-0.95);
            } else {
                shooterRightMotor.setPower(0);
                shooterLeftMotor.setPower(0);
                }
                    //below code was for index to be controlled by operator using joystick; power by joystick position
                    // indexMotor.setPower(gamepad2.left_stick_y);
                    if (gamepad1.right_bumper) {
                        indexMotor.setPower(-1);
                    } else if (gamepad1.left_bumper) {
                        indexMotor.setPower(1);
                    } else {
                        indexMotor.setPower(0);
                        //Below code is to control the roller before the shooter which we will call feedWheel for name. Open to name changes
                        if (gamepad2.right_bumper) {
                            feedWheel.setPower(-1);
                        } else if (gamepad2.left_bumper) {
                            feedWheel.setPower(1);
                        } else {
                            feedWheel.setPower(0);
                        }

                    }
                }
//           ballProtector is the original servo which has since been replaced with a roller before the shooter
//            if (gamepad2.a) {
//                ballProtector.setPosition(.5);
//            } else if (gamepad2.b) {
//                ballProtector.setPosition(.6);
//            }


            }
        }
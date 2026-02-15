package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@TeleOp
public class FlywheelTuning extends OpMode {
    public DcMotorEx leftMotor;
    public DcMotorEx rightMotor;
    public double lowVelocity = 1400;
    public double highVelocity = 2000;
    double curTargetVelocity = highVelocity;
    double F = 0;
    double P = 0;
    double[] stepSizes = {10.0, 1.0, 0.1, 0.001, 0.0001};
    int stepIndex = 1;

    @Override
    public void init() {
        leftMotor = hardwareMap.get(DcMotorEx.class, "shooterLeftMotor");
        rightMotor = hardwareMap.get(DcMotorEx.class, "shooterRightMotor");
        leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotor.setDirection(DcMotor.Direction.REVERSE);
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        leftMotor.setVelocityPIDFCoefficients(pidfCoefficients.p, pidfCoefficients.i,
                pidfCoefficients.d, pidfCoefficients.f);
        rightMotor.setVelocityPIDFCoefficients(pidfCoefficients.p, pidfCoefficients.i,
                pidfCoefficients.d, pidfCoefficients.f);
        telemetry.addLine("Init Complete");
    }

    @Override
    public void loop() {
        if (gamepad1.yWasPressed()) {
            if (curTargetVelocity == highVelocity) {
                curTargetVelocity = lowVelocity;
            } else {
                curTargetVelocity = highVelocity;
            }

        }
        if (gamepad1.bWasPressed()) {
            stepIndex = (stepIndex + 1) % stepSizes.length;
        }
        if (gamepad1.dpadRightWasPressed()) {
            F -= stepSizes[stepIndex];
        }
        if (gamepad1.dpadLeftWasPressed()) {
            F += stepSizes[stepIndex];
        }

        if (gamepad1.dpadDownWasPressed()) {
            P += stepSizes[stepIndex];
        }
        if (gamepad1.dpadUpWasPressed()) {
            P -= stepSizes[stepIndex];
        }
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        leftMotor.setVelocityPIDFCoefficients(pidfCoefficients.p, pidfCoefficients.i,
                pidfCoefficients.d, pidfCoefficients.f);
        rightMotor.setVelocityPIDFCoefficients(pidfCoefficients.p, pidfCoefficients.i,
                pidfCoefficients.d, pidfCoefficients.f);
        leftMotor.setVelocity(curTargetVelocity);
        rightMotor.setVelocity(curTargetVelocity);

        double curVelocityLeft = leftMotor.getVelocity();
//double curVelocityRight = rightMotor.getVelocity();
        double error = curTargetVelocity - curVelocityLeft;

        telemetry.addData("Target Velocity", curTargetVelocity);
        telemetry.addData("Current Velocity", "%.2f", curVelocityLeft);
        telemetry.addData("Error", "%.2f", error);
        telemetry.addLine("------------------------------");
        telemetry.addData("Tuning P", "%.4f (D-Pad U/D)");
        telemetry.addData("Tuning F", "%.4f (D-Pad L/R)");
        telemetry.addData("Step Size", "%.4f (B Button)", stepSizes[stepIndex]);
    }
}
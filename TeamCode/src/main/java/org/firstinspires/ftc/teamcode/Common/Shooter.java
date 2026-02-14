package org.firstinspires.ftc.teamcode.Common;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

public class Shooter {

    public static final double ENCODER_COUNTER_PER_REV = 28.0;

    public static final double SPEED_LOW = 1850;
    public static final double SPEED_MEDIUM_LOW = 2000;
    public static final double SPEED_MEDIUM_HIGH = 2250;
    public static final double SPEED_HIGH = 2500;
    public static final double SPEED_MAX = 1.0;
    public static final double FEED_SPEED = -1.0;
    public static final double FEED_SPEED_REVERSE = 1.0;
    public static final double FEED_IDLE = -0.2;
    public static final double COUNTS_TO_RPMS = 60 / Shooter.ENCODER_COUNTER_PER_REV;
    public static final double kP = 5.0;
    public static final double kI = 0.05;
    public static final double kD = 0.0;
    public static final double kF = 12.5;
    public static final double READY_THRESH_UPPER = 1.02;
    public static final double READY_THRESH_LOWER = 0.98;


    public DcMotorEx leftMotor;
    public DcMotorEx rightMotor;

    private Servo servo;
    private double targetVelocity;
    public final boolean enableAutoShoot = false;

    public Shooter(HardwareMap hardwareMap) {
        this.leftMotor = hardwareMap.get(DcMotorEx.class, HardwareConfig.SHOOTER_LEFT_MOTOR);
        this.rightMotor = hardwareMap.get(DcMotorEx.class, HardwareConfig.SHOOTER_RIGHT_MOTOR);
        this.servo = hardwareMap.get(Servo.class, HardwareConfig.FEED_SERVO);
        this.leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        this.leftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.rightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(
                kP, kI, kD, kF
        );

        this.leftMotor.setVelocityPIDFCoefficients(pidfCoefficients.p, pidfCoefficients.i,
                pidfCoefficients.d, pidfCoefficients.f);
        this.rightMotor.setVelocityPIDFCoefficients(pidfCoefficients.p, pidfCoefficients.i,
                pidfCoefficients.d, pidfCoefficients.f);

    }

    public void setSpeed(double speed) {
        this.leftMotor.setPower(-speed);
        this.rightMotor.setPower(speed);
    }

    public void setVelocity(double rpms) {
        this.targetVelocity = rpms;
        this.leftMotor.setVelocity(-1 * Shooter.ENCODER_COUNTER_PER_REV * (rpms / 60)); // uncomment once left encoder is fixed
        this.rightMotor.setVelocity(Shooter.ENCODER_COUNTER_PER_REV * (rpms / 60));
    }

    public double getTargetVelocity() {
        return this.targetVelocity;
    }

    public double getRightVelocity() {
        return this.rightMotor.getVelocity()*COUNTS_TO_RPMS;
    }

    public double getLeftVelocity() {
        return -1* this.leftMotor.getVelocity()*COUNTS_TO_RPMS;
    }

    public double getVelocity() {
        return (this.getRightVelocity() + this.getLeftVelocity()) / 2;
    }

    public boolean isReady() {
        double minVelocity = this.targetVelocity * READY_THRESH_LOWER;
        double maxVelocity = this.targetVelocity * READY_THRESH_UPPER;
        double currentVelocity = this.getVelocity();
        return minVelocity <= currentVelocity && currentVelocity <= maxVelocity;
    }

    public void stop() {
        this.leftMotor.setPower(0);
        this.rightMotor.setPower(0);
    }



    public void servoOpen() {
        this.servo.setPosition(1);}

        public void servoClose() {
            this.servo.setPosition(-1);}
}

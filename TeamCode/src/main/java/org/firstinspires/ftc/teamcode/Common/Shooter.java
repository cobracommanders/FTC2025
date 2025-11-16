package org.firstinspires.ftc.teamcode.Common;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Shooter {

    public static final double SPEED_LOW = 0.35;
    public static final double SPEED_MEDIUM_LOW = 0.45;
    public static final double SPEED_MEDIUM_HIGH = 0.55;
    public static final double SPEED_HIGH = 0.65;
    public static final double SPEED_MAX = 1.0;


    private DcMotor leftMotor;
    private DcMotor rightMotor;
    private DcMotor feedWheel;

    public Shooter(HardwareMap hardwareMap) {
        this.leftMotor = hardwareMap.dcMotor.get(HardwareConfig.SHOOTER_LEFT_MOTOR);
        this.rightMotor = hardwareMap.dcMotor.get(HardwareConfig.SHOOTER_RIGHT_MOTOR);
        this.feedWheel = hardwareMap.dcMotor.get(HardwareConfig.FEED_WHEEL_MOTOR);

        this.leftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.rightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.feedWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void setSpeed(double speed) {
        this.leftMotor.setPower(-speed);
        this.rightMotor.setPower(speed);
    }

    public void stop() {
        this.leftMotor.setPower(0);
        this.rightMotor.setPower(0);
    }

    public void feed() {
        this.feedWheel.setPower(1.0);
    }

    public void feedReverse() {
        this.feedWheel.setPower(-1.0);
    }

    public void feedStop() {
        this.feedWheel.setPower(0);
    }
}

package org.firstinspires.ftc.teamcode.Common;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    private DcMotor motor;

    public Intake(HardwareMap hardwareMap) {
        this.motor = hardwareMap.dcMotor.get(HardwareConfig.INDEX_MOTOR);
        this.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.motor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void intake() {
        this.motor.setPower(1.0);
    }

    public void reverse() {
        this.motor.setPower(-1.0);
    }

    public void stop() {
        this.motor.setPower(0);
    }

    public void drive(double power) {
        this.motor.setPower(power);
    }
}

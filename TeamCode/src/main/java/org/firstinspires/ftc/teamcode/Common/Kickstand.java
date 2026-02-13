package org.firstinspires.ftc.teamcode.Common;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

public class Kickstand {
     this.feedWheel =hardwareMap.dcMotor.get(HardwareConfig.FEED_WHEEL_MOTOR);
     this.feedWheel.setPower(0);
     this.feedWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


    private DcMotor feedWheel;

    public void feedStop() {
        this.feedWheel.setPower(0);
    }

    public void kick() {
        this.feedWheel.setPower(0.6);
    }

}
package org.firstinspires.ftc.teamcode.Common;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Kickstand {

    private DcMotor kickstand;
    public Kickstand(HardwareMap hardwareMap) {
        this.kickstand = hardwareMap.dcMotor.get(HardwareConfig.KICKSTAND_MOTOR);
        this.kickstand.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.kickstand.setPower(0);
    }

    public void kickStop() {
        this.kickstand.setPower(0);
    }
public void unkick() {
        this.kickstand.setPower(-0.2);
}
    public void kick() {
        this.kickstand.setPower(0.6);
    }

}
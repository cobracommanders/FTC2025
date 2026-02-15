package org.firstinspires.ftc.teamcode.Common;

import java.util.function.Consumer;

public class AutoShoot {

    private Intake intake;
    private Shooter shooter;
    private double fireVelcity;

    public AutoShoot(Intake intake, Shooter shooter, double fireVelocity) {
        this.intake = intake;
        this.shooter = shooter;
        this.fireVelcity = fireVelocity;
    }

    public final void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    public void spinUp() {
        this.shooter.setVelocity(this.fireVelcity);
    }

    private void indexBall() {
        this.shooter.servoClose();
        sleep(300); // TODO: replace with servo.getPosition() checks with bounds
        this.intake.drive(0.7);
        sleep(300);
        this.intake.stop();
    }

    private void shoot() {
        this.shooter.setVelocity(this.fireVelcity);
        this.shooter.servoOpen();
        sleep(300L);
        this.intake.drive(0.7);
        sleep(300L);
        this.intake.stop();
        this.shooter.servoClose();
    }

    public void autoShoot(int num_balls) {  // TODO: while op mode is active must be here!!!
        this.spinUp();
        for (int i = 0; i < num_balls; i++) {
            this.indexBall();
            while (!this.shooter.isReady()) {
                sleep(50L);
            }
            this.shoot();
        }
        this.shooter.idle();
    }

}

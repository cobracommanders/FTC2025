package org.firstinspires.ftc.teamcode.Common;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

public class AutoShoot {

    public enum State {
        IDLE,
        SPIN_UP,
        INDEX_CLOSE,
        INDEX_INTAKE,
        WAIT_READY,
        SHOOT_OPEN,
        SHOOT_INTAKE,
        SHOOT_CLEANUP,
        DONE
    }

    private final LinearOpMode opMode;
    private final Intake intake;
    private final Shooter shooter;
    private final double fireVelocity;

    private State state = State.IDLE;
    private int ballsRemaining = 0;
    private long stateStartTime = 0;

    public AutoShoot(LinearOpMode opMode, Intake intake, Shooter shooter, double fireVelocity) {
        this.opMode = opMode;
        this.intake = intake;
        this.shooter = shooter;
        this.fireVelocity = fireVelocity;
    }

    /**
     * Start the auto-shoot sequence. Non-blocking -- call update() each loop iteration.
     */
    public void start(int numBalls) {
        this.ballsRemaining = numBalls;
        setState(State.SPIN_UP);
    }

    /**
     * Cancel the sequence and return hardware to a safe state.
     */
    public void cancel() {
        this.intake.stop();
        this.shooter.servoClose();
        this.shooter.idle();
        this.state = State.IDLE;
        this.ballsRemaining = 0;
    }

    /** Returns true if the sequence is currently running. */
    public boolean isBusy() {
        return state != State.IDLE && state != State.DONE;
    }

    public State getState() {
        return state;
    }

    public int getBallsRemaining() {
        return ballsRemaining;
    }

    private void setState(State newState) {
        this.state = newState;
        this.stateStartTime = System.currentTimeMillis();
    }

    private long elapsed() {
        return System.currentTimeMillis() - stateStartTime;
    }

    /**
     * Call this every loop iteration. Advances the state machine one step.
     * Returns immediately (non-blocking) so the rest of your loop can run.
     */
    public void update() {
        switch (state) {
            case IDLE:
            case DONE:
                break;

            case SPIN_UP:
                shooter.setVelocity(fireVelocity);
                shooter.servoClose();
                setState(State.INDEX_CLOSE);
                break;

            case INDEX_CLOSE:
                // Wait for servo to close (300ms)
                if (elapsed() >= 300) {
                    intake.drive(0.7);
                    setState(State.INDEX_INTAKE);
                }
                break;

            case INDEX_INTAKE:
                // Wait for intake to index ball (300ms)
                if (elapsed() >= 300) {
                    intake.stop();
                    setState(State.WAIT_READY);
                }
                break;

            case WAIT_READY:
                // Wait for flywheel to reach target velocity
                if (shooter.isReady()) {
                    shooter.setVelocity(fireVelocity);
                    shooter.servoOpen();
                    setState(State.SHOOT_OPEN);
                }
                break;

            case SHOOT_OPEN:
                // Wait for ball to contact flywheel (300ms)
                if (elapsed() >= 300) {
                    intake.drive(0.7);
                    setState(State.SHOOT_INTAKE);
                }
                break;

            case SHOOT_INTAKE:
                // Wait for intake to push ball through (300ms)
                if (elapsed() >= 300) {
                    intake.stop();
                    shooter.servoClose();
                    ballsRemaining--;
                    if (ballsRemaining > 0) {
                        setState(State.INDEX_CLOSE);
                    } else {
                        shooter.idle();
                        setState(State.DONE);
                    }
                }
                break;
        }
    }

    /**
     * Blocking convenience method for autonomous programs.
     * Runs the full shoot sequence while checking opModeIsActive() each iteration.
     * If the opmode is stopped, the sequence is cancelled and hardware is set to a safe state.
     */
    public void run(int numBalls) {
        start(numBalls);
        while (isBusy() && opMode.opModeIsActive()) {
            update();
            opMode.sleep(20);
        }
        if (!opMode.opModeIsActive()) {
            cancel();
        }
    }
}

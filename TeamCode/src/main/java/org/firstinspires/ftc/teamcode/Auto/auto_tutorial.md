# Autonomous Programming Reference

## New Auto that Shoots

Likely want the setup for the new auto to be identical (to avoid confusion and IMU issues)

Starter steps:
- Turns clockwise 45°
- Spins up shooter, waits until ready
- Fires 3 shots with recovery time between each
- Drives backwards (negative y) for remaining time to get off the line

## Autonomous Template

Use MechanumAutonomousShoot ([source](MechanumAutonomousShootBLUE.java)) as a template. Feel free to update it. Right now it doesn't do anything

```java
package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.Common.MechanumDrive;
import org.firstinspires.ftc.teamcode.Common.Shooter;
import org.firstinspires.ftc.teamcode.Common.Intake;

@Autonomous(name = "Your Auto Name", group = "Robot")
public class YourClassName extends LinearOpMode {
    private MechanumDrive drivetrain;
    private Shooter shooter;
    private Intake intake;

    @Override
    public void runOpMode() throws InterruptedException {
        this.drivetrain = new MechanumDrive(hardwareMap);
        this.shooter = new Shooter(hardwareMap);
        this.intake = new Intake(hardwareMap);

        waitForStart();
        resetRuntime();

        // Your implementation here!!!
    }
}
```

## API Reference

### MechanumDrive ([source](../Common/MechanumDrive.java))

```java
this.drivetrain.drive(double x, double y, double rotation, double scaler)
```

This method drives the robot in a specific way. 

- `x`: strafe (+ right, - left)
- `y`: forward/back (+ forward, - back)
- `rotation`: turn (+ CCW, - CW)
- `scaler`: use `1.0`

example:
```java
this.drivetrain.drive(0.0, 0.5, 0.0, 1.0);
```

```java
this.drivetrain.turnToHeading(double degrees, double speed)
```

This method drives the robot in a specific way.

- `degrees`: angle to turn to
- `power`: speed as a percentage to turn with

example:
```java
this.drivetrain.turnToHeading(45, 0.35);  // turn 45 degrees (clockwise) at 35% power
```

```java
this.drivetrain.stop()
```
- Stops all drive motors

### Intake ([source](../Common/Intake.java))

**Methods:**
```java
void intake()                   // Run the intake
void reverse()                  // Run the intake in reverse
void stop()                     // Stop the intake motors
```


### Shooter ([source](../Common/Shooter.java))

**Constants:**
```java
Shooter.SPEED_LOW          // 1850 RPM
Shooter.SPEED_MEDIUM_LOW   // 2000 RPM
Shooter.SPEED_MEDIUM_HIGH  // 2250 RPM
Shooter.SPEED_HIGH         // 2500 RPM
```

**Methods:**
```java
void setVelocity(double rpms)  // Set target velocity
boolean isReady()               // True when at target ±4%
void feed()                     // Run feed wheel (shoot)
void feedIdle()                 // Run feed wheel in reverse slowly to keep the balls in
void feedStop()                 // Stop feed wheel
void stop()                     // Stop shooter motors
```

### Timing & Control

```java
double getRuntime()      // Seconds since resetRuntime()
void sleep(long ms)      // Pause execution (milliseconds)
boolean opModeIsActive() // True while auto running
```

## Code Snippets

### Timed drive
```java
while (opModeIsActive() && getRuntime() < 30.0) {
    this.drivetrain.drive(0, -0.5, 0, 1.0);
}
this.drivetrain.stop();
```

### Time-based turn
```java
while (opModeIsActive() && getRuntime() < 1.5) {  // turn for 1.5 seconds at 30% speed in CCW direction
    this.drivetrain.drive(0, 0, -0.3, 1.0);
}
this.drivetrain.stop();
```

### IMU-based turn to specific heading
```java
this.drivetrain.turnToHeading(45, 0.3);  // Turn to 45 degrees at 30% power
```

### Drive intake
```java
this.intake.intake();
```

### Stop intake
```java
this.intake.stop():;
```

### Shooter spin-up with wait
```java
this.shooter.feedIdle();
this.shooter.setVelocity(Shooter.SPEED_MEDIUM_LOW);  // see Shooter.java for what RPMs this value corresponds to

double startTime = getRuntime();
while (opModeIsActive() && !this.shooter.isReady() && (getRuntime() - startTime) < 5.0) {
    sleep(50);
}
```

### Fire single shot when ready

```java
// Spin up and wait
this.shooter.feedIdle();
this.shooter.setVelocity(Shooter.SPEED_MEDIUM_LOW);

while (opModeIsActive() && !this.shooter.isReady()) {
    sleep(50);
}

// Fire one shot
this.shooter.feed();
sleep(300);
this.shooter.feedIdle();
```

## Notes

- Turn duration (1.5s) will need tuning for exactly 45°
- `getRuntime()` returns seconds, `sleep()` takes milliseconds
- **Always** check `opModeIsActive()` in loops for emergency stop

## See Also

- [`MechanumDrive.java`](../Common/MechanumDrive.java)
- [`Shooter.java`](../Common/Shooter.java)
- [`MechanumAutonomousDriveBackwards.java`](MechanumAutonomousDriveBackwards.java)
- [`MechanumFieldCentricRegularTeleOp.java`](../TeleOp/MechanumFieldCentricRegularTeleOp.java:47-88)

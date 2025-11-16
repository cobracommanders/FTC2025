# TeamCode Module - FTC 2025/2026 Season

This README will help you understand how our code is organized and how everything works together.

## 📁 Code Organization

Our code is organized into folders (called "packages" in Java):

```
teamcode/
├── Common/          - Reusable robot parts (drivetrain, shooter, intake)
├── TeleOp/          - Driver-controlled programs
└── Auto/            - Autonomous programs (robot drives itself)
```

### Common/ - Robot Subsystems

These are the building blocks of our robot. Each file controls one major part:

- **`MechanumDrive.java`** - Controls the 4-wheel mecanum drivetrain
  - Handles field-centric driving (joystick always points away from driver)
  - Uses IMU (gyroscope) to know which way the robot is facing
  - Has slow mode for precise movements

- **`Shooter.java`** - Controls the shooting mechanism
  - Two motors that spin to launch game pieces
  - Feed wheel that pushes pieces into the shooter
  - Different preset speeds for different distances

- **`Intake.java`** - Controls the intake mechanism
  - One motor that pulls game pieces into the robot
  - Can run forward (intake) or reverse (spit out)

### TeleOp/ - Driver Control Programs

These are the programs drivers use during matches:

- **`MechanumFieldCentricRegularTeleOp.java`** - Main driver control program
  - **Gamepad 1** (Driver): Controls driving, intake, slow mode
  - **Gamepad 2** (Operator): Controls shooter, feed wheel, intake
  - See the file for complete button mappings

### Auto/ - Autonomous Programs

These programs run during the first 30 seconds when the robot drives itself:

- **`MechanumAutonomousDriveBackwards.java`** - Simple: drive backwards for 2 seconds
- **`MechanumAutonomousShoot.java`** - Drive backwards (shooting code in to be added)
- **`MechanumAutonomousDriveBackwardsTurn.java`** - Experimental: drive backwards, then turn

## 🎮 How the Code Works

### Field-Centric Driving

Our robot uses **field-centric control**, which means:
- Pushing the joystick forward **always** moves away from the driver
- This works even if the robot has spun around completely
- Makes driving much more intuitive

**How it works:**
1. IMU (gyroscope) tells us which direction the robot is facing
2. Code uses math (trigonometry) to rotate the joystick inputs
3. Result: joystick directions stay relative to the driver, not the robot

### Robot Heading Persistence (Auto → TeleOp)

One cool feature: the robot **remembers** its direction when switching from Auto to TeleOp!

**How it works:**
- Auto program runs and sets the robot's "forward" direction
- When Auto ends, we save that direction
- TeleOp starts and keeps using the same "forward" direction
- Drivers can still manually reset with the Options button if needed

This is controlled by the `headingInitialized` variable in `MechanumDrive.java`.

## 🚀 Getting Started

### If you're new to programming:

1. **Start by reading the TeleOp file** (`MechanumFieldCentricRegularTeleOp.java`)
   - This shows how all the pieces work together
   - Has lots of comments explaining each section

2. **Then look at the Common files** to see how each robot part works
   - `Shooter.java` - controls the shooter and feedwheel
   - `Intake.java` - controls the intake
   - `MechanumDrive.java` - controls the drivetrain

3. **Finally check out Auto programs** to see autonomous routines
   - These are shorter and simpler than TeleOp
   - Good examples of using the Common classes

### If you're new to Java (but know another language):

- **Classes as blueprints**: Each file (MechanumDrive, Shooter, etc.) is a class
- **Objects as instances**: In TeleOp, we create objects like `new MechanumDrive(hardwareMap)`
- **Public methods**: Methods like `drive()`, `setSpeed()` are how we control the robot parts
- **Static variables**: Used in MechanumDrive for the `headingInitialized` flag (persists across OpModes)

### If you're new to FTC:

- **OpMode**: A program that runs on the robot (either TeleOp or Autonomous)
- **HardwareMap**: How we connect code to physical motors and sensors
- **LinearOpMode**: The style of OpMode we use (runs top-to-bottom with loops)
- **Driver Station**: The phone/tablet that drivers use to control the robot

## 📚 Key Concepts Explained

### Why do we use classes for robot parts?

Instead of putting all the motor code in one giant file, we break it into pieces:
- **Easier to understand**: Each file does one thing
- **Reusable**: Both TeleOp and Auto can use the same MechanumDrive class
- **Easier to test**: Can test shooter without worrying about drivetrain
- **Multiple people can work**: One person works on shooter, another on drivetrain

### What's with all the `this.` everywhere?

`this.frontLeft` means "the frontLeft motor that belongs to THIS robot part"
- It's optional but makes code clearer
- Helps avoid confusion between variables and parameters

### Why are some variables `private` and some `public`?

- **private**: Only this class can use it (internal details)
  - Example: `private DcMotor leftMotor` in Shooter.java
- **public**: Anyone can use it (part of the interface)
  - Example: `public static final double SPEED_HIGH` in Shooter.java

This is called "encapsulation" - hiding complexity.

### What does `static` mean?

- **Regular variables**: Each object has its own copy
  - Every Shooter object has its own `leftMotor`
- **Static variables**: Shared by ALL objects of this class
  - All MechanumDrive objects share the same `headingInitialized` flag
  - Used for data that needs to persist across OpModes

## 🐛 Troubleshooting

### Robot moves in wrong direction
- Check motor directions in `MechanumDrive.initializeDrivetrain()`
- Your robot's motors might be mounted differently

### Field-centric is backwards
- Press the Options / Start button to reset heading
- Or check IMU orientation in `MechanumDrive.initializeImu()`

### Shooter spins wrong way
- Check the negative sign in `Shooter.setSpeed()` on line 30

### Motors keep spinning instead of stopping
- Check ZeroPowerBehavior is set to BRAKE (do not do this for a shooter or other fast moving system)
- Make sure you're calling `.stop()` methods

## 📖 Learning Resources

### Official FTC Resources
- [Game Manual 0 (GM0)](https://gm0.org/) - Community-created FTC guide
- [FTC Docs](https://ftc-docs.firstinspires.org/) - Official documentation
- [FTC SDK GitHub](https://github.com/FIRST-Tech-Challenge/FtcRobotController) - Example code

### Java Learning
- [Codecademy Java](https://www.codecademy.com/learn/learn-java)
- [W3Schools Java](https://www.w3schools.com/java/)

### Advanced Topics (for when you're ready)
- PID control for precise movements
- Computer vision with cameras
- Path following for complex autonomous
- State machines for complex mechanisms

## 🤝 Contributing to This Code

### Before making changes:
2. **Add comments** explaining what you changed and why
3. **Ask for help** if you're not sure - it's better to ask than break something!

### Code style guide:
- Use clear variable names: `shooterSpeed` not `s`
- Add comments explaining **why**, not just **what**
- Keep methods short (one job per method)
- Test your changes before committing

## ❓ Questions?

If you're stuck or confused:
1. **Read the comments** in the code - they explain a lot!
2. **Ask a mentor or experienced team member**
3. **Check GM0** (gm0.org) - best FTC resource available
4. **Google it** - lots of FTC teams share their solutions online

---

Remember: Everyone starts as a beginner. Don't be afraid to experiment, break code, and learn from mistakes. That's how you become better programmer!

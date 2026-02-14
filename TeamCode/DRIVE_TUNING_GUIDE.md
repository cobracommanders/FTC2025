# Drive PID Tuning Guide

## Setup
1. Verify encoder constants in `MechanumDrive.java` (lines 60-63):
   - `TICKS_PER_REV` = 537.7 (GoBilda 312 RPM)
   - `WHEEL_DIAMETER_INCHES` = 3.78 (96mm wheels)
2. Deploy and run **"Drive PID Tuning"** TeleOp
3. Open FTC Dashboard: `192.168.43.1:8080`
4. Select graphs: `drive/distanceError`, `drive/power`, `drive/headingError`

## Tuning Steps

### Step 1: Tune kP
- Start: `kP=0.02`, `kD=0`, `kI=0`
- Increase kP until robot reaches target with slight overshoot
- If oscillating wildly, reduce kP

### Step 2: Tune kD
- Add kD to dampen overshoot
- Start around `kD=0.005`, increase until overshoot stops
- Too much kD = sluggish response

### Step 3: Tune kI (if needed)
- Only if robot consistently stops short
- Start very small: `kI=0.0005`
- Increase slowly until steady-state error eliminated

## Controls
| Button | Action |
|--------|--------|
| A | Drive forward |
| B | Drive backward |
| D-Pad Up/Down | Adjust kP |
| D-Pad Left/Right | Adjust kD |
| Bumpers | Adjust max power |
| X/Y | Adjust distance |
| G2 A/B | Adjust kI |
| Options | Reset IMU |

## Typical Values
- kP: 0.02 - 0.08
- kD: 0.005 - 0.02
- kI: 0 - 0.002

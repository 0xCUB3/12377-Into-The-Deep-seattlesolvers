package org.firstinspires.ftc.teamcode.opmode.TeleOp;

import static org.firstinspires.ftc.teamcode.hardware.Globals.*;
import static org.firstinspires.ftc.teamcode.commandbase.Deposit.*;
import static org.firstinspires.ftc.teamcode.commandbase.Intake.*;

import com.pedropathing.localization.Pose;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.ConditionalCommand;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.UninterruptibleCommand;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.commandbase.Deposit;
import org.firstinspires.ftc.teamcode.commandbase.Drive;
import org.firstinspires.ftc.teamcode.commandbase.Intake;
import org.firstinspires.ftc.teamcode.commandbase.commands.*;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.TelemetryData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TeleOp
public class FullTeleOp extends CommandOpMode {
    private static final Logger logger = LoggerFactory.getLogger(FullTeleOp.class);

    public GamepadEx driver;
    public GamepadEx operator;

    public ElapsedTime timer;
    public ElapsedTime gameTimer;

    TelemetryData telemetryData = new TelemetryData(telemetry);

    private final Robot robot = Robot.getInstance();

    private boolean endgame = false;
    private boolean frontSpecimenScoring = false;

    @Override
    public void initialize() {
        logger.info("Initializing FullTeleOp");

        // Must have for all opModes
        opModeType = OpModeType.TELEOP;
        // depositInit = DepositInit.MIDDLE_HOLD;

        INTAKE_HOLD_SPEED = 0;

        // DO NOT REMOVE! Resetting FTCLib Command Scheduler
        super.reset();
        logger.info("Command Scheduler reset");

        robot.init(hardwareMap);
        logger.info("Robot initialized");

        // Initialize subsystems
        register(robot.deposit, robot.intake);
        logger.info("Deposit and Intake subsystems registered");

        robot.intake.setActiveIntake(IntakeMotorState.STOP);
        logger.info("Intake motor set to STOP");

        driver = new GamepadEx(gamepad1);
        operator = new GamepadEx(gamepad2);
        logger.info("Gamepads initialized");

        // Driver Gamepad controls
        driver.getGamepadButton(GamepadKeys.Button.B).whenPressed(
                new InstantCommand(() -> {
                    logger.info("Driver B pressed: Toggling Intake (ANY_COLOR)");
                    robot.intake.toggleActiveIntake(SampleColorTarget.ANY_COLOR);
                })
        );

        driver.getGamepadButton(GamepadKeys.Button.A).whenPressed(
                new InstantCommand(() -> {
                    logger.info("Driver A pressed: Toggling Intake (ALLIANCE_ONLY)");
                    robot.intake.toggleActiveIntake(SampleColorTarget.ALLIANCE_ONLY);
                })
        );

        driver.getGamepadButton(GamepadKeys.Button.X).whenPressed(
                new SequentialCommandGroup(
                        new InstantCommand(() -> logger.info("Driver X pressed: Moving Intake to INTAKE_READY")),
                        new SetIntake(robot, IntakePivotState.INTAKE_READY, IntakeMotorState.HOLD, MAX_EXTENDO_EXTENSION/2, true)
                )
        );

        driver.getGamepadButton(GamepadKeys.Button.Y).whenPressed(
                new SequentialCommandGroup(
                        new InstantCommand(() -> logger.info("Driver Y pressed: Moving Intake to INTAKE (full extendo)")),
                        new SetIntake(robot, IntakePivotState.INTAKE_READY, intakeMotorState, MAX_EXTENDO_EXTENSION, true)
                )
        );

//        driver.getGamepadButton(GamepadKeys.Button.DPAD_UP).whenPressed(
//                new InstantCommand(() -> robot.follower.setPose(new Pose(0, 0, 0)))
//        );

        operator.getGamepadButton(GamepadKeys.Button.DPAD_UP).whenPressed(
                new SequentialCommandGroup(
                        new InstantCommand(() -> logger.info("Operator Dpad Up pressed: Moving Deposit to MIDDLE_HOLD (Endgame Ascent Height)")),
                        new SetDeposit(robot, Deposit.DepositPivotState.MIDDLE_HOLD, ENDGAME_ASCENT_HEIGHT, false).withTimeout(2500)  // Adjust timeout as needed
                )
        );

        driver.getGamepadButton(GamepadKeys.Button.DPAD_DOWN).whenPressed(
                new SequentialCommandGroup(
                        new InstantCommand(() -> logger.info("Driver Dpad Down pressed: Moving Intake to INTAKE_READY and setting IntakeMotorState to HOLD")),
                        new InstantCommand(() -> robot.intake.setPivot(IntakePivotState.INTAKE_READY)),
                        new InstantCommand(() -> robot.intake.setActiveIntake(IntakeMotorState.HOLD))
                )
        );

        driver.getGamepadButton(GamepadKeys.Button.DPAD_RIGHT).whenPressed(
                new InstantCommand(() -> {
                    logger.info("Driver Dpad Right pressed: Retracting Extendo");
                    robot.intake.setExtendoTarget(0);
                })
        );

        // TO-DO: need to make into 1 method in Drive.java
        driver.getGamepadButton(GamepadKeys.Button.DPAD_LEFT).whenPressed(
                new ConditionalCommand(
                        new InstantCommand(() -> {
                            logger.info("Driver Dpad Left pressed: Setting Sub Pusher to OUT");
                            robot.drive.setSubPusher(Drive.SubPusherState.OUT);
                        }),
                        new InstantCommand(() -> {
                            logger.info("Driver Dpad Left pressed: Setting Sub Pusher to IN");
                            robot.drive.setSubPusher(Drive.SubPusherState.IN);
                        }),
                        () -> Drive.subPusherState.equals(Drive.SubPusherState.IN))
        );

        driver.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER).whenPressed(
                new InstantCommand(() -> {
                    logger.info("Driver Left Bumper pressed: Moving Intake Pivot to TRANSFER");
                    robot.intake.setPivot(IntakePivotState.TRANSFER);
                })
        );

        driver.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER).whenPressed(
                new InstantCommand(() -> {
                    logger.info("Driver Right Bumper pressed: Moving Intake Pivot to INTAKE");
                    robot.intake.setPivot(IntakePivotState.INTAKE);
                })
        );

        driver.getGamepadButton(GamepadKeys.Button.LEFT_STICK_BUTTON).whenPressed(
                new UninterruptibleCommand(
                        new SequentialCommandGroup(
                                new InstantCommand(() -> logger.info("Driver Left Stick pressed: Starting UNINTERRUPTIBLE UndoTransfer")),
                                new UndoTransfer(robot),
                                new SetIntake(robot, IntakePivotState.INTAKE, IntakeMotorState.REVERSE, MAX_EXTENDO_EXTENSION, true)
                        )
                )
        );

        driver.getGamepadButton(GamepadKeys.Button.RIGHT_STICK_BUTTON).whenPressed(
                new UninterruptibleCommand(
                        new SequentialCommandGroup(
                                new InstantCommand(() -> logger.info("Driver Right Stick pressed: Starting UNINTERRUPTIBLE RealTransfer")),
                                new RealTransfer(robot)
                        )
                )
        );

        // Reset CommandScheduler + make slides reached true
        driver.getGamepadButton(GamepadKeys.Button.PS).whenPressed(
                new UninterruptibleCommand(
                        new SequentialCommandGroup(
                                new InstantCommand(() -> logger.warn("Driver PS pressed: Resetting CommandScheduler and setting slides target to current position")),
                                new InstantCommand(super::reset),
                                new InstantCommand(() -> robot.drive.setSubPusher(Drive.SubPusherState.IN)),
                                new InstantCommand(() -> robot.deposit.setSlideTarget(robot.deposit.getLiftScaledPosition()))
                        )
                )
        );

        // Operator Gamepad controls
        operator.getGamepadButton(GamepadKeys.Button.X).whenPressed(
                new InstantCommand(() -> {
                    frontSpecimenScoring = !frontSpecimenScoring;
                    logger.info("Operator X pressed: Toggling frontSpecimenScoring to " + frontSpecimenScoring);
                })
        );

        operator.getGamepadButton(GamepadKeys.Button.B).whenPressed(
                new ConditionalCommand(
                        new UninterruptibleCommand(
                                new SequentialCommandGroup(
                                        new InstantCommand(() -> logger.info("Operator B pressed: Starting UNINTERRUPTIBLE SetDeposit to SPECIMEN_SCORING (front)")),
                                        new SetDeposit(robot, DepositPivotState.SPECIMEN_SCORING, HIGH_SPECIMEN_HEIGHT, false).withTimeout(1500)
                                )
                        ),
                        new UninterruptibleCommand(
                                new SequentialCommandGroup(
                                        new InstantCommand(() -> logger.info("Operator B pressed: Starting UNINTERRUPTIBLE SetDeposit to SPECIMEN_SCORING (back)")),
                                        new SetDeposit(robot, DepositPivotState.SPECIMEN_SCORING, HIGH_SPECIMEN_HEIGHT, false).withTimeout(1500)
                                )
                        ),
                        () -> frontSpecimenScoring
                )
        );

        operator.getGamepadButton(GamepadKeys.Button.A).whenPressed(
                new ConditionalCommand(
                        new UninterruptibleCommand(
                                new SequentialCommandGroup(
                                        new InstantCommand(() -> logger.info("Operator A pressed: Starting UNINTERRUPTIBLE SetDeposit to SPECIMEN_INTAKE (front)")),
                                        new SetDeposit(robot, DepositPivotState.SPECIMEN_INTAKE, 0, false).withTimeout(1500)
                                )
                        ),
                        new UninterruptibleCommand(
                                new SequentialCommandGroup(
                                        new InstantCommand(() -> logger.info("Operator A pressed: Starting UNINTERRUPTIBLE SetDeposit to SPECIMEN_INTAKE (back)")),
                                        new SetDeposit(robot, DepositPivotState.SPECIMEN_INTAKE, 0, false).withTimeout(1500)
                                )
                        ),
                        () -> frontSpecimenScoring
                )
        );

        operator.getGamepadButton(GamepadKeys.Button.Y).whenPressed(
                new ConditionalCommand(
                        new SequentialCommandGroup(
                                new InstantCommand(() -> logger.info("Operator Y pressed: Starting UNINTERRUPTIBLE attachSpecimen")),
                                new attachSpecimen(robot.deposit)
                        ),
                        new InstantCommand(() -> logger.warn("Operator Y pressed: Cannot execute attachSpecimen because not in SPECIMEN_SCORING")),
                        () -> depositPivotState.equals(DepositPivotState.SPECIMEN_SCORING)
                )
        );

        operator.getGamepadButton(GamepadKeys.Button.DPAD_UP).whenPressed(
                new UninterruptibleCommand(
                        new SequentialCommandGroup(
                                new InstantCommand(() -> logger.info("Operator Dpad Up pressed: Starting UNINTERRUPTIBLE SetDeposit to SCORING (HIGH_BUCKET_HEIGHT)")),
                                new SetDeposit(robot, DepositPivotState.SCORING, HIGH_BUCKET_HEIGHT, false).withTimeout(1500)
                        )
                )
        );

        operator.getGamepadButton(GamepadKeys.Button.DPAD_DOWN).whenPressed(
                new UninterruptibleCommand(
                        new SequentialCommandGroup(
                                new InstantCommand(() -> logger.info("Operator Dpad Down pressed: Starting UNINTERRUPTIBLE SetDeposit to SCORING (LOW_BUCKET_HEIGHT)")),
                                new SetDeposit(robot, DepositPivotState.SCORING, LOW_BUCKET_HEIGHT, false).withTimeout(1500)
                        )
                )
        );

        operator.getGamepadButton(GamepadKeys.Button.LEFT_STICK_BUTTON).whenPressed(
                new UninterruptibleCommand(
                        new SequentialCommandGroup(
                                new InstantCommand(() -> logger.info("Operator Left Stick pressed: Starting UNINTERRUPTIBLE SetDeposit to MIDDLE_HOLD")),
                                new SetDeposit(robot, DepositPivotState.MIDDLE_HOLD, 0, true).withTimeout(1500)
                        )
                )
        );

        operator.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER).whenPressed(
                new InstantCommand(() -> {
                    logger.info("Operator Left Bumper pressed: Closing Claw");
                    robot.deposit.setClawOpen(false);
                })
        );

        operator.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER).whenPressed(
                new InstantCommand(() -> {
                    logger.info("Operator Right Bumper pressed: Opening Claw");
                    robot.deposit.setClawOpen(true);
                })
        );
        logger.info("All buttons bound");

        super.run();
        logger.info("Super.run() from CommandOpMode called");
    }

    @Override
    public void run() {
        // Keep all the has movement init for until when TeleOp starts
        // This is like the init but when the program is actually started
        if (timer == null) {
            robot.initHasMovement();
            logger.info("HasMovement initialized");

            INTAKE_HOLD_SPEED = 0.15; // Enable hold
            logger.info("Intake hold speed set to " + INTAKE_HOLD_SPEED);

            timer = new ElapsedTime();
            gameTimer = new ElapsedTime();
            logger.info("Timers initialized");
        }
        // Endgame/hang rumble after 105 seconds to notify robot.driver to hang
        else if ((gameTimer.seconds() > 105) && (!endgame)) {
            endgame = true;
            logger.warn("Endgame sequence triggered - rumbling controllers!");
            gamepad1.rumble(500);
            gamepad2.rumble(500);
        }

        if (sampleColor.equals(SampleColorDetected.RED)) {
            gamepad1.setLedColor(1, 0, 0, Gamepad.LED_DURATION_CONTINUOUS);
        } else if (sampleColor.equals(SampleColorDetected.BLUE)) {
            gamepad1.setLedColor(0, 0, 1, Gamepad.LED_DURATION_CONTINUOUS);
        } else if (sampleColor.equals(SampleColorDetected.YELLOW)) {
            gamepad1.setLedColor(1, 1, 0, Gamepad.LED_DURATION_CONTINUOUS);
        } else {
            gamepad1.setLedColor(0, 0, 0, Gamepad.LED_DURATION_CONTINUOUS);
        }

        // purple is back (default) spec scoring, green is front spec scoring
        if (frontSpecimenScoring) {
            gamepad2.setLedColor(0, 1, 0, Gamepad.LED_DURATION_CONTINUOUS);
        } else {
            gamepad2.setLedColor(1, 0, 1, Gamepad.LED_DURATION_CONTINUOUS);
        }

        // Pinpoint Field Centric Code
        double speedMultiplier = 0.35 + (1 - 0.35) * gamepad1.left_trigger;
        robot.follower.setTeleOpMovementVectors(-gamepad1.left_stick_y * speedMultiplier, -gamepad1.left_stick_x * speedMultiplier, -gamepad1.right_stick_x * speedMultiplier, false);
        robot.follower.update();

        // Manual control of extendo
        if (gamepad1.right_trigger > 0.01 &&
                !depositPivotState.equals(DepositPivotState.TRANSFER) &&
                robot.intake.getExtendoScaledPosition() <= (MAX_EXTENDO_EXTENSION - 5)) {

            robot.intake.target += 5;
        }

        // DO NOT REMOVE! Runs FTCLib Command Scheduler
        super.run();

        telemetryData.addData("timer", timer.milliseconds());
        telemetryData.addData("autoEndPose", autoEndPose.toString());
        telemetryData.addData("extendoReached", robot.intake.extendoReached);
        telemetryData.addData("extendoRetracted", robot.intake.extendoRetracted);
        telemetryData.addData("slidesRetracted", robot.deposit.slidesRetracted);
        telemetryData.addData("slidesReached", robot.deposit.slidesReached);
        telemetryData.addData("opModeType", opModeType.name());

        telemetryData.addData("hasSample()", robot.intake.hasSample());
        telemetryData.addData("colorSensor getDistance", robot.colorSensor.getDistance(DistanceUnit.CM));
        telemetryData.addData("Intake sampleColor", Intake.sampleColor);
        telemetryData.addData("correctSampleDetected", Intake.correctSampleDetected());
        telemetryData.addData("intakeMotorState", Intake.intakeMotorState);

        telemetryData.addData("liftTop.getPower()", robot.liftTop.getPower());
        telemetryData.addData("liftBottom.getPower()", robot.liftBottom.getPower());
        telemetryData.addData("extension.getPower()", robot.extension.getPower());

        telemetryData.addData("getExtendoScaledPosition()", robot.intake.getExtendoScaledPosition());
        telemetryData.addData("getLiftScaledPosition()", robot.deposit.getLiftScaledPosition());

        telemetryData.addData("slides target", robot.deposit.target);
        telemetryData.addData("extendo target", robot.intake.target);

        telemetryData.addData("intakePivotState", intakePivotState);
        telemetryData.addData("depositPivotState", depositPivotState);
        telemetryData.addData("Sigma", "Oscar");

        telemetryData.update(); // DO NOT REMOVE! Needed for telemetry
        timer.reset();
        // DO NOT REMOVE! Removing this will return stale data since bulk caching is on Manual mode
        // Also only clearing the control hub to decrease loop times
        // This means if we start reading both hubs (which we aren't) we need to clear both
        robot.ControlHub.clearBulkCache();
    }

    @Override
    public void end() {
        autoEndPose = robot.follower.getPose();
        logger.info("TeleOp ended");
    }
}
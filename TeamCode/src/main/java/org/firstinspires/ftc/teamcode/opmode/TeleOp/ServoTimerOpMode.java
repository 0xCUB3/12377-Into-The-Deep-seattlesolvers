package org.firstinspires.ftc.teamcode.opmode.TeleOp;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Config
@TeleOp(name = "Servo Timer OpMode")
public class ServoTimerOpMode extends OpMode {

    // Declare servos
    private Servo leftIntake;
    private Servo rightIntake;
    private Servo leftDeposit;
    private Servo rightDeposit;
    private Servo claw;
    private Servo sweeper;

    // Configurable servo positions
    public static double INTAKE_POSITION = 0.5;
    public static double DEPOSIT_POSITION = 0.5;
    public static double CLAW_POSITION = 0.5;
    public static double SWEEPER_POSITION = 0.5;


    // Elapsed time for opmode
    private ElapsedTime elapsedTime;

    @Override
    public void init() {
        // Initialize hardware
        leftIntake = hardwareMap.get(Servo.class, "leftIntakePivot");
        rightIntake = hardwareMap.get(Servo.class, "rightIntakePivot");
        leftDeposit = hardwareMap.get(Servo.class, "leftDepositPivot");
        rightDeposit = hardwareMap.get(Servo.class, "rightDepositPivot");
        claw = hardwareMap.get(Servo.class, "depositClaw");
        sweeper = hardwareMap.get(Servo.class, "subPusher");

        // Reverse necessary servos
        leftIntake.setDirection(Servo.Direction.REVERSE);
        leftDeposit.setDirection(Servo.Direction.REVERSE);

        // Initialize telemetry
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        // Initialize timer
        elapsedTime = new ElapsedTime();

        // Initialize servos to default positions (these will be immediately overwritten by the dashboard values in loop())
        leftIntake.setPosition(INTAKE_POSITION);
        rightIntake.setPosition(INTAKE_POSITION);
        leftDeposit.setPosition(DEPOSIT_POSITION);
        rightDeposit.setPosition(DEPOSIT_POSITION);
        claw.setPosition(CLAW_POSITION);
        sweeper.setPosition(SWEEPER_POSITION);


        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        // Update servo positions based on configurable values
        leftIntake.setPosition(INTAKE_POSITION);
        rightIntake.setPosition(INTAKE_POSITION); // Intakes move together
        leftDeposit.setPosition(DEPOSIT_POSITION);
        rightDeposit.setPosition(DEPOSIT_POSITION); // Deposits move together
        claw.setPosition(CLAW_POSITION);
        sweeper.setPosition(SWEEPER_POSITION);

        // Display timer value and servo positions
        telemetry.addData("Elapsed Time", elapsedTime.seconds());
        telemetry.addData("Left Intake Position", leftIntake.getPosition());
        telemetry.addData("Right Intake Position", rightIntake.getPosition());
        telemetry.addData("Left Deposit Position", leftDeposit.getPosition());
        telemetry.addData("Right Deposit Position", rightDeposit.getPosition());
        telemetry.addData("Claw Position", claw.getPosition());
        telemetry.addData("Sweeper Position", sweeper.getPosition());
        telemetry.update();
    }

    @Override
    public void start() {
        // Reset timer when opmode starts
        elapsedTime.reset();
    }
}
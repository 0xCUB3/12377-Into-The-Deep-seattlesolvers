package org.firstinspires.ftc.teamcode.opmode.TeleOp;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

@Config
@TeleOp(name = "Slides and Extendo Encoder Tuner")
public class SlidesAndExtendoEncoderTuner extends OpMode {

    private DcMotorEx liftTop;
    private DcMotorEx liftBottom;
    private DcMotorEx extension;

    private Motor.Encoder liftEncoder;
    private Motor.Encoder extensionEncoder;

    public static int LIFT_TOP_TARGET = 0;
    public static int LIFT_BOTTOM_TARGET = 0;
    public static int EXTENSION_TARGET = 0;
    public static boolean USE_POSITION_CONTROL = true; // Toggle for run to position
    public static double MANUAL_LIFT_POWER = 0.5;
    public static double MANUAL_EXTENDO_POWER = 0.5;


    private ElapsedTime elapsedTime;

    @Override
    public void init() {
        // --- Lift Initialization ---
        try {
            liftTop = hardwareMap.get(DcMotorEx.class, "liftTop");
            liftTop.setDirection(DcMotorSimple.Direction.REVERSE);
            liftTop.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            liftTop.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); // Reset encoder on init
            liftTop.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);      // Start without encoder

        } catch (Exception e) {
            telemetry.addLine("liftTop not found");
        }

        try {
            liftBottom = hardwareMap.get(DcMotorEx.class, "liftBottom");
            liftBottom.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            liftBottom.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            liftBottom.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        } catch (Exception e) {
            telemetry.addLine("liftBottom not found");
        }

        try {
            if (liftTop != null) {
                liftEncoder = new Motor(hardwareMap, "liftTop").encoder; // Get the SolversLib encoder
                liftEncoder.setDirection(Motor.Direction.REVERSE);    // Match motor direction.
                liftEncoder.reset(); // Reset the encoder
            }
        } catch (Exception e) {
            telemetry.addLine("liftEncoder not found or error creating");
        }

        // --- Extension Initialization ---
        try{
            extension = hardwareMap.get(DcMotorEx.class, "extension");
            extension.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            extension.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            extension.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }catch (Exception e){
            telemetry.addLine("extension not found");
        }

        try{
            if(extension != null){
                extensionEncoder = new Motor(hardwareMap, "extension").encoder;
                extensionEncoder.setDirection(Motor.Direction.REVERSE);
                extensionEncoder.reset();
            }
        }catch (Exception e){
            telemetry.addLine("extensionEncoder not found");
        }



        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        elapsedTime = new ElapsedTime();

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {

        if (USE_POSITION_CONTROL) {
            // Run to position mode
            if (liftTop != null) {
                liftTop.setTargetPosition(LIFT_TOP_TARGET);
                liftTop.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                liftTop.setPower(0.8);  // Constant power while moving to position
            }
            if (liftBottom != null) {
                liftBottom.setTargetPosition(LIFT_BOTTOM_TARGET);
                liftBottom.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                liftBottom.setPower(0.8);   // Constant power
            }

            if(extension != null){
                extension.setTargetPosition(EXTENSION_TARGET);
                extension.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                extension.setPower(0.8);
            }

        } else {
            // Manual Mode. Gamepad controls the slides and extendo
            double liftPower = -gamepad1.left_stick_y * MANUAL_LIFT_POWER; //scale power
            double extensionPower = gamepad1.right_stick_y * MANUAL_EXTENDO_POWER;

            if (liftTop != null) {
                liftTop.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                liftTop.setPower(liftPower);
            }
            if (liftBottom != null) {
                liftBottom.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                liftBottom.setPower(liftPower);
            }

            if(extension != null){
                extension.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                extension.setPower(extensionPower);
            }
        }


        // Display encoder values and timer
        telemetry.addData("Elapsed Time", elapsedTime.seconds());
        telemetry.addData("Lift Top Encoder", (liftEncoder != null) ? liftEncoder.getPosition() : "N/A");
        telemetry.addData("Lift Top Target", LIFT_TOP_TARGET);
        telemetry.addData("Lift Bottom Encoder", (liftBottom != null) ? liftBottom.getCurrentPosition() : "N/A");
        telemetry.addData("Lift Bottom Target", LIFT_BOTTOM_TARGET);
        telemetry.addData("Extension Encoder", (extensionEncoder != null) ? extensionEncoder.getPosition() : "N/A");
        telemetry.addData("Extension Target", EXTENSION_TARGET);

        telemetry.addData("Lift Top Power", (liftTop != null) ? liftTop.getPower() : "N/A");
        telemetry.addData("Lift Bottom Power", (liftBottom != null) ? liftBottom.getPower() : "N/A");
        telemetry.addData("Extension Power", (extension != null) ? extension.getPower() : "N/A");

        telemetry.update();
    }

    @Override
    public void start() {
        elapsedTime.reset();
    }
}
package org.firstinspires.ftc.teamcode.opmode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.seattlesolvers.solverslib.hardware.motors.Motor;

@TeleOp(name = "Slides Test")
public class SlidesTest extends com.qualcomm.robotcore.eventloop.opmode.OpMode {

    private DcMotorEx liftTop;
    private DcMotorEx liftBottom;
    private DcMotorEx extension;

    private Motor.Encoder liftEncoder;
    private Motor.Encoder extensionEncoder;

    @Override
    public void init() {
        try {
            liftTop = hardwareMap.get(DcMotorEx.class, "liftTop");
            liftTop.setDirection(DcMotorSimple.Direction.REVERSE);
            liftTop.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        } catch (Exception e) {
            telemetry.addLine("liftTop not found");
        }

        try {
            liftBottom = hardwareMap.get(DcMotorEx.class, "liftBottom");
            liftBottom.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        } catch (Exception e) {
            telemetry.addLine("liftBottom not found");
        }

        try {
            extension = hardwareMap.get(DcMotorEx.class, "extension");
            extension.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        } catch (Exception e) {
            telemetry.addLine("extension not found");
        }

        try {
            if (liftTop != null) {
                liftEncoder = new Motor(hardwareMap, "liftTop").encoder;
                liftEncoder.setDirection(Motor.Direction.REVERSE);
            }
        } catch (Exception e) {
            telemetry.addLine("liftEncoder not found or error creating");
        }

        try {
            if (extension != null) {
                extensionEncoder = new Motor(hardwareMap, "extension").encoder;
                extensionEncoder.setDirection(Motor.Direction.REVERSE);
            }
        } catch (Exception e) {
            telemetry.addLine("extensionEncoder not found or error creating");
        }

        telemetry.update();
    }

    @Override
    public void loop() {
        double liftPower = gamepad1.left_stick_y;
        double extensionPower = gamepad1.right_stick_y;

        // Improved Position Holding (No Low Power!)
        if (Math.abs(liftPower) < 0.1 && liftTop != null && liftBottom != null) {
            liftTop.setTargetPosition(liftTop.getCurrentPosition());
            liftTop.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            liftBottom.setTargetPosition(liftBottom.getCurrentPosition());
            liftBottom.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            liftPower = 0; // no power needed.
        } else {
            if (liftTop != null) liftTop.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            if (liftBottom != null) liftBottom.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        if (Math.abs(extensionPower) < 0.1 && extension != null) {
            extension.setTargetPosition(extension.getCurrentPosition());
            extension.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            extensionPower = 0; // no power needed.
        } else {
            if (extension != null) extension.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        if (liftTop != null) liftTop.setPower(liftPower);
        if (liftBottom != null) liftBottom.setPower(liftPower);
        if (extension != null) extension.setPower(extensionPower);

        telemetry.addData("lift Encoder Position", (liftEncoder != null) ? liftEncoder.getPosition() : "N/A");
        telemetry.addData("extension Encoder Position", (extensionEncoder != null) ? extensionEncoder.getPosition() : "N/A");
        telemetry.addData("liftTop Power", (liftTop != null) ? liftTop.getPower() : "N/A");
        telemetry.addData("liftBottom Power", (liftBottom != null) ? liftBottom.getPower() : "N/A");
        telemetry.addData("extension Power", (extension != null) ? extension.getPower() : "N/A");
        telemetry.update();
    }
}
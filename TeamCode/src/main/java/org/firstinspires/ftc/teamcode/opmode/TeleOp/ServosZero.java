package org.firstinspires.ftc.teamcode.opmode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Servos Zero")
public class ServosZero extends com.qualcomm.robotcore.eventloop.opmode.OpMode {


    private Servo leftIntakePivot;
    private Servo rightIntakePivot;
    private Servo leftDepositPivot;
    private Servo rightDepositPivot;
    private Servo depositClaw;


    @Override
    public void init() {
        try {
            leftIntakePivot = hardwareMap.get(Servo.class, "leftIntakePivot");
        } catch (Exception e) {
            telemetry.addLine("leftIntakePivot not found");
        }
        try {
            rightIntakePivot = hardwareMap.get(Servo.class, "rightIntakePivot");
        } catch (Exception e) {
            telemetry.addLine("rightIntakePivot not found");
        }


        try{
            leftDepositPivot = hardwareMap.get(Servo.class, "leftDepositPivot");
        } catch (Exception e){
            telemetry.addLine("leftDepositPivot not found");
        }

        try {
            rightDepositPivot = hardwareMap.get(Servo.class, "rightDepositPivot");
        } catch (Exception e){
            telemetry.addLine("rightDepositPivot not found");
        }
        try{
            depositClaw = hardwareMap.get(Servo.class, "depositClaw");
        } catch (Exception e){
            telemetry.addLine("depositClaw not found");
        }




        if (leftIntakePivot != null) leftIntakePivot.setPosition(0);
        if (rightIntakePivot != null) rightIntakePivot.setPosition(0);
        if (leftDepositPivot != null) leftDepositPivot.setPosition(0);
        if (rightDepositPivot != null) rightDepositPivot.setPosition(0);
        if (depositClaw != null) depositClaw.setPosition(0);


        telemetry.addData("leftIntakePivot", (leftIntakePivot != null) ? leftIntakePivot.getPosition() : "N/A");
        telemetry.addData("rightIntakePivot", (rightIntakePivot != null) ? rightIntakePivot.getPosition() : "N/A");
        telemetry.addData("leftDepositPivot", (leftDepositPivot != null) ? leftDepositPivot.getPosition() : "N/A");
        telemetry.addData("rightDepositPivot", (rightDepositPivot != null) ? rightDepositPivot.getPosition() : "N/A");
        telemetry.addData("depositClaw", (depositClaw != null) ? depositClaw.getPosition() : "N/A");

        telemetry.update();
    }

    @Override
    public void loop() {
        // This OpMode doesn't do anything in the loop, as it's for initialization only.
    }
}
package org.firstinspires.ftc.teamcode.commandbase;

import static org.firstinspires.ftc.teamcode.hardware.Globals.*;
import static org.firstinspires.ftc.teamcode.hardware.Globals.DEPOSIT_PIVOT_SPECIMEN_INTAKE_POS;
import static org.firstinspires.ftc.teamcode.hardware.Globals.DEPOSIT_PIVOT_SPECIMEN_SCORING_POS;

import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.controller.PIDFController;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.hardware.Robot;

public class Deposit extends SubsystemBase {
    private final Robot robot = Robot.getInstance();
    private final int divideConstant = 30;
    private static final PIDFController slidePIDF = new PIDFController(0.0125,0,0.0002, 0.00016);

    // Between open and closed
    public boolean clawOpen;

    public double target;

    public boolean slidesReached;

    // Between retracted and extended
    public boolean slidesRetracted;

    public enum DepositPivotState {
        SCORING,
        SPECIMEN_SCORING,
        TRANSFER,
        MIDDLE_HOLD,
        SPECIMEN_INTAKE
    }
    public static DepositPivotState depositPivotState;

    public void init() {
        slidePIDF.setTolerance(12, 10);
        setSlideTarget(0);

        // OpMode specific initializations
        if (opModeType.equals(OpModeType.AUTO)) {
            if (depositInit.equals(DepositInit.BUCKET_SCORING)) {
                setPivot(DepositPivotState.SCORING);
            } else {
                setPivot(DepositPivotState.SPECIMEN_SCORING);
            }
            setClawOpen(false);

        } else if (opModeType.equals(OpModeType.TELEOP)) {
            setPivot(DepositPivotState.MIDDLE_HOLD);
            setClawOpen(true);
        }
    }

    public int getLiftScaledPosition() {
        return (robot.liftEncoder.getPosition() / divideConstant);
    }

    public void setSlideTarget(double target) {
        this.target = Range.clip(target, 0, MAX_SLIDES_EXTENSION);
        slidePIDF.setSetPoint(target);
    }

    public void autoUpdateSlides() {
        if (this.target == HIGH_SPECIMEN_ATTACH_HEIGHT && !slidesReached) {
            slidePIDF.setP(0.01);
        } else {
            slidePIDF.setP(0.007);
        }

        double power = slidePIDF.calculate(getLiftScaledPosition(), target);
        slidesReached = slidePIDF.atSetPoint()
                        || (target == 0 && getLiftScaledPosition() < 15)
                        || (getLiftScaledPosition() >= target && target == HIGH_BUCKET_HEIGHT)
                        || (target == SLIDES_PIVOT_READY_EXTENSION + 50 && getLiftScaledPosition() > SLIDES_PIVOT_READY_EXTENSION && getLiftScaledPosition() < SLIDES_PIVOT_READY_EXTENSION + 65);
        slidesRetracted = (target <= 0) && slidesReached;

        // Just make sure it gets to fully retracted if target is 0
        if (target == 0 && !slidesReached) {
            power -= 0.1;
        }

        if (slidesRetracted) {
            robot.liftTop.setPower(0);
            robot.liftBottom.setPower(0);
        } else {
            robot.liftTop.setPower(power);
            robot.liftBottom.setPower(power);
        }
    }

    public void setClawOpen(boolean open) {
        if (open) {
            robot.depositClaw.setPosition(DEPOSIT_CLAW_OPEN_POS);
        } else {
            robot.depositClaw.setPosition(DEPOSIT_CLAW_CLOSE_POS);
        }

        this.clawOpen = open;
    }

    public void setPivot(DepositPivotState depositPivotState) {
        switch (depositPivotState) {
            case SCORING:
                robot.leftDepositPivot.setPosition(DEPOSIT_PIVOT_SCORING_POS);
                robot.rightDepositPivot.setPosition(DEPOSIT_PIVOT_SCORING_POS);
                break;
            case SPECIMEN_SCORING:
                robot.leftDepositPivot.setPosition(DEPOSIT_PIVOT_SPECIMEN_SCORING_POS);
                robot.rightDepositPivot.setPosition(DEPOSIT_PIVOT_SPECIMEN_SCORING_POS);
                break;
            case TRANSFER:
                robot.leftDepositPivot.setPosition(DEPOSIT_PIVOT_TRANSFER_POS);
                robot.rightDepositPivot.setPosition(DEPOSIT_PIVOT_TRANSFER_POS);
                break;
            case SPECIMEN_INTAKE:
                robot.leftDepositPivot.setPosition(DEPOSIT_PIVOT_SPECIMEN_INTAKE_POS);
                robot.rightDepositPivot.setPosition(DEPOSIT_PIVOT_SPECIMEN_INTAKE_POS);
                break;
            case MIDDLE_HOLD:
                robot.leftDepositPivot.setPosition(DEPOSIT_PIVOT_MIDDLE_POS);
                robot.rightDepositPivot.setPosition(DEPOSIT_PIVOT_MIDDLE_POS);
                break;
        }

        Deposit.depositPivotState = depositPivotState;
    }

    @Override
    public void periodic() {
        autoUpdateSlides();
    }
}

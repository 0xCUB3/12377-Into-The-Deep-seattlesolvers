package org.firstinspires.ftc.teamcode.commandbase;

import static org.firstinspires.ftc.teamcode.hardware.Globals.*;

import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.teamcode.hardware.Robot;

public class Drive extends SubsystemBase {
    private final Robot robot = Robot.getInstance();

    public enum SubPusherState {
        IN,
        OUT,
        AUTO_PUSH
    }

    public static SubPusherState subPusherState = SubPusherState.IN;

    public void init() {
        setSubPusher(SubPusherState.IN);
    }
    public void setSubPusher(SubPusherState subPusherState) {
        switch (subPusherState) {
            case IN:
                robot.subPusher.setPosition(SUB_PUSHER_IN);
                break;
            case OUT:
                robot.subPusher.setPosition(SUB_PUSHER_OUT);
                break;
            case AUTO_PUSH:
                robot.subPusher.setPosition(SUB_PUSHER_AUTO);
                break;
        }

        Drive.subPusherState = subPusherState;
    }
}

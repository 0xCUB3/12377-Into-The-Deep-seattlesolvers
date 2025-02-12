package org.firstinspires.ftc.teamcode.hardware;


import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.localization.Pose;

import org.firstinspires.ftc.teamcode.commandbase.Deposit;

@Config
public class Globals {
    public enum OpModeType {
        AUTO,
        TELEOP
    }

    public enum AllianceColor {
        RED,
        BLUE
    }

    public enum PoseLocationName {
        BLUE_BUCKET,
        BLUE_OBSERVATION,
        RED_BUCKET,
        RED_OBSERVATION
    }

    public enum DepositInit {
        BUCKET_SCORING,
        SPECIMEN_SCORING
    }

    public static DepositInit depositInit;

    public static OpModeType opModeType;
    public static AllianceColor allianceColor;
    public static PoseLocationName poseLocationName;

    public static Pose subSample1 = new Pose(62.000, 93.700, Math.toRadians(90));
    public static Pose subSample2 = new Pose(62.000, 93.700, Math.toRadians(90));
    public static Pose autoEndPose = new Pose(0, 0, Math.toRadians(0));


    // Robot Width and Length (in inches)
    public static double ROBOT_WIDTH = 11.5;
    public static double ROBOT_LENGTH = 12.25;

    // Intake Motor
    public static double INTAKE_FORWARD_SPEED = 1.0;
    public static double INTAKE_REVERSE_SPEED = -0.5;
    public static double INTAKE_HOLD_SPEED = 0.15;
    public static int REVERSE_TIME_MS = 300;

    // Intake Color Sensor
    public static double MIN_DISTANCE_THRESHOLD = 0.95;
    public static double MAX_DISTANCE_THRESHOLD = 1.4;
    public static int YELLOW_THRESHOLD = 800;
    public static int RED_THRESHOLD = 0;
    public static int BLUE_THRESHOLD = 0;

    public static int YELLOW_EDGE_CASE_THRESHOLD = 1450;
    public static int RED_EDGE_CASE_THRESHOLD = 700;
    public static int BLUE_EDGE_CASE_THRESHOLD = 675;

    // Intake Pivot
    public static double INTAKE_PIVOT_TRANSFER_POS = 0.85;
    public static double INTAKE_PIVOT_READY_TRANSFER_POS = 0.25;
    public static double INTAKE_PIVOT_INTAKE_POS = 0.05;
    public static double INTAKE_PIVOT_READY_INTAKE_POS = 0.34;
    public static double INTAKE_PIVOT_HOVER_INTAKE_POS = 0.71;

    // Intake Extendo
    public static double MAX_EXTENDO_EXTENSION = 480;

    // Deposit Pivot
    public static double DEPOSIT_PIVOT_TRANSFER_POS = 0.82;
    public static double DEPOSIT_PIVOT_READY_TRANSFER_POS = 0.90;
    public static double DEPOSIT_PIVOT_MIDDLE_POS = 0.9;
    public static double DEPOSIT_PIVOT_AUTO_BAR_POS = 0.35;
    public static double DEPOSIT_PIVOT_SCORING_POS = 0.23;
    public static double DEPOSIT_PIVOT_SPECIMEN_INTAKE_POS = 0.27;
    public static double DEPOSIT_PIVOT_SPECIMEN_SCORING_POS = 0.23;
    public static double DEPOSIT_PIVOT_SPECIMEN_FRONT_INTAKE_POS = 0.03;
    public static double DEPOSIT_PIVOT_SPECIMEN_BACK_INTAKE_POS = 0.83;
    public static double DEPOSIT_PIVOT_SPECIMEN_FRONT_SCORING_POS = 0.20;
    public static double DEPOSIT_PIVOT_SPECIMEN_BACK_SCORING_POS = 0.71;

    // 0.84 sec/360° -> 0.828 sec/355° -> (gear ratio of 5:4) 1.035 sec/355° -> 1035 milliseconds/355°
    public static double DEPOSIT_PIVOT_MOVEMENT_TIME = 1035 + 200; // 200 milliseconds of buffer
    // 0.5 sec/360° -> 0.49 sec/355° -> 490 milliseconds/355°
    public static double INTAKE_PIVOT_MOVEMENT_TIME = 490 + 200; // 200 milliseconds of buffer

    // Deposit Claw
    public static double DEPOSIT_CLAW_OPEN_POS = 0.99;
    public static double DEPOSIT_CLAW_CLOSE_POS = 0.83;

    // Deposit Slides
    public static double MAX_SLIDES_EXTENSION = 2050;
    public static double SLIDES_PIVOT_READY_EXTENSION = 400;
    public static double LOW_BUCKET_HEIGHT = 20;
    public static double HIGH_BUCKET_HEIGHT = 45;
    public static double HIGH_SPECIMEN_HEIGHT = 900;
    public static double HIGH_SPECIMEN_ATTACH_HEIGHT = 1350;
    public static double AUTO_ASCENT_HEIGHT = 900;
    public static double ENDGAME_ASCENT_HEIGHT = 1150;

    // Sub Pusher / Sweeper Servo
    public static double SUB_PUSHER_OUT = 0.45;
    public static double SUB_PUSHER_IN = 0.0;
    public static double SUB_PUSHER_AUTO = 0.5;

    // command timeout
    public final static int MAX_COMMAND_RUN_TIME_MS = 3000;
}
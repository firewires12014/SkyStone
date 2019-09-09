package org.firstinspires.ftc.teamcode.teleops;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.subsytems.Mecanum;
import org.firstinspires.ftc.teamcode.util.Utility;

@TeleOp(name = "TeleOp", group = "bot")

public class MecanumTeleOp extends OpMode {

    DcMotor fr, fl, br, bl;
    DcMotor intakeArm, dumperArm, hangElevator, intake;

    Servo dumper;

    ColorSensor colorSensor;

    double frPower, flPower, brPower, blPower;

    final double SCALE_FACTOR = 255;
    final double TERMINAL_POSITION = 0.0;


    private Utility firebot = new Utility();

    @Override
    public void init(){
        fr = hardwareMap.dcMotor.get("frontRight");
        fl = hardwareMap.dcMotor.get("frontLeft");
        br = hardwareMap.dcMotor.get("backRight");
        bl = hardwareMap.dcMotor.get("backLeft");
        hangElevator = hardwareMap.dcMotor.get("hangElevator");
        intakeArm = hardwareMap.dcMotor.get("intakeArm");
        dumperArm = hardwareMap.dcMotor.get("dumperArm");
        intake = hardwareMap.dcMotor.get("intake");

        colorSensor = hardwareMap.colorSensor.get("colorSensor");

        dumper = hardwareMap.servo.get("dumper");

        //Set Position
        dumper.setPosition(TERMINAL_POSITION);
    }

    @Override
    public void start(){

    }

    @Override
    public void loop(){


        // hsvValues is an array that will hold the hue, saturation, and value information.
        float hsvValues[] = {0F, 0F, 0F};

        //Declaring and initializing gamepad controls

        //Misc Joystick intialization and conditioning
        //original configuration: db: 0 , off: 0.05 , gain: 0.9
        double gamepad2LeftY = firebot.joystick_conditioning(gamepad2.left_stick_y, 0, 0.05, 0.95);
        double gamepad2RightY = firebot.joystick_conditioning(gamepad2.right_stick_y, 0, 0.05, 0.95);

        //Testing servo buttons
        boolean gamepad1A = gamepad1.a;
        boolean gamepad1X = gamepad1.x;
        boolean gamepad1Y = gamepad1.y;
        boolean gamepad1B = gamepad1.b;
        boolean gamepad2A = gamepad2.a;

        //Trigger initialization and conditioning
        double gamepad1RightTrigger = gamepad1.right_trigger;
        double gamepad1LeftTrigger = gamepad1.left_trigger;
        double gamepad2RightTrigger = firebot.joystick_conditioning(gamepad2.right_trigger, 0, 0.05, 0.9);
        double gamepad2LeftTrigger = firebot.joystick_conditioning(gamepad2.left_trigger, 0, 0.05, 0.9);


        //D-Pad initialization
        boolean dpadUp = gamepad1.dpad_up; //Directional Pad: Up
        boolean dpadDown = gamepad1.dpad_down; //Directional Pad: Down
        boolean dpadLeft = gamepad1.dpad_left;
        boolean dpadRight = gamepad1.dpad_right;
        boolean dpadUp2 = gamepad2.dpad_up;
        boolean dpadDown2 = gamepad2.dpad_down;

        boolean rightBumper2 = gamepad2.right_bumper;
        boolean leftBumper2 = gamepad2.left_bumper;
        rightBumper2 = false;

        //Mecanum values
        double maxPower = .5; //Maximum power for power range
        double yMove = firebot.joystick_conditioning(gamepad1.left_stick_y, 0, 0.05, 0.9);
        double xMove = firebot.joystick_conditioning(gamepad1.left_stick_x, 0, 0.05, 0.9);
        double cMove = firebot.joystick_conditioning(gamepad1.right_stick_x, 0, 0.05, 0.9);
        double armPower  =  -gamepad2.left_stick_y * .249;
        double frontLeftPower; //Front Left motor power
        double frontRightPower = 0; //Front Right motor power
        double backLeftPower; //Back Left motor power
        double backRightPower = 0; //Back Right motor power

        // convert the RGB values to HSV values.
        // multiply by the SCALE_FACTOR.
        // then cast it back to int (SCALE_FACTOR is a double)
        Color.RGBToHSV((int) (colorSensor.red() * SCALE_FACTOR),
                (int) (colorSensor.green() * SCALE_FACTOR),
                (int) (colorSensor.blue() * SCALE_FACTOR),
                hsvValues);
        telemetry.addData("Hue", hsvValues[0]);

        //If statement to prevent power from being sent to the same motor from multiple sources
        if (!dpadUp && !dpadDown) {
            //Calculating Mecanum power
            frontLeftPower = yMove - xMove - cMove;
            frontRightPower = -yMove - xMove - cMove;
            backLeftPower = yMove + xMove - cMove;
            backRightPower = -yMove + xMove - cMove;


            //Limiting power values from -1 to 1 to conform to setPower() limits
            frontLeftPower = Range.clip(frontLeftPower, -maxPower, maxPower);
            frontRightPower = Range.clip(frontRightPower, -maxPower, maxPower);
            backLeftPower = Range.clip(backLeftPower, -maxPower, maxPower);
            backRightPower = Range.clip(backRightPower, -maxPower, maxPower);

            //Setting power to Mecanum drivetrain
            fl.setPower(-frontLeftPower);
            fr.setPower(-frontRightPower);
            bl.setPower(-backLeftPower);
            br.setPower(-backRightPower);


            /*((x - 1.5)^3 + 1.5(x - 1.5)^2) + 0.5) D(0 , 1.5] Max/n = 1.5 */
            //Alternative inputs active for drive

                //Strafe left
//            if(gamepad1LeftTrigger > 0){
//                robot.frontLeft.setPower(gamepad1LeftTrigger);
//                robot.frontRight.setPower(gamepad1LeftTrigger);
//                robot.backLeft.setPower(-gamepad1LeftTrigger);
//                robot.backRight.setPower(-gamepad1LeftTrigger);
//            }
//
//            //Strafe right
//             if(gamepad1RightTrigger > 0){
//
//                    robot.frontLeft.setPower(-gamepad1RightTrigger);
//                    robot.frontRight.setPower(-gamepad1RightTrigger);
//                    robot.backLeft.setPower(gamepad1RightTrigger);
//                    robot.backRight.setPower(gamepad1RightTrigger);
//
//            }

                //Full forward power
                if(dpadUp){
                    fl.setPower(-1);
                    fr.setPower(1);
                    bl.setPower(-1);
                    br.setPower(1);
                }

                //Full reverse power
                if (dpadDown){
                    fl.setPower(1);
                    fr.setPower(-1);
                    bl.setPower(1);
                    br.setPower(-1);
                }

                if(dpadLeft){
                    fl.setPower(1);
                    fr.setPower(1);
                    bl.setPower(-1);
                    br.setPower(-1);
                }

                if(dpadRight) {
                    fl.setPower(-1);
                    fr.setPower(-1);
                    bl.setPower(1);
                    br.setPower(1);
                }
            }
            //Drivetrain Debugging
//            if(gamepad1.a)
//            {
//                fr.setPower(0.4);
//            }
//
//            if(gamepad1.b)
//            {
//                fl.setPower(0.4);
//            }
//
//            if(gamepad1.y)
//            {
//                br.setPower(0.4);
//            }
//
//            if(gamepad1.x)
//            {
//                bl.setPower(0.4);
//            }
        //Servo Debugging
        if (gamepad1A)dumper.setPosition(.35);
        if (gamepad1B)dumper.setPosition(.825);
        if (gamepad1Y)dumper.setPosition(.75);
        if (gamepad1X)dumper.setPosition(1.0);
        if (gamepad2A)dumper.setPosition(0);

        // Send calculated power to arm
        if(hsvValues[0] > 180 && gamepad2LeftY > 0) {
            intakeArm.setPower(0);
        }

        else if(gamepad2LeftY > 0){
            intakeArm.setPower(armPower * .8);
        }

        else if(gamepad2LeftY < 0){
            intakeArm.setPower(armPower);
        }

        else{intakeArm.setPower(0);}

        dumperArm.setPower(gamepad2RightY * .5);

        if (gamepad1RightTrigger > 0){
            hangElevator.setPower(-gamepad1RightTrigger);

        }
        else {
            hangElevator.setPower(gamepad1LeftTrigger);
        }

        if (gamepad2RightTrigger > 0){
            intake.setPower(-gamepad2RightTrigger);

        }
        else {
            intake.setPower(gamepad2LeftTrigger);
        }

    }




    @Override
    public void stop(){

    }
}

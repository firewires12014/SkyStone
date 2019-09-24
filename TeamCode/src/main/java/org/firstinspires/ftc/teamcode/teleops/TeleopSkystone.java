package org.firstinspires.ftc.teamcode.teleops;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.util.Utility;

@TeleOp(name = "TeleOp Skystone", group = "bot")

public class MecanumTeleOp extends OpMode {

    DcMotor fr, fl, br, bl, intakeRight, intakeLeft;
    Servo leftServo, rightServo;

    double frPower, flPower, brPower, blPower;

    final double SCALE_FACTOR = 255;

    private Utility firebot = new Utility();

    @Override
    public void init() {
        fr = hardwareMap.dcMotor.get("frontRight");
        fl = hardwareMap.dcMotor.get("frontLeft");
        br = hardwareMap.dcMotor.get("backRight");
        bl = hardwareMap.dcMotor.get("backLeft");
        intakeRight = hardwareMap.dcMotor.get("intakeRight");
        intakeLeft = hardwareMap.dcMotor.get("intakeLeft");

        leftServo = hardwareMap.servo.get("leftServo");
        rightServo = hardwareMap.servo.get("rightServo");

        leftServo.setPosition(0.5);
        rightServo.setPosition(0.5);
    }

    @Override
    public void start(){

    }

    @Override
    public void loop(){
        //Declaring and initializing gamepad controls

        if (gamepad1.a) {
            leftServo.setPosition(1);
            rightServo.setPosition(1);
        }
        if (gamepad1.b) {
            leftServo.setPosition(0.5);
            rightServo.setPosition(0.6);
        }

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

        if (gamepad1.right_trigger > 0) {
            intakeRight.setPower(-gamepad1RightTrigger);
            intakeLeft.setPower(gamepad1RightTrigger);
        }
        if (gamepad1.left_trigger > 0) {
            intakeRight.setPower(gamepad1LeftTrigger);
            intakeLeft.setPower(-gamepad1LeftTrigger);
        }
        
        //Mecanum values
        double maxPower = 1; //Maximum power for power range
        double yMove = firebot.joystick_conditioning(gamepad1.left_stick_y, 0, 0.05, 0.9);
        double xMove = firebot.joystick_conditioning(gamepad1.left_stick_x, 0, 0.05, 0.9);
        double cMove = firebot.joystick_conditioning(gamepad1.right_stick_x, 0, 0.05, 0.9);
        double armPower  =  -gamepad2.left_stick_y * .249;
        double frontLeftPower; //Front Left motor power
        double frontRightPower = 0; //Front Right motor power
        double backLeftPower; //Back Left motor power
        double backRightPower = 0; //Back Right motor power

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
            fl.setPower(frontLeftPower);
            fr.setPower(-frontRightPower);
            bl.setPower(backLeftPower);
            br.setPower(-backRightPower);


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

    }




    @Override
    public void stop(){

    }
}

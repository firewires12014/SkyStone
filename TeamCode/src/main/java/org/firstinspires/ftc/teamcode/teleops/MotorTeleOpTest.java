package org.firstinspires.ftc.teamcode.teleops;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.util.Utility;

@TeleOp(name = "Motor Test", group = "bot")

public class MotorTeleOpTest extends OpMode {

    DcMotor intakeRight, intakeLeft;
    Servo leftServo, rightServo;

    double frPower, flPower, brPower, blPower;

    final double SCALE_FACTOR = 255;

    private Utility firebot = new Utility();

    @Override
    public void init(){
        intakeRight = hardwareMap.dcMotor.get("intakeRight");
        intakeLeft = hardwareMap.dcMotor.get("intakeLeft");
        leftServo = hardwareMap.servo.get("leftServo");
        rightServo = hardwareMap.servo.get("rightServo");

        leftServo.setPosition(0);
        rightServo.setPosition(0);
    }

    @Override
    public void start(){

    }

    @Override
    public void loop(){
        if (gamepad2.right_trigger > 0) {
            intakeRight.setPower(-gamepad2.right_trigger);
            intakeLeft.setPower(gamepad2.right_trigger);
        }
        if (gamepad2.left_trigger > 0) {
            intakeRight.setPower(gamepad2.left_trigger);
            intakeLeft.setPower(-gamepad2.left_trigger);
        }
        if (gamepad2.a) {
            leftServo.setPosition(1);
            rightServo.setPosition(1);
        }
        if (gamepad2.b) {
            leftServo.setPosition(0.5);
            rightServo.setPosition(0.6);
        }


    }




    @Override
    public void stop(){

    }
}

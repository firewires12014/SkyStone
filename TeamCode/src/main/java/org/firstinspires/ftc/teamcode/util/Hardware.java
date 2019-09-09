package org.firstinspires.ftc.teamcode.util;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Hardware
{
    /* Public OpMode members. */

    //Motors
    public DcMotor frontLeft, backLeft, frontRight, backRight, hangElevator, intakeArm, dumperArm = null;

    //Servos
    public Servo dumper;

    //Sensors
    ColorSensor colorSensor;

    /* local OpMode members. */
    HardwareMap hwMap           =  null;
    private ElapsedTime period  = new ElapsedTime();

    /* Constructor */
    public Hardware(){

    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        hwMap = ahwMap;

        //Motor hardware mapping
        frontRight = ahwMap.dcMotor.get("frontRight");
        frontLeft = ahwMap.dcMotor.get("frontLeft");
        backRight = ahwMap.dcMotor.get("backRight");
        backLeft = ahwMap.dcMotor.get("backLeft");
        intakeArm = ahwMap.dcMotor.get("intakeArm");
        dumperArm = ahwMap.dcMotor.get("dumperArm");
        colorSensor = ahwMap.colorSensor.get("colorSensor");
        hangElevator = ahwMap.dcMotor.get("hangElevator");
        dumper = ahwMap.servo.get("dumper");



        //Servo hardware mapping
        //servoIntake = hwMap.get(CRServo.class, "intake");

        //Sensor hardware mapping

        //Motor set powers
        frontLeft.setPower(0);
        backLeft.setPower(0);
        frontRight.setPower(0);
        backRight.setPower(0);
        hangElevator.setPower(0);
        dumperArm.setPower(0);
        intakeArm.setPower(0);

        //Continuous Servo set powers

        //Run motors without encoder
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        hangElevator.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        dumperArm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);



    }
}
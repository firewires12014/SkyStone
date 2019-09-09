package org.firstinspires.ftc.teamcode.teleops;

import android.graphics.Color;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.subsytems.Mecanum;
import org.firstinspires.ftc.teamcode.util.Utility;

@TeleOp(name = "FullSpeed Tele Op", group = "bot")

public class FullSpeedMec extends OpMode {

    DcMotor fr, fl, br, bl;
    DcMotor arm;

    ColorSensor colorSensor;

    double frPower, flPower, brPower, blPower;
    final double SCALE_FACTOR = 255;

    private int globalAngle;
    BNO055IMU imu;
    Orientation lastAngles = new Orientation();

    private Utility firebot = new Utility();

    @Override
    public void init(){
        fr = hardwareMap.dcMotor.get("frontRight");
        fl = hardwareMap.dcMotor.get("frontLeft");
        br = hardwareMap.dcMotor.get("backRight");
        bl = hardwareMap.dcMotor.get("backLeft");
        arm = hardwareMap.dcMotor.get("arm");
        colorSensor = hardwareMap.colorSensor.get("colorSensor");

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "AdafruitIMUCalibration.json";
        parameters.loggingEnabled = false;
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
    }

    @Override
    public void start(){

    }

    @Override
    public void loop(){

        // hsvValues is an array that will hold the hue, saturation, and value information.
        float hsvValues[] = {0F, 0F, 0F};

        //Declaring and initializing gamepad controls
        telemetry.addData("Current Angle", getHeading());

        //Joystick intialization and conditioning
        //original configuration: db: 0 , off: 0.05 , gain: 0.9
        double gamepad2LeftY = firebot.joystick_conditioning(gamepad2.left_stick_y, 0, 0.05, 0.95);
        double intakeAdjustPower = firebot.joystick_conditioning(gamepad2.right_stick_y, 0, 0.05, 0.95);

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
        double maxPower = .99; //Maximum power for power range
        double yMove = firebot.joystick_conditioning(gamepad1.left_stick_y, 0, 0.05, 0.9);
        double xMove = firebot.joystick_conditioning(gamepad1.left_stick_x, 0, 0.05, 0.9);
        double cMove = firebot.joystick_conditioning(gamepad1.right_stick_x, 0, 0.05, 0.9);
        double armPower  =  -gamepad2.left_stick_y * 1;
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
        //45 deg - 15
        //90 deg - 45 (43?)
        //


        if(gamepad1.a)
        {
            fr.setPower(0.4);
        }

        if(gamepad1.b)
        {
            fl.setPower(0.4);
        }

        if(gamepad1.y)
        {
            br.setPower(0.4);
        }

        if(gamepad1.x)
        {
            bl.setPower(0.4);
        }

        // Send calculated power to arm
        if(hsvValues[0] > 180 && gamepad2.left_stick_y > 0) {
            arm.setPower(0);
        }

        else if(gamepad2.left_stick_y > 0){
            arm.setPower(armPower * .8);
        }

        else if(gamepad2.left_stick_y < 0){
            arm.setPower(armPower);
        }

        else{arm.setPower(0);}


        telemetry.addData("Arm Power", armPower);
        telemetry.update();
    }


    private double getAngle() {
        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;
        if (deltaAngle < -180)
            deltaAngle += 360;
        else if (deltaAngle > 180)
            deltaAngle -= 360;
        globalAngle += deltaAngle;
        lastAngles = angles;
        return globalAngle;
    }

    public double getHeading(){
        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        return(angles.firstAngle+360)%360;

    }

    public double getError(double targetAngle){
        double angleError = 0;

        angleError = (targetAngle - getHeading());
        angleError -= (360 * Math.floor(0.5 + ((angleError)/360.0)));

        return angleError;
    }


    @Override
    public void stop(){

    }
}

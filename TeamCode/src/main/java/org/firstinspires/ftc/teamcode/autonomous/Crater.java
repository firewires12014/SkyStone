package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.util.Hardware;

import java.util.List;

@Autonomous(name = "Crater", group = "Autonomous")
//@Disabled
public class Crater extends LinearOpMode{

    private Hardware robot = new Hardware();
    private ElapsedTime runtime = new ElapsedTime();

    private static final double COUNTS_PER_MOTOR_REV = 1120;
    private static final double DRIVE_GEAR_REDUCTION = .51;
    private static final double WHEEL_DIAMETER_INCHES = 6;
    private static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.14159265358979323846264338327950288419716939937510582097494459230781640628620899862803482534211706798214808651328230664709384460955058223172535940812848111745028410270193852110555964462294895493038196442881097566593344612847564823378678316527120190914564856692346034861045432664821339360726024914127372458700660631558817488152092096282925409171536436789259036001133053054882046652138414695194151160943305727036575959195309218611738193261179310511854807446237996274956735188575272489122793818301194912983367336244065664308602139494639522473719070217986094370277053921717629317675238467481846766940513200056812714526356082778577134275778960917363717872146844090122495343014654958537105079227968925892354201995611212902196086403441815981362977477130996051870721134999999837297804995105973173281609631859502445945534690830264252230825334468503526193118817101000313783875288658753320838142061717766914730359825349042875546873115956286388235378759375195778185778053217122680661300192787661119590921642019893809525720106548586327886593615338182796823030195203530185296899577362259941389124972177528347913151557485724245415069595082953311686172785588907509838175463746493931925506040092770167113900984882401285836160356370766010471018194295559619894676783744944825537977472684710404753464620804668425906949129331367702898915210475216205696602405803815019351125338243003558764024749647326391419927260426992279678235478163600934172164121992458631503028618297455570674983850549458858692699569092721079750930295532116534498720275596023648066549911988183479775356636980742654252786255181841757467289097777279);

    private int globalAngle;
    BNO055IMU imu;
    Orientation lastAngles = new Orientation();

    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";


    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY = "AeWbHOX/////AAABmU9GHfAlN0CJgNed4l/4qrseJ0TsGVFEMRaWpMvpOi5s8CW0iiayYB5YkoDgiqFkJexDQsxfRIVpnA+iCCsrYqZXBTIu66lWASvyynGsattVV49V5Bp+BRuxywn0m6pnJRXFlwjnvgHR7xoUrRpE6Pwir0lIlpUIBJREYw9uMc6eTL3yedJstdgV40zwUOwPzwe++1GQ+34JISHpnIZ4xPca+uAtCPje1h3XeR1PP/HHk1/2tNhKz4XVYtYVq5+6ev/8Ca+D9t9j5wXSvi3FOSZmCPVICYO+vWGeEFzeWxmvC34mAPZoZfwGVcz4HYgdRl4tJiIC19VSuW+7iFX/7/GOI/TPFNnnz3EUJOTfFQiy";
    /**
     * #vuforia is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * #tfod is the variable we will use to store our instance of the Tensor Flow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    private String sampleLocation = "UNKNOWN";

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        robot.frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.hangElevator.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        /* Wait for the game to begin */
        waitForStart();

        resetStartTime();

        robot.frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.hangElevator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        /* Step 1: Lower Robot */
//        lowerRobot();

        if (opModeIsActive()) {
            /* Activate Tensor Flow Object Detection. */
            if (tfod != null) {
                tfod.activate();
                sleep(2000);
            }

            while (opModeIsActive()) {
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());
                        if (updatedRecognitions.size() == 3) {
                            int goldMineralX = -1;
                            int silverMineral1X = -1;
                            int silverMineral2X = -1;
                            for (Recognition recognition : updatedRecognitions) {
                                if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                    goldMineralX = (int) recognition.getLeft();
                                } else if (silverMineral1X == -1) {
                                    silverMineral1X = (int) recognition.getLeft();
                                } else {
                                    silverMineral2X = (int) recognition.getLeft();
                                }
                            }
                            if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
                                if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
                                    sampleLocation = "LEFT";
                                } else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
                                    sampleLocation = "RIGHT";
                                } else {
                                    sampleLocation = "CENTER";
                                }
                            }
                        }
                        telemetry.update();
                    }
                }
                /* Begin Auto Path Movement */
                /*  */
                /* Step 3. Turn the robot */
                encoderDrive(0.5, 12, -12, 4);

                /* Step 4. Square the robot up with the wall */
                encoderDrive(0.2, -6, -6, 4);

                /* Step 5. Drive Forward to position to sample */

                runtime.reset();
                /* Step 6. Sample the Mineral then return to position */
                switch (sampleLocation) {
                    case "UNKNOWN":
                    case "CENTER":
                        forward(.2, 9.5);
                        backward(.2, 4);
                        strafeLeft(.4, 5, 6);
                        turnLeft(.4, 6.1);
                        forward(.5, 3);
                        turnLeft(.4, 3.25);
                        strafeRight(.4, 8, 5);
                        forward(.5, 15);
                        //dumpMarker();
                        backward(.6, 15);
                        turnLeft(.4, 13.5);
                        forward(.4, 6);

                        break;

                    case "LEFT":
                        forward(.2, 5);
                        strafeLeft(.4, 4, 6);
                        forward(.3, 5);
                        backward(.3, 5);
                        turnLeft(.4, 6.1);
                        forward(.5, 2.75);
                        turnLeft(.4, 3.25);
                        strafeRight(.4, 9, 5);
                        forward(.6, 11);
                        //dumpMarker();
                        backward(.8, 16);
                        turnLeft(.4, 13.5);
                        break;

                    case "RIGHT":
                        forward(.2, 5);
                        strafeRight(.4, 5, 6);
                        forward(.3, 3.75);
                        backward(.3, 3.75);
                        turnLeft(.4, 6.5);
                        forward(.5, 10);
                        turnLeft(.4, 3.75);
                        strafeRight(.4, 8.5, 5);
                        forward(.6, 12);
                        //dumpMarker();
                        backward(.6, 15);
                        turnLeft(.4, 13.5);
                        forward(.4, 6.5);
                        break;
                }

                /* End */
                sleep(30000);
            }
        }

        if (tfod != null) {
            tfod.shutdown();
        }


    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }

    /*
     * Initialize the Tensor Flow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }


    /*
     * encoderDrive allows the robot to go at a certain speed for a certain distance based
     * on ticks of the encoder. This allows the robot to be very precise in it's movements
     *
     * @param speed
     * @param leftInches
     * @param rightInches
     * @param timeoutS
     */

    public void encoderDrive(double speed, double leftInches, double rightInches, double timeoutS) {

        int newFrontRightTarget;
        int newFrontLeftTarget;
        int newBackRightTarget;
        int newBackLeftTarget;
        robot.frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newFrontRightTarget = robot.frontRight.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
            newFrontLeftTarget = robot.frontLeft.getCurrentPosition() + (int) (-leftInches * COUNTS_PER_INCH);
            newBackRightTarget = robot.backRight.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
            newBackLeftTarget = robot.backLeft.getCurrentPosition() + (int) (-leftInches * COUNTS_PER_INCH);

            robot.frontRight.setTargetPosition(newFrontRightTarget);
            robot.frontLeft.setTargetPosition(newFrontLeftTarget);
            robot.backRight.setTargetPosition(newBackRightTarget);
            robot.backLeft.setTargetPosition(newBackLeftTarget);
            // Turn On RUN_TO_POSITION

            robot.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            robot.frontRight.setPower(Math.abs(speed));
            robot.frontLeft.setPower(Math.abs(speed));
            robot.backRight.setPower(Math.abs(speed));
            robot.backLeft.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (robot.frontRight.isBusy() && robot.frontLeft.isBusy() && robot.backRight.isBusy() && robot.backLeft.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Projected Position", "Running to %7d :%7d :%7d :%7d", newFrontRightTarget, newFrontLeftTarget,
                        newBackRightTarget, newBackLeftTarget);

                telemetry.addData("Current Position", "Running at %7d :%7d :%7d :%7d",
                        robot.frontRight.getCurrentPosition(),
                        robot.frontLeft.getCurrentPosition(),
                        robot.backRight.getCurrentPosition(),
                        robot.backLeft.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            robot.frontRight.setPower(0);
            robot.frontLeft.setPower(0);
            robot.backRight.setPower(0);
            robot.backLeft.setPower(0);


            // Turn off RUN_TO_POSITION
            robot.frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    /*
     * raiseRobot lowers the robots arm to it's starting position thus raising the robot in the air if it is hooked
     */
    public void raiseRobot() {
        encoderHang(0.8, 0, 8);
        sleep(200);
    }

    /*
     * lowerRobot raises the robots arm to its hanging position thus lowering the robot if it is hooked
     */
    public void lowerRobot() {
        encoderHang(0.5, -6850, 10);
        sleep(200);
    }


    /*
     * forward moves the robot forward at the prescribed speed for the prescribed distance
     *
     * @param spd
     * @param fwd
     */
    public void forward(double spd, double fwd) {
        encoderDrive(spd, -fwd, -fwd, 10.0);
        sleep(200);
    }

    /*
     * backward moves the robot backward at the prescribed speed for the prescribed distance
     *
     * @param spd
     * @param back
     */
    public void backward(double spd, double back) {
        encoderDrive(spd, back, back, 5.0);
        sleep(200);
    }

    /*
     * turnRight turns the robot right at the prescribed speed for the prescribed distance
     *
     * @param spd
     * @param turn
     */
    public void turnRight(double spd, double turn) {
        encoderDrive(spd, -turn, turn, 5.0);
        sleep(200);
    }

    /*
     * turnLeft turns the robot right at the prescribed speed for the prescribed distance
     *
     * @param spd
     * @param turn
     */
    public void turnLeft(double spd, double turn) {
        encoderDrive(spd, turn, -turn, 5.0);
        sleep(200);
    }
    //
//    public void strafeLeft(double spd, double strafe, double angle){
//        strafeDrive(spd, strafe, angle, 'L');
//
//    }
//
//    public void strafeRight(double spd, double strafe, double angle){
//        strafeDrive(spd, strafe, angle, 'R');
//    }
    public void strafeRight(double spd, double strafe, double timeoutS){
        strafeDrive(spd, strafe, strafe, -strafe, -strafe, timeoutS);

    }

    public void strafeLeft(double spd, double strafe, double timeoutS){
        strafeDrive(spd, -strafe, -strafe, strafe, strafe, timeoutS);
    }

    private void rotate(int degrees) {
        double flp, frp, blp, brp;


        resetAngle();
        if (degrees < 0) {   // turn right.

            flp = 0.2;
            frp = 0.2;
            blp = 0.2;
            brp = 0.2;
        }
        else if (degrees > 0) {   // turn left.

            flp = -0.2;
            frp = -0.2;
            blp = -0.2;
            brp = -0.2;
        }
        else return;
        robot.frontRight.setPower(frp);
        robot.frontLeft.setPower(flp);
        robot.backRight.setPower(brp);
        robot.backLeft.setPower(blp);

        if (degrees < 0) {//right
            while (opModeIsActive() && getHeading() == 0) {}
            while (opModeIsActive() && getHeading() > degrees) {}
        } else {//left
            while (opModeIsActive() && getHeading() < degrees) {}
        }
        robot.frontRight.setPower(0);
        robot.frontLeft.setPower(0);
        robot.backRight.setPower(0);
        robot.backLeft.setPower(0);
        sleep(1000);
        resetAngle();
    }

    private void resetAngle() {
        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        globalAngle = 0;
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
    public void strafeDrive(double speed, double frontLeftInches, double frontRightInches, double backLeftInches, double backRightInches, double timeoutS) {

        int newFrontRightTarget;
        int newFrontLeftTarget;
        int newBackRightTarget;
        int newBackLeftTarget;


        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newFrontRightTarget = robot.frontRight.getCurrentPosition() + (int) (frontRightInches * COUNTS_PER_INCH);
            newFrontLeftTarget = robot.frontLeft.getCurrentPosition() + (int) (frontLeftInches * COUNTS_PER_INCH);
            newBackRightTarget = robot.backRight.getCurrentPosition() + (int) (backRightInches * COUNTS_PER_INCH);
            newBackLeftTarget = robot.backLeft.getCurrentPosition() + (int) (backLeftInches * COUNTS_PER_INCH);

            robot.frontRight.setTargetPosition(newFrontRightTarget);
            robot.frontLeft.setTargetPosition(newFrontLeftTarget);
            robot.backRight.setTargetPosition(-newBackRightTarget);
            robot.backLeft.setTargetPosition(-newBackLeftTarget);
            // Turn On RUN_TO_POSITION
            robot.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            robot.frontRight.setPower(Math.abs(speed));
            robot.frontLeft.setPower(Math.abs(speed));
            robot.backRight.setPower(Math.abs(speed));
            robot.backLeft.setPower(Math.abs(speed));


            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (robot.frontRight.isBusy() && robot.frontLeft.isBusy() && robot.backRight.isBusy() && robot.backLeft.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Projected Position","Running to %7d :%7d :%7d :%7d", newFrontRightTarget, newFrontLeftTarget,
                        newBackRightTarget, newBackLeftTarget);
                telemetry.addData("Current Position","Running at %7d :%7d :%7d :%7d",
                        robot.frontRight.getCurrentPosition(),
                        robot.frontLeft.getCurrentPosition(),
                        robot.backRight.getCurrentPosition(),
                        robot.backLeft.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            robot.frontRight.setPower(0);
            robot.frontLeft.setPower(0);
            robot.backRight.setPower(0);
            robot.backLeft.setPower(0);

            // Turn off RUN_TO_POSITION
            robot.frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void encoderHang(double speed, int hangTarget, double timeoutS) {

        if (opModeIsActive()) {


            robot.hangElevator.setTargetPosition(hangTarget);

            robot.hangElevator.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            runtime.reset();
            robot.hangElevator.setPower(Math.abs(speed));

            while (opModeIsActive() && (runtime.seconds() < timeoutS) && robot.hangElevator.isBusy()) {

//                telemetry.addData("Path1", "Running to %7d", hangTarget);
//                telemetry.addData("Path2", "Running at %7d", robot.hangElevator.getCurrentPosition());
                telemetry.addData("Projected Position","Running to %7d", hangTarget);
                telemetry.addData("Current Position","Running at %7d", robot.hangElevator.getCurrentPosition());
                telemetry.update();

            }

            robot.hangElevator.setPower(0);

            robot.hangElevator.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

    }
}




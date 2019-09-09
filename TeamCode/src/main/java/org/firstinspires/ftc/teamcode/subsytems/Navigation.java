package org.firstinspires.ftc.teamcode.subsytems;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.HardwareMap;

public abstract class Navigation {
    public Navigation(HardwareMap map,String name){

    }

    public abstract double getHeading();

    public abstract double getError(double targetAngle);
}

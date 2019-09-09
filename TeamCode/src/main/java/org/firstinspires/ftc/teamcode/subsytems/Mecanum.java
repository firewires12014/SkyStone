package org.firstinspires.ftc.teamcode.subsytems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.List;

public class Mecanum {

    private List<DcMotor> motors;

    public Mecanum(List<DcMotor> motors){
        this.motors = motors;
    }

    private void setPowerAll(double powerFR, double powerFL, double powerBR, double powerBL){
        motors.get(0).setPower(powerFR);
        motors.get(1).setPower(powerFL);
        motors.get(2).setPower(powerBR);
        motors.get(3).setPower(powerBL);
    }

}

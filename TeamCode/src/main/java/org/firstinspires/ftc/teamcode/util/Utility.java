package org.firstinspires.ftc.teamcode.util;

public class Utility {

    /**
     *
     * Condition the joystick
     * @param x
     * @param db   - Deadband
     * @param off  - Offset
     * @param gain - Gain
     * @return0.
     *
     */
    public float joystick_conditioning(float x, float db, double off, double gain) {
        float output = 0;
        boolean sign = (x > 0);

        x = Math.abs(x);
        if (x > db) {
            output = (float) (off - ((off - 1) * Math.pow(((db - x) / (db - 1)), gain)));
            output *= sign ? 1 : -1;
        }
        return output;
    }

}

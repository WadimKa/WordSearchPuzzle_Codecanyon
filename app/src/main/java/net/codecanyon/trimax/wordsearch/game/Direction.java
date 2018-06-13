package net.codecanyon.trimax.wordsearch.game;


public enum Direction {
    EAST(0),
    NORTH_EAST(315),
    NORTH(270),
    NORTH_WEST(225),
    WEST(180),
    SOUTH_WEST(135),
    SOUTH(90),
    SOUTH_EAST(45);

    private static final float SNAP_COEFF = 0.785398f;
    private float degree;


    Direction(float angleDegree) {
        degree = angleDegree;
    }

    public static Direction getDirection(float radians) {

        if (radians <= (.5 * SNAP_COEFF) && radians >= -(.5 * SNAP_COEFF)) {
            return EAST;
        } else if (radians > (.5 * SNAP_COEFF) && radians < (1.5 * SNAP_COEFF)) {
            return NORTH_EAST;
        } else if (radians >= (1.5 * SNAP_COEFF) && radians <= (2.5 * SNAP_COEFF)) {
            return NORTH;
        } else if (radians > (2.5 * SNAP_COEFF) && radians < (3.5 * SNAP_COEFF)) {
            return NORTH_WEST;
        } else if (radians >= (3.5 * SNAP_COEFF) || radians <= -(3.5 * SNAP_COEFF)) {
            return WEST;
        } else if (radians < -(2.5 * SNAP_COEFF) && radians > -(3.5 * SNAP_COEFF)) {
            return SOUTH_WEST;
        } else if (radians <= -(1.5 * SNAP_COEFF) && radians >= -(2.5 * SNAP_COEFF)) {
            return SOUTH;
        } else {
            return SOUTH_EAST;
        }
    }

    public boolean isAngle() {
        return (this == NORTH_EAST || this == SOUTH_EAST || this == NORTH_WEST || this == SOUTH_WEST);
    }

    public boolean isLeft() {
        return (this == WEST || this == NORTH_WEST || this == SOUTH_WEST);
    }

    public boolean isUp() {
        return (this == NORTH || this == NORTH_WEST || this == NORTH_EAST);
    }

    public boolean isDown() {
        return (this == SOUTH || this == SOUTH_WEST || this == SOUTH_EAST);
    }

    public boolean isRight() {
        return (this == EAST || this == NORTH_EAST || this == SOUTH_EAST);
    }

    public float getAngleDegree() {
        return degree;
    }


}

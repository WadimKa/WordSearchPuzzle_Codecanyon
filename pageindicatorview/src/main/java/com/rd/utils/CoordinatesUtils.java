package com.rd.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.rd.animation.type.AnimationType;
import com.rd.draw.data.Indicator;
import com.rd.draw.data.Orientation;

public class CoordinatesUtils {

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static int getCoordinate(@Nullable Indicator indicator, int position) {
        if (indicator == null) {
            return 0;
        }

        if (indicator.getOrientation() == Orientation.HORIZONTAL) {
            return getXCoordinate(indicator, position);
        } else {
            return getYCoordinate(indicator, position);
        }
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static int getXCoordinate(@Nullable Indicator indicator, int position) {
        if (indicator == null) {
            return 0;
        }

        int coordinate;
        if (indicator.getOrientation() == Orientation.HORIZONTAL) {
            coordinate = getHorizontalCoordinate(indicator, position);
        } else {
            coordinate = getVerticalCoordinate(indicator);
        }

        coordinate += indicator.getPaddingLeft();
        return coordinate;
    }

    public static int getYCoordinate(@Nullable Indicator indicator, int position) {
        if (indicator == null) {
            return 0;
        }

        int coordinate;
        if (indicator.getOrientation() == Orientation.HORIZONTAL) {
            coordinate = getVerticalCoordinate(indicator);
        } else {
            coordinate = getHorizontalCoordinate(indicator, position);
        }

        coordinate += indicator.getPaddingTop();
        return coordinate;
    }

    private static int getHorizontalCoordinate(@NonNull Indicator indicator, int position) {
        int count = indicator.getCount();
        int radius = indicator.getRadius();
        int stroke = indicator.getStroke();
        int padding = indicator.getPadding();

        int coordinate = 0;
        for (int i = 0; i < count; i++) {
            coordinate += radius + (stroke / 2);

            if (position == i) {
                return coordinate;
            }

            coordinate += radius + padding + (stroke / 2);
        }

        if (indicator.getAnimationType() == AnimationType.DROP) {
            coordinate += radius * 2;
        }

        return coordinate;
    }

    private static int getVerticalCoordinate(@NonNull Indicator indicator) {
        int radius = indicator.getRadius();
        int coordinate;

        if (indicator.getAnimationType() == AnimationType.DROP) {
            coordinate = radius * 3;
        } else {
            coordinate = radius;
        }

        return coordinate;
    }

    public static Pair<Integer, Float> getProgress(@NonNull Indicator indicator, int position, float positionOffset, boolean isRtl) {
        int count = indicator.getCount();
        int selectedPosition = indicator.getSelectedPosition();

        if (isRtl) {
            position = (count - 1) - position;
        }


        if (position < 0) {
            position = 0;

        } else if (position > count - 1) {
            position = count - 1;
        }

        boolean isRightOverScrolled = position > selectedPosition;
        boolean isLeftOverScrolled;

        if (isRtl) {
            isLeftOverScrolled = position - 1 < selectedPosition;
        } else {
            isLeftOverScrolled = position + 1 < selectedPosition;
        }

        if (isRightOverScrolled || isLeftOverScrolled) {
            selectedPosition = position;
            indicator.setSelectedPosition(selectedPosition);
        }

        boolean slideToRightSide = selectedPosition == position && positionOffset != 0;
        int selectingPosition;
        float selectingProgress;

        if (slideToRightSide) {
            selectingPosition = isRtl ? position - 1 : position + 1;
            selectingProgress = positionOffset;

        } else {
            selectingPosition = position;
            selectingProgress = 1 - positionOffset;
        }

        if (selectingProgress > 1) {
            selectingProgress = 1;

        } else if (selectingProgress < 0) {
            selectingProgress = 0;
        }

        return new Pair<>(selectingPosition, selectingProgress);
    }
}

package com.example.demo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class Display {
    private Digit[] digits = new Digit[12];
    private Line minus;

    public void draw(Group root, int x, int y) {
        int z = 0;

        for (int i = 0; i < 12; i++) {
            digits[11 - i] = new Digit();
            digits[11 - i].draw(root, x + z, y);
            z += 28;
        }

        minus = drawNegative(root);
    }

    public void showNumber(String number) {
        reset();

        int dotPosFromEnd = showDot(number);

        number = number.replace(".", "");

        if (number.indexOf('-') != -1) {
            showNegative(number);
            number = number.replace("-", "");
        }

        showDigits(number);
        showRanking(number, dotPosFromEnd);
    }

    private int showDot(String number) {
        int dotPos = number.indexOf('.');

        if (dotPos == -1)
            dotPos = number.length() - 1;

        int dotPosFromEnd = number.length() - 1 - dotPos;

        digits[dotPosFromEnd].setDot(true);

        return dotPosFromEnd;
    }

    private void showNegative(String number) {
        if (number.length() == 13) {
            setNegative(true);
        } else {
            int minusPos = number.indexOf('-');
            int minusPosFromEnd = number.length() - minusPos - 1;
            digits[minusPosFromEnd].setNegative();
        }
    }

    private void showDigits(String number) {
        for (int i = 0; i < number.length(); i++) {
            int idxFromEnd = number.length() - 1 - i;
            digits[i].set(Integer.parseInt(number.charAt(idxFromEnd) + ""));
        }
    }

    private void showRanking(String number, int dotPosFromEnd) {
        for (int i = dotPosFromEnd, j = 0; i < number.length(); i++, j++)
            if (j == 3) {
                digits[i].setRank(true);
                j = 0;
            }
    }
    private void reset() {
        setNegative(false);

        for (int i = 0; i < 12; i++)
            digits[i].reset();
    }

    public void setVisible(boolean turnOn) {
        for (int i = 0; i < 12; i++)
            digits[i].setVisible(turnOn);

        minus.setVisible(turnOn);
    }

    private Line drawNegative(Group root) {
        Line line = new Line();
        line.setStartX(75);
        line.setStartY(90);
        line.setEndX(85);
        line.setEndY(90);
        line.setStrokeWidth(4);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        root.getChildren().add(line);
        return line;
    }

    private void setNegative(boolean turnOn) {
        minus.setStroke(turnOn ? Color.rgb(35, 37, 38): Color.rgb(175, 175, 175));
    }
}

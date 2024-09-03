package com.example.demo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class Digit {
    public final static boolean[][] cfgs = new boolean[][] {
            new boolean[] {true, true, true, true, true, true, false},
            new boolean[] {false, true, true, false, false, false, false},
            new boolean[] {true, true, false, true, true, false, true},
            new boolean[] {true, true, true, true, false, false, true},
            new boolean[] {false, true, true, false, false, true, true},
            new boolean[] {true, false, true, true, false, true, true},
            new boolean[] {true, false, true, true, true, true, true},
            new boolean[] {true, true, true, false, false, false, false},
            new boolean[] {true, true, true, true, true, true, true},
            new boolean[] {true, true, true, true, false, true, true},
            new boolean[] {false, false, false, false, false, false, false},
    };

    public final static boolean[] minusCfg = new boolean[] {false, false, false, false, false, false, true};
    private Line[] segments = new Line[7];
    private Line dot;
    private Line rank;

    public void setNegative() {
        set(minusCfg);
    }

    public void setVisible(boolean turnOn) {
        dot.setVisible(turnOn);

        for(int i = 0; i < 7; i++)
            segments[i].setVisible(turnOn);
    }

    private Line addLine(Group root, double xS, double yS, double xE, double yE) {
        Line line = new Line();
        line.setStartX(xS);
        line.setStartY(yS);
        line.setEndX(xE);
        line.setEndY(yE);
        line.setStrokeWidth(4);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        root.getChildren().add(line);

        return line;
    }

    public void draw(Group root, int x, int y) {
        addDot(root, x + 25.25, y + 52.5,  x + 25.25, y + 52.5);
        addRank(root, x + 23, y - 1,  x + 24, y - 4);

        segments[0] = addLine(root,2 + x, y, 19 + x, y);
        segments[1] = addLine(root,21 + x, 2 + y, 21 + x, 21.75 + y);
        segments[2] = addLine(root,21 + x, 27.25 + y, 21 + x, 47.5 + y);
        segments[3] = addLine(root,2 + x, 50 + y, 19 + x, 50 + y);
        segments[4] = addLine(root, x, 27.25 + y, x, 47.5 + y);
        segments[5] = addLine(root,  x, 2 + y, x, 21.75 + y);
        segments[6] = addLine(root,3 + x, 25 + y, 18 + x, 25 + y);
    }

    private void switchSegment(Line seg, boolean turnOn) {
        seg.setStroke(turnOn ? Color.rgb(35, 37, 38): Color.rgb(175, 175, 175));
    }
    private void addDot(Group root, double xS, double yS, double xE, double yE) {
        dot = new Line();
        dot.setStartX(xS);
        dot.setStartY(yS);
        dot.setEndX(xE);
        dot.setEndY(yE);
        dot.setStrokeWidth(4);
        dot.setStrokeLineCap(StrokeLineCap.ROUND);
        root.getChildren().add(dot);
    }

    public void setDot(boolean turnOn) {
        dot.setStroke(turnOn ? Color.rgb(35, 37, 38): Color.rgb(175, 175, 175));
    }

    private void addRank(Group root, double xS, double yS, double xE, double yE) {
        rank = new Line();
        rank.setStartX(xS);
        rank.setStartY(yS);
        rank.setEndX(xE);
        rank.setEndY(yE);
        rank.setStrokeWidth(2);
        rank.setStrokeLineCap(StrokeLineCap.ROUND);
        root.getChildren().add(rank);
    }

    public void setRank(boolean turnOn) {
        rank.setStroke(turnOn ? Color.rgb(35, 37, 38) : Color.rgb(175, 175, 175));
    }

    public void set(int d) {
        set(cfgs[d]);
    }

    private void set(boolean[] cfgD) {
        for(int i = 0; i < 7; i++)
            switchSegment(segments[i], cfgD[i]);
    }

    public void reset() {
        setDot(false);
        setRank(false);
        set(cfgs[10]);
    }
}

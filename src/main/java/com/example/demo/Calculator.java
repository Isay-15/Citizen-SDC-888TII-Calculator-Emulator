package com.example.demo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static java.math.BigDecimal.*;

public class Calculator {

    private String displayedNum = "0";
    private BigDecimal register = null;

    BigDecimal HUNDRED = new BigDecimal(100);
    private BigDecimal memNum1 = new BigDecimal(0);
    private BigDecimal memNum2 = new BigDecimal(0);
    private String op;
    private boolean ceWasPressed;
    private boolean mrc2WasPressed;
    private boolean resWasPressed;
    private boolean error;
    private boolean result;

    private int muCnt = 0;
    private boolean muDiv;
    public enum Switcher1 {
        UP,
        MATH,
        CUT
    }
    public enum Switcher2 {
        AUTO,
        ZERO,
        TWO,
        THREE,
        FOUR,
        FLOAT
    }

    public Switcher1 switcher1;
    public Switcher2 switcher2;
    public static class Response {
        public boolean error;
        public boolean m1;
        public boolean m2;
        public String displayedNum;
    }

    private void rescaleNumber() {
        if (switcher2 == Switcher2.FLOAT && displayedNum.contains("."))
            displayedNum = getDisplayNum().stripTrailingZeros().toPlainString();

        if (switcher2 != Switcher2.FLOAT)
            displayedNum = getDisplayNum().setScale(getScale(), getRoundingMode()).toPlainString();
    }

    private int getScale() {
        return switch (switcher2) {
            case ZERO       -> 0;
            case TWO, AUTO  -> 2;
            case THREE      -> 3;
            case FOUR       -> 4;
            case FLOAT      -> 11;
        };
    }
    private RoundingMode getRoundingMode() {
        return switch (switcher1) {
            case UP   -> RoundingMode.UP;
            case MATH -> RoundingMode.HALF_UP;
            case CUT  -> RoundingMode.DOWN;
        };
    }
    private Response collectResponse() {
        Response response = new Response();
        response.displayedNum = displayedNum;
        response.error = error;
        response.m1 = memNum1.compareTo(ZERO) != 0;
        response.m2 = memNum2.compareTo(ZERO) != 0;

        return response;
    }

    private void checkWholePart() {
        int wholePart = displayedNum.indexOf(".");

        if (wholePart == -1)
            wholePart = displayedNum.length();

        if (displayedNum.contains("-"))
            wholePart--;

        if (wholePart > 12)
            error = true;
    }

    private void cutDispNum() {
        int fix = 0;

        if (displayedNum.contains("."))
            fix++;

        if (displayedNum.contains("-"))
            fix++;

        int digitsCnt = displayedNum.length() - fix;
        digitsCnt = Math.min(digitsCnt, 12);

        displayedNum = displayedNum.substring(0, digitsCnt + fix);
    }

    public Response procNumBtn(String name) {
        ceWasPressed = false;
        mrc2WasPressed = false;

        if (resWasPressed)
            procACBtn();

        String dispNum = displayedNum;

        if (result) {
            register = getDisplayNum();
            dispNum = "" + name.charAt(0);
            result = false;

        } else if (!error) {
            if (displayedNum.equals("0"))
                dispNum = "" + name.charAt(0); //00
            else
                dispNum += name;
        }

        setDisplayNum(dispNum, false);
        return collectResponse();
    }

    public Response procOpBtn(String name) {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;

        if (result) {
            op = name;

        } else if (!error) {
            procResBtn(name);
            op = name;
            result = true;
        }

        return collectResponse();
    }

    private boolean isNeedDivBy100(String name) {
        if (switcher2 != Switcher2.AUTO)
            return false;

        if (displayedNum.contains("."))
            return false;

        if (op == null)
            return name.equals("plus") || name.equals("minus") || name.equals("res");

        return op.equals("plus") || op.equals("minus");
    }

    private void fixEmptyRegister(String op) {

        switch (op) {
            case "plus", "inc" -> register = getDisplayNum();
            case "minus" -> {
                register = getDisplayNum();
                setDisplayNum(ZERO);
            }
            case "div" -> {
                register = getDisplayNum();
                setDisplayNum(ONE);
            }
        };

    }

    public Response procResBtn(String name) {
        ceWasPressed = false;
        mrc2WasPressed = false;

        boolean resButton = name.equals("res");

        if (resButton)
            resWasPressed = true;

        BigDecimal dDispNum = getDisplayNum();

        if (op == null || error) {
            if (isNeedDivBy100(name))
                dDispNum = divBy100(dDispNum);

            setDisplayNum(dDispNum, resButton);
            return collectResponse();
        }

        if (isNeedDivBy100(name))
            dDispNum = divBy100(dDispNum);

        if(register == null) {
            fixEmptyRegister(op);
            dDispNum = getDisplayNum();
        }
        
        BigDecimal left = result ? dDispNum : register;
        BigDecimal right = result ? register : dDispNum;

        if (op.equals("div") && right.compareTo(ZERO) == 0) {
            error = true;
            displayedNum = "0";
        } else {
            BigDecimal res = switch (op) {
                case "plus" -> left.add(right);
                case "minus" -> left.subtract(right);
                case "inc" -> left.multiply(right);
                case "div" -> left.divide(right, getScale(), getRoundingMode());
                default -> ZERO;
            };

            setDisplayNum(res, resButton);

            if (!result)
                register = dDispNum;

            result = true;
        }

        return collectResponse();
    }

    public Response procPercentBtn() {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;

        if (op == null || error || register == null)
            return collectResponse();

        BigDecimal dDispNum = getDisplayNum();

        BigDecimal left = result ? dDispNum : register;

        BigDecimal res = switch (op) {
            case "plus"  -> left.add(divBy100(register.multiply(dDispNum)));
            case "minus" -> left.subtract(divBy100(register.multiply(dDispNum)));
            case "inc"   -> left.multiply(divBy100(dDispNum));
            case "div"   -> left.divide(divBy100(dDispNum), getScale(), getRoundingMode());
            default -> ZERO;
        };

        setDisplayNum(res);

        if (!result)
            register = dDispNum;

        result = true;

        return collectResponse();
    }

    private BigDecimal divBy100(BigDecimal value) {
        return value.divide(HUNDRED, getScale(), getRoundingMode());
    }

    public Response procMuBtn() {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;

        if(register == null)
            return collectResponse();

        BigDecimal a = register;
        BigDecimal b = getDisplayNum();
        BigDecimal res = getDisplayNum();

        if (muCnt == 0)
            res = switch (op) {
                case "plus"  -> HUNDRED.multiply(a.add(b).divide(b, getScale(), getRoundingMode()));
                case "minus" -> ((a.subtract(b)).divide(b, getScale(), getRoundingMode())).multiply(HUNDRED);
                case "inc"   -> a.multiply(ONE.add(divBy100(b)));
                case "div"   -> a.divide(ONE.subtract(divBy100(b)), getScale(), getRoundingMode());
                default -> ZERO;
            };

        if (muCnt == 1 && muDiv)
            res = b.subtract(a);

        muCnt++;
        muDiv = "div".equals(op);
        op = null;
        result = true;
        setDisplayNum(res);
        return collectResponse();
    }

    public Response procCEBtn() {
        mrc2WasPressed = false;
        resWasPressed = false;

        if (ceWasPressed) {
            procACBtn();
        } else {
            displayedNum = "0";
            ceWasPressed = true;
        }

        muCnt = 0;
        error = false;
        return collectResponse();
    }

    public Response procACBtn() {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;
        displayedNum = "0";
        register = null;
        op = null;
        memNum1 = ZERO;
        memNum2 = ZERO;
        error = false;
        result = false;
        muCnt = 0;
        return collectResponse();
    }

    public Response procMemMRBtn() {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;
        setDisplayNum(memNum1);
        return collectResponse();
    }

    public Response procMemMCBtn() {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;
        memNum1 = ZERO;
        return collectResponse();
    }

    public Response procMemPlusBtn() {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;

        if (!error) {
            memNum1 = memNum1.add(getDisplayNum());
            result = true;
        }

        return collectResponse();
    }

    public Response procMemMinusBtn() {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;

        if (!error) {
            memNum1 = memNum1.subtract(getDisplayNum());
            result = true;
        }

        return collectResponse();
    }

    public Response procMem2Btn(String name) {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;
        BigDecimal displayState = getDisplayNum();

        if (!error) {
            switch (name) {
                case "MII+" -> memNum2 = memNum2.add(displayState);
                case "MII-" -> memNum2 = memNum2.subtract(displayState);
            }

            result = true;
        }

        return collectResponse();
    }

    public Response procMRC2Btn() {
        ceWasPressed = false;
        resWasPressed = false;

        if (mrc2WasPressed) {
            memNum2 = ZERO;
        } else {
            setDisplayNum(memNum2);
            mrc2WasPressed = true;
        }

        return collectResponse();
    }

    public Response procRootBtn() {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;

        if (error)
            return collectResponse();

        if (displayedNum.charAt(0) == '-') {
            procChangeSignBtn();
            error = true;
        }

        setDisplayNum(getDisplayNum().sqrt(new MathContext(64, getRoundingMode())));
        return collectResponse();
    }

    public Response procDotBtn() {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;

        if (result) {
            register = getDisplayNum();
            displayedNum = "0.";
            result = false;

        } else if (displayedNum.indexOf('.') == -1)
            displayedNum += '.';

        return collectResponse();
    }

    public Response procChangeSignBtn() {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;

        if (displayedNum.equals("0"))
            return collectResponse();

        if (!error)
            setDisplayNum(getDisplayNum().multiply(ONE.negate()));

        return collectResponse();
    }

    public Response procBackSpaceBtn() {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;

        if (error)
            error = false;
        else
            setDisplayNum(displayedNum.length() > 1 ? displayedNum.substring(0, displayedNum.length() - 1) : "0", false);

        return collectResponse();
    }

    public BigDecimal getDisplayNum() {
        return new BigDecimal(displayedNum);
    }
    private void setDisplayNum(String value, boolean rescale) {
        displayedNum = value;

        if (rescale)
            rescaleNumber();

        checkWholePart();
        cutDispNum();
    }

    private void setDisplayNum(BigDecimal value,  boolean rescale) {
        setDisplayNum(value.toPlainString(), rescale);
    }

    private void setDisplayNum(BigDecimal value) {
        setDisplayNum(value.toPlainString(), true);
    }
    public Response getDisplayState() {
        return collectResponse();
    }
}

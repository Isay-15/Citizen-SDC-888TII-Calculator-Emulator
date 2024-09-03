package com.example.demo;

import java.text.DecimalFormat;

public class Calculator {
    private String displayedNum = "0";
    private double register;
    private double memNum1 = 0;
    private double memNum2 = 0;
    private String op;
    private boolean ceWasPressed;
    private boolean mrc2WasPressed;
    private boolean error;
    private boolean result;
    private boolean resWasPressed;
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

    private void reformatNumber() {
        double dDispNum = getDisplayNum();
        double k = Math.pow(10, getKBySw2());

        dDispNum *= k;

        if(switcher2 == Switcher2.FLOAT)
            dDispNum = Math.floor(dDispNum);
        else
            dDispNum = switch (switcher1) {
                case UP   -> Math.ceil(dDispNum);
                case MATH -> Math.round(dDispNum);
                case CUT  -> Math.floor(dDispNum);
            };

        DecimalFormat df = new DecimalFormat(getPatternBySw2());
        displayedNum = df.format(dDispNum / k);
    }
    private void simplifyDisplayedNum() {
        reformatNumber();

        if(displayedNum.indexOf('.') == -1)
            return;

        int bound = displayedNum.indexOf('-') != -1 ? 14 : 13;

        if(displayedNum.length() < bound)
            return;

        if(displayedNum.indexOf('.') < bound)
            displayedNum = displayedNum.substring(0, bound);
    }

    private Response collectResponse() {
        Response response = new Response();
        response.displayedNum = displayedNum;
        response.error = error;
        response.m1 = memNum1 != 0;
        response.m2 = memNum2 != 0;

        return response;
    }

    private void checkNum() {
        int digitsCnt = displayedNum.length();

        if(displayedNum.indexOf('.') != -1) {
            digitsCnt = digitsCnt - 1;
            error = false;
        }

        if(displayedNum.indexOf('-') != -1)
            digitsCnt = digitsCnt - 1;

        if (digitsCnt <= 12)
            return;

        if(displayedNum.indexOf('.') == -1)
            error = true;

        displayedNum = displayedNum.substring(0, displayedNum.length() - (digitsCnt - 12));
    }

    public Response procNumBtn(String name) {
        ceWasPressed = false;
        mrc2WasPressed = false;

        if(resWasPressed)
            procACBtn();

        if(result) {
            register = getDisplayNum();
            displayedNum = "" + name.charAt(0);
            result = false;

        } else if (!error) {
            if(displayedNum.equals("0"))
                displayedNum = "" + name.charAt(0); //00
            else
                displayedNum += name;
        }

        checkNum();
        return collectResponse();
    }

    public Response procOpBtn(String name) {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;

        if(result) {
            op = name;
        } else if (!error) {
            procResBtn(name);
            op = name;
            result = true;
        }

        double dDispNum = getDisplayNum();

        if(switcher2 == Switcher2.AUTO && dDispNum % 1 == 0)
            if(op.equals("plus") || op.equals("minus"))
                displayedNum = new DecimalFormat(getPatternBySw2()).format(dDispNum / 100);

        return collectResponse();
    }

    public Response procResBtn(String name) {
        ceWasPressed = false;
        mrc2WasPressed = false;

        if(name.equals("res"))
            resWasPressed = true;

        double dDispNum = getDisplayNum();

        if(op == null || error) {
            if(switcher2 == Switcher2.AUTO)
                displayedNum = dDispNum / 100 + "";

            return collectResponse();
        }

        double left = result ? dDispNum : register;
        double right = result ? register : dDispNum;

        if(switcher2 == Switcher2.AUTO) {
            left /= 100;

            if(op.equals("plus") || op.equals("minus"))
                right /= 100;
        }

        if(op.equals("div") && right == 0) {
            error = true;
            displayedNum = "0";

        } else {
            double res = switch (op) {
                case "plus" -> left + right;
                case "minus" -> left - right;
                case "inc" -> left * right;
                case "div" -> left / right;
                default -> 0;
            };

            displayedNum = res + "";

            simplifyDisplayedNum();

            if (!result)
                register = dDispNum;

            result = true;
        }

        checkNum();
        return collectResponse();
    }

    public Response procPercentBtn() {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;

        if(op == null || error)
            return collectResponse();

        double dDispNum = getDisplayNum();

        double left = result ? dDispNum : register;

        double res = switch (op) {
            case "plus"  -> left + ((register * dDispNum) / 100);
            case "minus" -> left - ((register * dDispNum) / 100);
            case "inc"   -> left * (dDispNum / 100.0);
            case "div"   -> left / (dDispNum / 100.0);
            default -> 0;
        };

        displayedNum = res + "";
        simplifyDisplayedNum();

        if(!result)
            register = dDispNum;

        result = true;
        checkNum();
        return collectResponse();
    }

    public Response procMuBtn() {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;

        double a = register;
        double b = getDisplayNum();

        double res = getDisplayNum();

        if(muCnt == 0)
            switch (op) {
                case "plus" -> res = ((a + b) / b) * 100;
                case "minus" -> res = ((a - b) / b) * 100;
                case "inc" -> res = a * (1 + b / 100);
                case "div" -> res = a / (1 - b / 100);
            };

        if(muCnt == 1 && muDiv)
            res = b - a;

        muCnt++;
        muDiv = "div".equals(op);
        op = null;
        result = true;
        displayedNum = res + "";
        simplifyDisplayedNum();
        checkNum();
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
        register = 0;
        op = null;
        memNum1 = 0;
        memNum2 = 0;
        error = false;
        result = false;
        muCnt = 0;
        return collectResponse();
    }

    public Response procMemMRBtn() {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;
        displayedNum = "" + memNum1;
        simplifyDisplayedNum();
        return collectResponse();
    }

    public Response procMemMCBtn() {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;
        memNum1 = 0;
        return collectResponse();
    }

    public Response procMemPlusBtn() {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;

        memNum1 += getDisplayNum();
        result = true;

        return collectResponse();
    }

    public Response procMemMinusBtn() {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;
        memNum1 -= getDisplayNum();
        result = true;
        return collectResponse();
    }

    public Response procMem2Btn(String name) {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;

        double dispStateLast = Double.parseDouble(displayedNum);

        switch (name) {
            case "MII+" -> memNum2 += dispStateLast;
            case "MII-" -> memNum2 -= dispStateLast;
        }

        result = true;
        return collectResponse();
    }

    public Response procMRC2Btn() {
        ceWasPressed = false;
        resWasPressed = false;

        if (mrc2WasPressed) {
            memNum2 = 0;

        } else {
            displayedNum = "" + memNum2;
            simplifyDisplayedNum();
            mrc2WasPressed = true;
        }

        return collectResponse();
    }

    public Response procRootBtn() {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;
        double rootArg;

        if(error)
            return collectResponse();

        if(displayedNum.charAt(0) == '-') {
            procChangeSignBtn();
            rootArg = Double.parseDouble(displayedNum);
            error = true;

        } else {
            rootArg = getDisplayNum();
        }

        displayedNum = "" + Math.sqrt(rootArg);
        simplifyDisplayedNum();
        return collectResponse();
    }

    public Response procDotBtn() {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;

        if(result) {
            register = getDisplayNum();
            displayedNum = "0.";
            result = false;

        } else if(displayedNum.indexOf('.') == -1)
            displayedNum += '.';

        return collectResponse();
    }

    public Response procChangeSignBtn() {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;

        if(displayedNum.equals("0"))
            return collectResponse();

        if(!error) {
            displayedNum = "" + getDisplayNum() * (-1);
            simplifyDisplayedNum();
        }

        return collectResponse();
    }

    public Response procBackSpaceBtn() {
        ceWasPressed = false;
        mrc2WasPressed = false;
        resWasPressed = false;

        displayedNum = displayedNum.length() > 1 ? displayedNum.substring(0, displayedNum.length() - 1) : "0";

        return collectResponse();
    }

    private int getKBySw2() {
        return switch(switcher2) {
            case ZERO       -> 0;
            case TWO, AUTO  -> 2;
            case THREE      -> 3;
            case FOUR       -> 4;
            case FLOAT      -> 11;
        };
    }

    private String getPatternBySw2() {
        return switch(switcher2) {
            case ZERO      -> "0.";
            case TWO, AUTO -> "0.00";
            case THREE     -> "0.000";
            case FOUR      -> "0.0000";
            case FLOAT     -> "0.###########";
        };
    }

    public Double getDisplayNum() {
        return Double.parseDouble(displayedNum);
    }

    public Response getDisplayState() {
            return collectResponse();
    }
}

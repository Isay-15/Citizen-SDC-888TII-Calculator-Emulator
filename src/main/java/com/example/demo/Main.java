package com.example.demo;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class Main extends Application {
    private Group root;
    private Text error;
    private Text m1;
    private Text m2;
    private Calculator calculator;
    private Display display;

    public static void main(String[] args) {
        launch(args);
    }
    private void changeScale(ImageView imageView) {
        Image img = imageView.getImage();

        double h = img.heightProperty().doubleValue();
        double w = img.widthProperty().doubleValue();

        double k = 1.2;
        imageView.setFitHeight(h / k);
        imageView.setFitWidth(w / k);
    }

    private ImageView addImg(String name, double x, double y) {
        return addImg(name, x, y, "jpg");
    }

    private ImageView addImg(String name, double x, double y, String ext) {
        String path = getImgPath(name, ext);
        ImageView imageView = new ImageView(new Image(path));
        changeScale(imageView);
        imageView.setX(x);
        imageView.setY(y);

        root.getChildren().add(imageView);

        return imageView;
    }

    private String getImgPath(String name, String ext) {
        return "C:\\Lessons\\demo\\imgs\\" + name + "." + ext;
    }

    private void addAnimation(ImageView imageView, double y) {
        imageView.setOnMousePressed(mouseEvent -> imageView.setY(y + 3));
        imageView.setOnMouseReleased(mouseEvent -> imageView.setY(y));
    }

    private void makeBlink() {
        boolean errorVisible = error.isVisible();
        boolean m1Visible = m1.isVisible();
        boolean m2Visible = m2.isVisible();

        display.setVisible(false);
        error.setVisible(false);
        m1.setVisible(false);
        m2.setVisible(false);

        new Thread(() -> {
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            display.setVisible(true);

            error.setVisible(errorVisible);
            m1.setVisible(m1Visible);
            m2.setVisible(m2Visible);
        }).start();
    }

    private interface CalcMeth {
        Calculator.Response exec(String name);
    }

    private void addDisplay() {
        display = new Display();
        display.draw(root, 108, 60);
        displayResponse(calculator.getDisplayState());
    }

    private Text addIndicator(String txt, int x, int y) {
        Text indicator = new Text();
        indicator.setText(txt);
        indicator.setX(x);
        indicator.setY(y);
        indicator.setFont(Font.font(18));
        root.getChildren().add(indicator);

        return indicator;
    }

    private void addIndicators() {
        m1 = addIndicator("MI", 75, 69);
        m2 = addIndicator("MII", 75, 86);
        error = addIndicator("E", 75, 109);
    }

    private void displayResponse(Calculator.Response response) {
        display.showNumber(response.displayedNum);
        error.setVisible(response.error);
        m1.setVisible(response.m1);
        m2.setVisible(response.m2);
    }

    private void addSw1() {
        ImageView switcher = addImg("switcher_1", 49, 270, "png");
        addImg("cover_1", 10, 266, "png");

        switcher.setOnMouseDragOver(mouseEvent -> {
            double mX = mouseEvent.getX();
            if(57 < mX && mX <= 78)
                switcher.setX(mX - switcher.getFitWidth() / 2);
        });

        switcher.setOnMouseDragExited(mouseEvent -> {
            double mX = mouseEvent.getX();

            int swIdx = (int) Math.round((mX - 57) / 9.6);
            swIdx = Math.min(Math.max(swIdx, 0), 2);

            double swX = swIdx * 9.6 + 57;
            switcher.setX(swX - switcher.getFitWidth() / 2);

            calculator.switcher2 = Calculator.Switcher2.values()[swIdx];
        });

        switcher.setOnDragDetected(mouseEvent -> switcher.startFullDrag());
    }

    private void addSw2() {
        ImageView switcher = addImg("switcher_2", 415, 269, "png");
        addImg("cover_2", 320, 265, "png");

        switcher.setOnMouseDragOver(mouseEvent -> {
            double mX = mouseEvent.getX();
            if(392 < mX && mX <= 443)
                switcher.setX(mX - switcher.getFitWidth() / 2);
        });

        switcher.setOnMouseDragExited(mouseEvent -> {
            double mX = mouseEvent.getX();

            int swIdx = (int) Math.round((mX - 393) / 9.6);
            swIdx = Math.min(Math.max(swIdx, 0), 5);

            double swX = swIdx * 9.6 + 393;
            switcher.setX(swX - switcher.getFitWidth() / 2);

            calculator.switcher2 = Calculator.Switcher2.values()[swIdx];
        });

        switcher.setOnDragDetected(mouseEvent -> switcher.startFullDrag());
    }

    private void addSwitchers() {
        addSw1();
        addSw2();

        calculator.switcher1 = Calculator.Switcher1.CUT;
        calculator.switcher2 = Calculator.Switcher2.FLOAT;
    }
    private void addButton(String name, boolean useBlink, double x, double y, CalcMeth meth) {
        ImageView imageView = addImg(name, x, y);

        imageView.setOnMouseClicked(mouseEvent -> {
            displayResponse(meth.exec(name));

            if(useBlink)
                makeBlink();
        });

        addAnimation(imageView, y);
    }
    private void addButtons() {
        addButton("9", false,268, 378, n -> calculator.procNumBtn(n));
        addButton("8", false,199, 378, n -> calculator.procNumBtn(n));
        addButton("7", false,130, 378, n -> calculator.procNumBtn(n));
        addButton("6", false,268, 442, n -> calculator.procNumBtn(n));
        addButton("5", false,199, 442, n -> calculator.procNumBtn(n));
        addButton("4", false,130, 442, n -> calculator.procNumBtn(n));
        addButton("3", false,268, 504, n -> calculator.procNumBtn(n));
        addButton("2", false,199, 504, n -> calculator.procNumBtn(n));
        addButton("1", false,130, 504, n -> calculator.procNumBtn(n));
        addButton("0", false,120, 566, n -> calculator.procNumBtn(n));
        addButton("00", false,199, 566, n -> calculator.procNumBtn(n));

        addButton("plus", true,348, 504, n -> calculator.procOpBtn(n));
        addButton("minus", true,427, 442, n -> calculator.procOpBtn(n));
        addButton("inc", true,358, 442, n -> calculator.procOpBtn(n));
        addButton("div", true,358, 378, n -> calculator.procOpBtn(n));

        addButton("res", true,426, 504, n -> calculator.procResBtn(n));
        addButton("%", true,427, 378, n -> calculator.procPercentBtn());
        addButton("ON_AC", true,40, 504, n -> calculator.procACBtn());
        addButton("CE_C", true,40, 566, n -> calculator.procCEBtn());

        addButton("MR", true,130, 316, n -> calculator.procMemMRBtn());
        addButton("MC", true,40, 316, n -> calculator.procMemMCBtn());
        addButton("M+", true,269, 316, n -> calculator.procMemPlusBtn());
        addButton("M-", true,199, 316, n -> calculator.procMemMinusBtn());

        addButton("MIIRC", true,130, 268, n -> calculator.procMRC2Btn());
        addButton("MII+", true,269, 268, n -> calculator.procMem2Btn(n));
        addButton("MII-", true,199, 268, n -> calculator.procMem2Btn(n));

        addButton("root", true,358, 316, n -> calculator.procRootBtn());
        addButton("+-", true,40, 442, n -> calculator.procChangeSignBtn());
        addButton("dot", true,268, 566, n -> calculator.procDotBtn());
        addButton("00-0", false,40, 378, n -> calculator.procBackSpaceBtn());
        addButton("MU", true,427, 316, n -> calculator.procMuBtn());
    }

    @Override
    public void start(Stage stage) throws Exception {
        calculator = new Calculator();
        root = new Group();
        Scene scene = new Scene(root,515, 655); // window size
        stage.setTitle("Calculator");
        addImg("Calc", 0, 0);
        addIndicators();
        addDisplay();
        addButtons();
        addSwitchers();
        stage.setScene(scene);
        stage.show();
    }
}

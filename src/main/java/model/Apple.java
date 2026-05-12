package model;

import java.util.Random;

public class Apple {
    private int x;
    private int y;
    private final Random random = new Random();

    public Apple(int fieldWidth, int fieldHeight) {
        relocate(fieldWidth,fieldHeight);
    }

    public void relocate(int fieldWidth, int fieldHeight) {
        if (fieldWidth > 40 && fieldHeight > 40) {
            this.x = 20 + random.nextInt(fieldWidth - 40);
            this.y = 20 + random.nextInt(fieldHeight - 40);
        } else {
            this.x = 50; this.y = 50; //заглушка если окно не прогрузилось
        }
    }

    public int getX() {return x;}
    public int getY() {return y;}
}

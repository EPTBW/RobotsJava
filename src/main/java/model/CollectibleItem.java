package model;

import api.ICollectible;
import java.util.Random;

public class CollectibleItem implements ICollectible {
    private final Type type;
    private int x;
    private int y;
    private final Random random = new Random();

    public CollectibleItem(Type type, int fieldWidth, int fieldHeight) {
        this.type = type;
        relocate(fieldWidth, fieldHeight);
    }

    public void relocate(int fieldWidth, int fieldHeight) {
        if (fieldWidth > 40 && fieldHeight > 40) {
            this.x = 20 + random.nextInt(fieldWidth - 40);
            this.y = 20 + random.nextInt(fieldHeight - 40);
        } else {
            this.x = 50;
            this.y = 50;
        }
    }

    @Override public Type getType() { return type; }
    @Override public double getX() { return x; }
    @Override public double getY() { return y; }
}
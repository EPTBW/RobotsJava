package api;

public interface ICollectible {
    enum Type {
        APPLE,
        BATTERY,
        MEDKIT
    }

    Type getType();
    double getX();
    double getY();
}

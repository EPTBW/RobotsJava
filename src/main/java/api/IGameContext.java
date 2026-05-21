package api;
import java.util.List;

public interface IGameContext {
    int getAppleX();
    int getAppleY();
    List<IBullet> getBullets();
    int getFieldWidth();
    int getFieldHeight();
}


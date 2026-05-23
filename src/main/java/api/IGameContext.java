package api;
import java.util.List;

public interface IGameContext {

    List<ICollectible> getCollectibles();

    List<IBullet> getBullets();
    int getFieldWidth();
    int getFieldHeight();
}


package api;
import java.awt.Graphics2D;

public interface IRobotVisualizer {
    void draw(Graphics2D g, IRobotController controller);
}

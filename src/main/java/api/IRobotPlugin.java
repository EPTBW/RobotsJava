package api;

public interface IRobotPlugin {
    String getName();
    IRobotController getController();
    IRobotVisualizer getVisualizer();
}

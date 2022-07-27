/**
 * 
 */
package gui;

import gui.Constants.UserInput;
import generation.Maze;
import generation.Order.Builder;

/**
 * This is a default implementation of the State interface
 * with methods that do nothing but providing runtime exceptions
 * such that subclasses of this class can selectively override
 * those methods that are truly needed.
 *
 * @author Peter Kemper
 *
 */
public class DefaultState implements State {
	  
    private static final String MSG_UNIMPLEMENTED_METHOD = "DefaultState:using unimplemented method";

	@Override
    public void start(Controller controller, MazePanel panel) {
        throw new RuntimeException(MSG_UNIMPLEMENTED_METHOD);    
    }

    @Override
    public void setFileName(String filename) {
        throw new RuntimeException(MSG_UNIMPLEMENTED_METHOD);   
    }

    @Override
    public void setSkillLevel(int skillLevel) {
        throw new RuntimeException(MSG_UNIMPLEMENTED_METHOD);  
    }
    
    @Override
	public void setSeed(int seed) {
        throw new RuntimeException(MSG_UNIMPLEMENTED_METHOD);  
    }
    @Override
    public void setPerfect(boolean isPerfect) {
        throw new RuntimeException(MSG_UNIMPLEMENTED_METHOD); 
    }

    @Override
    public void setMazeConfiguration(Maze config) {
        throw new RuntimeException(MSG_UNIMPLEMENTED_METHOD);   
    }

    @Override
    public void setPathLength(int pathLength) {
        throw new RuntimeException(MSG_UNIMPLEMENTED_METHOD);   
    }

    @Override
    public boolean keyDown(UserInput key, int value) {
        throw new RuntimeException(MSG_UNIMPLEMENTED_METHOD);
    }

    @Override
    public void setBuilder(Builder dfs) {
        throw new RuntimeException(MSG_UNIMPLEMENTED_METHOD);
    }
}

package gui;

import generation.Maze;

public class Container {
    Maze maze;
    static Container container = new Container();

    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    public Maze getMaze() {
        return maze;
    }

    public static Container getInstance() {
        return container;
    }
}

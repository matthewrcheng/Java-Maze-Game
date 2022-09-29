# Java-Maze-Game

## Basics
This is a simple maze game that allows the user to choose from difficulty levels 0-9. It will then randomly generate a maze for the user to solve.

To run:
```command
cd src
javac gui/MazeApplication.java
java gui/MazeApplication
```

For extra customization, there are command line arguments that can be added to alter the game functionality. 
  -g specifies the build generating algorithm
    -"Prim", "Boruvka", or filename
  -d specifies the type of solving robot; omitting this argument will default to the user solving the maze
    -"Wizard" or "WallFollower"
  -r specifies the reliability for each of the four robot sensors (only if a robot has been specified)
    -"rrrr" would have four reliable sensors, "uuuu" would have four unreliable sensors
    -order of arguments is forward, left, right, backward
  -b specifies the starting battery level for the robot (again only if a robot has been specified)

To run with a Boruvka generator:
```command
java gui/MazeApplication -g "Boruvka"
```

To run with a Wizard solver, reliable front and right sensors, unreliable left and back sensors, and 1000 battery:
```command
java gui/MazeApplication -d "Wizard" -r ruru -b 1000
```

To run with an input xml file:
```command
java gui/MazeApplication -g ../bin/data/input.xml
```

## Robots
There are two types of robots included in this game, Wizard and Wall Follower. Both of these robots attempt to solve the maze on their own.

Wizard is smart and knows the most optimal path to the exit.
Wall Follower is basic and attempts to ride the wall, hoping to eventually find the exit.

Each robot has a battery level, with 3500 as the default. As the robot moves through the maze, it depletes its energy source. If the robot can reach the end of the maze, it will display the remaining battery, however, if it runs out of energy, you will lose the game.

## Sensors
Each robot is equipped with four sensors. A sensor allows the robot to see in the cardinal direction that it is facing, so that it can measure how much space it has to move. These sensors can be reliable or unreliable.

Reliable sensors will always 

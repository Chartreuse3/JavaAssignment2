// The main things that are changed from phase one to phase two are the new "time limit" of
// 10 moves, as well as a message that lets the user know what all of their options to choose from are.

import java.util.ArrayList;
import java.util.Scanner;

interface Lootable {
    String loot(Player player);
}

interface Interactable {
    String interact(Player player);
}

interface Exitable {
    String exit(Player player);
}

abstract class Room {
    String name;
    Room north, south, east, west, up, down;

    Room(String name) {
        this.name = name;
    }

    abstract String getDescription();

    Room getAdjoiningRoom(char direction) {
        if (direction == 'n') return north;
        if (direction == 's') return south;
        if (direction == 'e') return east;
        if (direction == 'w') return west;
        if (direction == 'u') return up;
        if (direction == 'd') return down;
        return null;
    }

    String getExits() {
        String exits = "";
        if (north != null) exits += "north ";
        if (south != null) exits += "south ";
        if (east != null) exits += "east ";
        if (west != null) exits += "west ";
        if (up != null) exits += "up ";
        if (down != null) exits += "down ";
        return exits.trim();
    }

    boolean isValidDirection(char direction) {
        return getAdjoiningRoom(direction) != null;
    }

    void setNorth(Room room) { north = room; }
    void setSouth(Room room) { south = room; }
    void setEast(Room room) { east = room; }
    void setWest(Room room) { west = room; }
    void setUp(Room room) { up = room; }
    void setDown(Room room) { down = room; }

    String getName() { return name; }
}

class Player {
    int score;
    ArrayList<String> inventory = new ArrayList<>();

    void addToInventory(String item) {
        inventory.add(item);
    }

    void addToScore(int points) {
        score += points;
    }

    String getInventory() {
        if (inventory.isEmpty()) {
            return "Inventory is empty";
        }
        return String.join(", ", inventory);
    }

    int getScore() {
        return score;
    }
}

class LootRoom extends Room implements Lootable {
    LootRoom(String name) {
        super(name);
    }

    public String loot(Player player) {
        player.addToInventory("Treasure");
        player.addToScore(100);
        return "You found treasure!";
    }

    String getDescription() {
        return "A room with treasure.";
    }
}

class InteractRoom extends Room implements Interactable {
    InteractRoom(String name) {
        super(name);
    }

    public String interact(Player player) {
        player.addToScore(50);
        return "You interacted with a mysterious object!";
    }

    String getDescription() {
        return "A room with a strange object.";
    }
}

class ExitRoom extends Room implements Exitable {
    ExitRoom(String name) {
        super(name);
    }

    public String exit(Player player) {
        return "You found the exit!";
    }

    String getDescription() {
        return "This room has the exit.";
    }
}

class Maze {
    Room currentRoom;
    Player player;
    boolean isFinished = false;
    // this is a new part that adds a limit to the amount of moves the user can make
    // before the maze collapses
    int moveLimit = 10;

    Maze() {
        player = new Player();
        Room lootRoom = new LootRoom("Loot Room");
        Room interactRoom = new InteractRoom("Interact Room");
        Room exitRoom = new ExitRoom("Exit Room");

        lootRoom.setEast(interactRoom);
        interactRoom.setWest(lootRoom);
        interactRoom.setEast(exitRoom);
        exitRoom.setWest(interactRoom);
        lootRoom.setSouth(exitRoom);
        exitRoom.setNorth(lootRoom);

        currentRoom = lootRoom;
    }

    String exitCurrentRoom() {
        if (currentRoom instanceof Exitable) {
            isFinished = true;
            return ((Exitable) currentRoom).exit(player);
        }
        return "This room is not exitable.";
    }

    String interactWithCurrentRoom() {
        if (currentRoom instanceof Interactable) {
            return ((Interactable) currentRoom).interact(player);
        }
        return "Nothing to interact with.";
    }

    String lootCurrentRoom() {
        if (currentRoom instanceof Lootable) {
            return ((Lootable) currentRoom).loot(player);
        }
        return "Nothing to loot here.";
    }

    boolean move(char direction) {
        if (currentRoom.isValidDirection(direction)) {
            currentRoom = currentRoom.getAdjoiningRoom(direction);
            return true;
        }
        return false;
    }

    int getPlayerScore() {
        return player.getScore();
    }

    String getPlayerInventory() {
        return player.getInventory();
    }

    String getCurrentRoomDescription() {
        return currentRoom.getDescription();
    }

    String getCurrentRoomExits() {
        return currentRoom.getExits();
    }

    boolean isFinished() {
        return isFinished;
    }
}

public class Main {
    public static void main(String[] args) {
        Maze maze = new Maze();
        Scanner scanner = new Scanner(System.in);

        // a new message that lets the user know how many possible moves they get
        System.out.println("The maze is falling apart. Escape as fast as you can! (You get 10 moves)");

        while (!maze.isFinished() && maze.moveLimit > 0) {
            System.out.println(maze.getCurrentRoomDescription());
            System.out.println("Exits: " + maze.getCurrentRoomExits());

            // new part shows players the options of what they can do
            System.out.println("\nCommands:");
            System.out.println("n/s/e/w/u/d - Move in a direction (north, south, east, west, up, down)");
            System.out.println("i - Interact with the room");
            System.out.println("l - Loot the room");
            System.out.println("x - Try to exit the room");
            System.out.println("v - Check your inventory");
            System.out.print("Enter command: ");

            char command = scanner.next().charAt(0);

            switch (command) {
                case 'n', 's', 'e', 'w', 'u', 'd' -> {
                    if (!maze.move(command)) {
                        System.out.println("You can't go that way.");
                    } else {
                        maze.moveLimit--; // Reduce move limit after each move
                    }
                }
                case 'i' -> System.out.println(maze.interactWithCurrentRoom());
                case 'l' -> System.out.println(maze.lootCurrentRoom());
                case 'x' -> System.out.println(maze.exitCurrentRoom());
                case 'v' -> System.out.println("Inventory: " + maze.getPlayerInventory());
                default -> System.out.println("Invalid command.");
            }
        }

        if (maze.isFinished()) {
            System.out.println("Congratulations! You escaped the maze.");
        } else {
            System.out.println("The maze collapsed! You couldn't escape in time.");
        }

        System.out.println("Game over. Your score: " + maze.getPlayerScore());
    }
}

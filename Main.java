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
        return switch (direction) {
            case 'n' -> north;
            case 's' -> south;
            case 'e' -> east;
            case 'w' -> west;
            case 'u' -> up;
            case 'd' -> down;
            default -> null;
        };
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
        return inventory.isEmpty() ? "Inventory is empty" : String.join(", ", inventory);
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
    Player player = new Player();
    int moveLimit;  // Now set dynamically based on difficulty
    boolean isFinished = false;

    Maze(int moveLimit) {
        this.moveLimit = moveLimit;  // Set the move limit based on selected difficulty

        Room lootRoom = new LootRoom("Loot Room");
        Room interactRoom = new InteractRoom("Interact Room");
        Room exitRoom = new ExitRoom("Exit Room");

        lootRoom.north = exitRoom;
        lootRoom.east = interactRoom;
        interactRoom.west = lootRoom;
        interactRoom.east = exitRoom;
        exitRoom.west = interactRoom;
        exitRoom.south = lootRoom;

        currentRoom = lootRoom;
    }

    boolean move(char direction) {
        Room nextRoom = currentRoom.getAdjoiningRoom(direction);
        if (nextRoom != null) {
            currentRoom = nextRoom;
            moveLimit--;  // Decrease move limit after each move
            return true;
        }
        return false;
    }

    void displayOptions() {
        System.out.println("\nCommands:");
        System.out.println("n/s/e/w/u/d - Move");
        System.out.println("i - Interact");
        System.out.println("l - Loot");
        System.out.println("x - Exit");
        System.out.println("v - Inventory");
        System.out.print("Enter command: ");
    }

    String interact() {
        return currentRoom instanceof Interactable ? ((Interactable) currentRoom).interact(player) : "Nothing to interact with.";
    }

    String loot() {
        return currentRoom instanceof Lootable ? ((Lootable) currentRoom).loot(player) : "Nothing to loot.";
    }

    String exitRoom() {
        if (currentRoom instanceof Exitable) {
            isFinished = true;
            return ((Exitable) currentRoom).exit(player);
        }
        return "This room is not exitable.";
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // adds a difficulty selector
        System.out.println("Select difficulty: Easy (15 moves) or Hard (10 moves)");
        System.out.print("Enter 'e' for Easy or 'h' for Hard: ");
        char difficulty = scanner.next().charAt(0);

        // sets the move limit based on the difficulty selected
        int moveLimit = (difficulty == 'e') ? 15 : 10;

        // creates the new maze with the selected move limit
        Maze maze = new Maze(moveLimit);

        System.out.println("The maze is falling apart. Escape as fast as you can! (You get " + moveLimit + " moves)");

        while (!maze.isFinished && maze.moveLimit > 0) {
            System.out.println(maze.currentRoom.getDescription());
            System.out.println("Exits: " + maze.currentRoom.getExits());

            maze.displayOptions();
            char command = scanner.next().charAt(0);

            if ("nsewud".indexOf(command) != -1) {
                if (!maze.move(command)) {
                    System.out.println("You can't go that way.");
                }
            } else if (command == 'i') {
                System.out.println(maze.interact());
            } else if (command == 'l') {
                System.out.println(maze.loot());
            } else if (command == 'x') {
                System.out.println(maze.exitRoom());
            } else if (command == 'v') {
                System.out.println("Inventory: " + maze.player.getInventory());
            } else {
                System.out.println("Invalid command.");
            }
        }

        System.out.println(maze.isFinished ? "Congratulations! You escaped the maze." : "The maze collapsed! You couldn't escape in time.");
        System.out.println("Game over. Your score: " + maze.player.getScore());
    }
}

import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import java.util.function.Predicate;

//  interface to easily modify constants
interface IConstants {
  // Defines a boardsize constant
  final int BOARD_SIZE = 4;

  // define a cellsize constant
  final int CELL_SIZE = 26;

}

//  check if the given cell is flooded
class IsFlooded implements Predicate<Cell> {
  public boolean test(Cell t) {
    return t.flooded;
  }
}

//  Represents a single square of the game area
class Cell implements IConstants {
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  Color color;
  boolean flooded;

  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  Cell(int x, int y, Color color, Boolean flooded) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = flooded;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;

  }

  // draws the given cell
  public WorldImage drawCell() {
    return new RectangleImage(CELL_SIZE, CELL_SIZE, OutlineMode.SOLID, this.color);
  }
}

// Represents the entire FloodItWorld game world 
class FloodItWorld extends World implements IConstants {

  // All the cells of the game
  ArrayList<Cell> board = new ArrayList<Cell>(BOARD_SIZE * BOARD_SIZE);

  // the 2d grid of cells in the game
  ArrayList<ArrayList<Cell>> grid = new ArrayList<ArrayList<Cell>>(BOARD_SIZE);

  // make a hashmap of all the colors
  HashMap<Integer, Color> cellColors;

  // how many colors will the board have?
  int numColors;

  // is the world currently flooding?
  boolean flooding;

  // used for when the state of the world is flooding
  ArrayList<Cell> flooded = new ArrayList<Cell>();

  // used to store the color of the flooded cells
  Color floodColor;

  // how many moves do you have to win?
  int numMoves;

  // Random variable generator
  Random rand;
  
  // timer for the game
  int timer;

  // FloodIt World constructor for premade board
  FloodItWorld(HashMap<Integer, Color> cellColors, int numColors, Random rand) {
    this.cellColors = cellColors;
    this.numColors = numColors;
    this.rand = rand;
    this.flooding = false;
    this.board = makeBoard();
    this.numMoves = 0;
    this.timer = 0;
    assignNeighbors();
  }

  /// FloodIt World constructor for random board
  FloodItWorld(HashMap<Integer, Color> cellColors, int numColors) {
    this.cellColors = cellColors;
    this.numColors = numColors;
    this.rand = new Random();
    this.flooding = false;
    this.board = makeBoard();
    this.numMoves = 0;
    this.timer = 0;
    assignNeighbors();
  }

  // adds cells with random colors to 1D board arraylist and to 2d grid arraylist
  // assigns all neighbors as null for now
  ArrayList<Cell> makeBoard() {
    ArrayList<Cell> temp = new ArrayList<Cell>(BOARD_SIZE * BOARD_SIZE);
    for (int p = 0; p < BOARD_SIZE; p++) {
      this.grid.add(new ArrayList<Cell>(BOARD_SIZE));
    }
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = CELL_SIZE / 2; j < (BOARD_SIZE * CELL_SIZE); j += CELL_SIZE) {
        Cell tempCell = new Cell(j, (i * CELL_SIZE) + CELL_SIZE / 2,
            this.cellColors.get(this.rand.nextInt(this.numColors)), false);
        // set topmost leftmost cell to flooded
        if (i == 0 && j == CELL_SIZE / 2) {
          tempCell.flooded = true;
        }
        temp.add(tempCell);
        this.grid.get(i).add(tempCell);
      }
    }
    return temp;
  }

  // EFFECT: assigns the neighbors of all cells in the grid
  void assignNeighbors() {
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        Cell current = this.grid.get(i).get(j);
        if (i == 0) {
          current.top = null;
        }
        else {
          current.top = grid.get(i - 1).get(j);
        }
        if (i == (BOARD_SIZE - 1)) {
          current.bottom = null;
        }
        else {
          current.bottom = grid.get(i + 1).get(j);
        }
        if (j == 0) {
          current.left = null;
        }
        else {
          current.left = grid.get(i).get(j - 1);
        }
        if (j == (BOARD_SIZE - 1)) {
          current.right = null;
        }
        else {
          current.right = grid.get(i).get(j + 1);
        }
      }
    }
  }

  // returns a scene with all cells drawn on it
  // also puts the current move counter and the timer on the scene
  public WorldScene makeScene() {
    WorldImage background = new RectangleImage(BOARD_SIZE * 15, BOARD_SIZE * 15, OutlineMode.SOLID,
        Color.WHITE);
    WorldScene scene = this.getEmptyScene();
    scene.placeImageXY(background, (BOARD_SIZE * CELL_SIZE) / 2, (BOARD_SIZE * CELL_SIZE) / 2);
    for (Cell c : this.board) {
      scene.placeImageXY(c.drawCell(), c.x, c.y);
    }
    scene.placeImageXY(new TextImage(((Integer)this.numMoves).toString(), 25, Color.WHITE), 
        (BOARD_SIZE * CELL_SIZE) / 2, (BOARD_SIZE * CELL_SIZE) / 2);
    scene.placeImageXY(new TextImage(((Integer)(this.timer / 10)).toString(), 10, Color.WHITE), 
        ((BOARD_SIZE * CELL_SIZE) - (CELL_SIZE / 2)), (CELL_SIZE / 2));
    return scene;
  }

  // returns a final scene with a win/lose screen
  public WorldScene makeAFinalScene(String message) {
    WorldImage background = new RectangleImage(BOARD_SIZE * 15, BOARD_SIZE * 15, OutlineMode.SOLID,
        Color.WHITE);
    WorldScene scene = this.getEmptyScene();
    scene.placeImageXY(background, (BOARD_SIZE * CELL_SIZE) / 2, (BOARD_SIZE * CELL_SIZE) / 2);
    //lil bit of extra credit: displays number of moves so far in middle of board
    scene.placeImageXY(new TextImage(message, Color.BLACK), (BOARD_SIZE * CELL_SIZE) / 2,
        (BOARD_SIZE * CELL_SIZE) / 2);
    return scene;
  }

  // when the mouse is clicked, updates the boardstate
  public void onMouseClicked(Posn pos) {
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        int x = grid.get(i).get(j).x;
        int y = grid.get(i).get(j).y;
        if (pos.x < x + CELL_SIZE / 2 && pos.x > x - CELL_SIZE / 2 && pos.y < y + CELL_SIZE / 2
            && pos.y > y - CELL_SIZE / 2) {
          this.floodColor = grid.get(i).get(j).color;
          this.flooding = true;
          this.numMoves++;
        }
      }
    }
  }

  // floods the appropriate cells by updating their flooded values
  public void flood(Cell cell) {
    Color color = cell.color;
    if (!color.equals(this.floodColor)) {
      cell.color = this.floodColor;
      if (cell.top != null && cell.top.color.equals(color)) {
        cell.top.flooded = true;
      }
      if (cell.bottom != null && cell.bottom.color.equals(color)) {
        cell.bottom.flooded = true;
      }
      if (cell.left != null && cell.left.color.equals(color)) {
        cell.left.flooded = true;
      }
      if (cell.right != null && cell.right.color.equals(color)) {
        cell.right.flooded = true;
      }
    }
  }

  // resets game when "r" is pressed
  // resetting the board calls bigBang and makes another world
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.endOfWorld("Reset");
      new FloodItWorld(this.cellColors, this.numColors, this.rand).bigBang((BOARD_SIZE) * CELL_SIZE,
          (BOARD_SIZE) * CELL_SIZE, 0.2);
    }
  }

  // updates the state of the world every tick
  public void onTick() {
    if (this.flooding) {
      for (Cell cell : this.flooded) {
        flood(cell);
      }
      // if flooded size has not increased since last time
      if (this.updateFlooded()) {
        this.flooding = false;
      }
    }
    this.timer++;
  }

  // updates the flooded cells
  boolean updateFlooded() {
    IsFlooded pred = new IsFlooded();
    int counter = 0;
    for (Cell cell : board) {
      if (pred.test(cell)) {
        this.flooded.add(cell);
        counter++;
      }
    }
    return (counter == 0);
  }

  // ends the world if:
  // the player made more than the max number of moves (loss)
  // the player flooded the whole board (win)
  // otherwise continues the game
  public WorldEnd worldEnds() {
    int winNumber = (BOARD_SIZE - 2) + this.numColors;
    if (this.numMoves > winNumber) {
      return new WorldEnd(true, this.makeAFinalScene("You lost!"));
    }
    else if (this.gameWon()) {
      return new WorldEnd(true, this.makeAFinalScene("You won!"));
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }

  // checks if the whole board is same color
  boolean gameWon() {
    boolean allSame = true;
    for (Cell cell : board) {
      if (!cell.color.equals(this.floodColor)) {
        allSame = false;
      }
    }
    return allSame;
  }

}

//class for examples and tests of FloodItWorld
class ExampleFloodIt implements IConstants {

  // board states
  ArrayList<Cell> mtlist = new ArrayList<Cell>();

  // cellColors Hashmap initialize
  HashMap<Integer, Color> cellColors;

  Random testRand = new Random(10);

  FloodItWorld flood1;

  FloodItWorld flood2;

  FloodItWorld flood3;

  void initCellColors() {
    this.testRand = new Random(10);
    this.cellColors = new HashMap<Integer, Color>();
    this.cellColors.put(0, Color.MAGENTA);
    this.cellColors.put(1, Color.BLACK);
    this.cellColors.put(2, Color.RED);
    this.cellColors.put(3, Color.BLUE);
    this.cellColors.put(4, Color.GREEN);
    this.cellColors.put(5, Color.YELLOW);
    this.cellColors.put(6, Color.GRAY);
    this.cellColors.put(7, Color.PINK);
    flood2 = new FloodItWorld(this.cellColors, 2, this.testRand);
    flood3 = new FloodItWorld(this.cellColors, 1, this.testRand);
  }

  // method to test the game
  void testGame(Tester t) {
    initCellColors();
    this.flood1 = new FloodItWorld(this.cellColors, 4);
    this.flood1.bigBang((BOARD_SIZE) * CELL_SIZE, (BOARD_SIZE) * CELL_SIZE, 0.1);
  }

  // tests the assignNeighbors method
  // tests all neighbors of each cell in a 2x2 grid- uses color checking to see if
  // cells are neighbors,
  // which a TA told me was fine as long as I was thorough
  void testAssignNeighbors(Tester t) {
    initCellColors();
    t.checkExpect(this.flood2.grid.get(0).get(0).right.color, Color.MAGENTA);
    t.checkExpect(this.flood2.grid.get(0).get(0).bottom.color, Color.MAGENTA);
    t.checkExpect(this.flood2.grid.get(0).get(1).bottom.color, Color.BLACK);
    t.checkExpect(this.flood2.grid.get(0).get(1).left.color, Color.BLACK);
    t.checkExpect(this.flood2.grid.get(1).get(0).top.color, Color.BLACK);
    t.checkExpect(this.flood2.grid.get(1).get(0).right.color, Color.BLACK);
    t.checkExpect(this.flood2.grid.get(1).get(1).top.color, Color.MAGENTA);
    t.checkExpect(this.flood2.grid.get(1).get(1).left.color, Color.MAGENTA);
    t.checkExpect(this.flood2.grid.get(0).get(0).top, null);
    t.checkExpect(this.flood2.grid.get(0).get(0).top, null);
    t.checkExpect(this.flood2.grid.get(0).get(0).left, null);
    t.checkExpect(this.flood2.grid.get(0).get(3).right, null);
    t.checkExpect(this.flood2.grid.get(0).get(1).top, null);
    t.checkExpect(this.flood2.grid.get(3).get(0).bottom, null);
    t.checkExpect(this.flood2.grid.get(1).get(0).left, null);
    t.checkExpect(this.flood2.grid.get(3).get(3).right, null);
    t.checkExpect(this.flood2.grid.get(3).get(3).bottom, null);
  }

  // tests the makeScene method
  void testMakeScene(Tester t) {
    initCellColors();
    WorldImage background = new RectangleImage(2 * 15, 2 * 15, OutlineMode.SOLID, Color.WHITE);
    WorldScene scene = this.flood2.getEmptyScene();
    scene.placeImageXY(background, 52, 52);
    scene.placeImageXY(new RectangleImage(26, 26, OutlineMode.SOLID, Color.BLACK), 13, 13);
    scene.placeImageXY(new RectangleImage(26, 26, OutlineMode.SOLID, Color.MAGENTA), 13, 26);
    scene.placeImageXY(new RectangleImage(26, 26, OutlineMode.SOLID, Color.MAGENTA), 26, 13);
    scene.placeImageXY(new RectangleImage(26, 26, OutlineMode.SOLID, Color.MAGENTA), 26, 26);
    scene.placeImageXY(new TextImage("0", 25, Color.BLACK), (BOARD_SIZE * CELL_SIZE) / 2, 
        (BOARD_SIZE * CELL_SIZE) / 2);
    t.checkExpect(this.flood2.makeScene(), scene);
  }

  // tests the makeBoard method
  void testMakeBoard(Tester t) {
    initCellColors();
    t.checkExpect(this.flood2.grid.get(0).get(0).color, Color.BLACK);
    t.checkExpect(this.flood2.grid.get(0).get(1).color, Color.MAGENTA);
    t.checkExpect(this.flood2.grid.get(1).get(0).color, Color.MAGENTA);
    t.checkExpect(this.flood2.grid.get(1).get(1).color, Color.BLACK);
  } 

  // tests the drawCell method
  void testDrawCell(Tester t) {
    initCellColors();
    t.checkExpect(this.flood2.grid.get(0).get(1).drawCell(),
        new RectangleImage(26, 26, OutlineMode.SOLID, Color.MAGENTA));
    t.checkExpect(this.flood2.grid.get(0).get(0).drawCell(),
        new RectangleImage(26, 26, OutlineMode.SOLID, Color.BLACK));
  }

  // tests the onMouseClicked method
  void testOnMouseClicked(Tester t) {
    initCellColors();
    this.flood2.onMouseClicked(new Posn(39, 13));
    t.checkExpect(this.flood2.floodColor, Color.MAGENTA);
    t.checkExpect(this.flood2.flooding, true);
    this.flood2.onMouseClicked(new Posn(13, 13));
    t.checkExpect(this.flood2.floodColor, Color.BLACK);
    t.checkExpect(this.flood2.flooding, true);
  }

  // tests the isFlooded method
  void testIsFlooded(Tester t) {
    initCellColors();
    IsFlooded pred = new IsFlooded();
    t.checkExpect(pred.test(this.flood2.board.get(0)), true);
    t.checkExpect(pred.test(this.flood2.board.get(1)), false);
  }

  // tests the onTick method
  void testOnTick(Tester t) {
    initCellColors();
    this.flood2.onTick();
    t.checkExpect(this.flood2.flooded.size(), 0);
  }

  // tests the makeAFinalScene method
  void testMakeAFinalScene(Tester t) {
    initCellColors();
    WorldImage background = new RectangleImage(BOARD_SIZE * 15, BOARD_SIZE * 15, OutlineMode.SOLID,
        Color.WHITE);
    WorldScene scene = this.flood2.getEmptyScene();
    scene.placeImageXY(background, (BOARD_SIZE * CELL_SIZE) / 2, (BOARD_SIZE * CELL_SIZE) / 2);
    scene.placeImageXY(new TextImage("You lost!", Color.BLACK), (BOARD_SIZE * CELL_SIZE) / 2,
        (BOARD_SIZE * CELL_SIZE) / 2);
    t.checkExpect(this.flood2.makeAFinalScene("You lost!"), scene);

  }

  // tests the flood method
  void testFlood(Tester t) {
    initCellColors();
    this.flood3.floodColor = Color.black;
    this.flood3.flood(this.flood3.board.get(0));
    t.checkExpect(this.flood3.board.get(1).flooded, true);
    t.checkExpect(this.flood3.board.get(2).flooded, false);
    t.checkExpect(this.flood3.board.get(4).flooded, true);
  }

  // tests the onKeyEvent method
  // testing this method causes a 2x2 board with 2 colors
  // to be created because onKeyEvent calls bigbang
  void testOnKeyEvent(Tester t) {
    initCellColors();
    this.flood2.onKeyEvent("r");
    t.checkExpect(this.flood2.flooding, false);
    this.flood2.onMouseClicked(new Posn(39, 13));
    this.flood2.onKeyEvent("e");
    t.checkExpect(this.flood2.flooding, true);
  }

  // tests the updateFlooded method
  void testUpdateFlooded(Tester t) {
    initCellColors();
    this.flood2.onMouseClicked(new Posn(13, 13));
    t.checkExpect(this.flood2.updateFlooded(), false);
    this.flood2.onMouseClicked(new Posn(100, 100));
    t.checkExpect(this.flood2.updateFlooded(), false);
  }

  // tests the worldEnds method
  void testWorldEnds(Tester t) {
    initCellColors();
    this.flood2.numMoves = 6;
    t.checkExpect(this.flood2.worldEnds(),
        new WorldEnd(true, this.flood2.makeAFinalScene("You lost!")));
    this.flood3.floodColor = Color.magenta;
    t.checkExpect(this.flood3.worldEnds(),
        new WorldEnd(true, this.flood2.makeAFinalScene("You won!")));
  }

  // tests the gameWon method
  void testGameWon(Tester t) {
    initCellColors();
    t.checkExpect(this.flood2.gameWon(), false);
    this.flood3.floodColor = Color.magenta;
    t.checkExpect(this.flood3.gameWon(), true);
  }

}




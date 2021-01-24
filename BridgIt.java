import java.util.ArrayList;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

// interface to easily modify constants
interface IConstants {

  // define a cellsize constant
  final int CELL_SIZE = 26;

}

// represents a single square of the game area
class Cell implements IConstants {
  // components of the cell: coordinates, color, filled
  int x;
  int y;
  Color color;
  boolean canChange;

  // the cells surrounding this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  // constructor, needs to set the Cell's directions to null initially
  Cell(int x, int y, Color color, boolean canChange) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.canChange = canChange;
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

// Represents the entire BridgIt game world 
class BridgItWorld extends World implements IConstants {

  // set the board size of the world
  // defined here so that each world can have it's boardSize modified for testing
  // we originally had BOARD_SIZE defined in IConstants but in order to test
  // different size boards we need to have it defined here
  int boardSize = 11;

  // the 2d grid of all cells in the game
  ArrayList<ArrayList<Cell>> grid = new ArrayList<ArrayList<Cell>>(boardSize);

  // arrays of all the start/end points for magenta and pink
  // used to determine when we have a complete path and the winner can be named
  ArrayList<Cell> magentaStart = new ArrayList<Cell>();
  ArrayList<Cell> magentaEnd = new ArrayList<Cell>();
  ArrayList<Cell> pinkStart = new ArrayList<Cell>();
  ArrayList<Cell> pinkEnd = new ArrayList<Cell>();

  // boolean for whose turn it is
  boolean p1turn;

  // boolean for if a path exists between magentaStart/magentaEnd
  // or pinkStart/pinkEnd
  boolean pathExists = false;

  // if p1 has a complete path, this is set to true
  boolean p1Won;

  // timer for the game
  int timer;

  // BridgIt World constructor
  // make the board, then assign the neighbors to all the cells
  BridgItWorld() {
    makeBoard();
    assignNeighbors();
    p1turn = true;
    this.timer = 0;

  }

  // EFFECT: Add cells to the 2d grid ArrayList
  // also adds to the magentaStart/magentaEnd and pinkStart/pinkEnd arrays
  public void makeBoard() {
    for (int p = 0; p < boardSize; p++) {
      this.grid.add(new ArrayList<Cell>(boardSize));
    }
    for (int i = 0; i < boardSize; i++) {
      for (int j = 0; j < boardSize; j++) {
        if (i % 2 == 0) {
          if (j % 2 == 0) {
            if (i == 0 || i == boardSize - 1 || j == 0 || j == boardSize - 1) {
              this.grid.get(i).add(makeCell(Color.WHITE, i, j, false));
            }
            else {
              this.grid.get(i).add(makeCell(Color.WHITE, i, j, true));
            }

          }
          else {
            this.grid.get(i).add(makeCell(Color.MAGENTA, i, j, false));
            if (i == 0) {
              this.magentaStart.add(this.grid.get(i).get(j));
            }
            if (i == this.boardSize - 1) {
              this.magentaEnd.add(this.grid.get(i).get(j));
            }
          }
        }
        else {
          if (j % 2 == 0) {
            this.grid.get(i).add(makeCell(Color.PINK, i, j, false));
            if (j == 0) {
              this.pinkStart.add(this.grid.get(i).get(j));
            }
            if (j == this.boardSize - 1) {
              this.pinkEnd.add(this.grid.get(i).get(j));
            }
          }
          else {
            this.grid.get(i).add(makeCell(Color.WHITE, i, j, true));
          }
        }
      }
    }

  }

  // returns a new cell with the correct coordinates and given parameters
  // essentially a helper for makeBoard to reduce clutter
  public Cell makeCell(Color color, int j, int i, boolean canChange) {
    return new Cell((CELL_SIZE / 2) + (i * CELL_SIZE), (CELL_SIZE / 2) + (j * CELL_SIZE), color,
        canChange);
  }

  // EFFECT: assigns the neighbors of all cells in the grid
  void assignNeighbors() {
    for (int i = 0; i < boardSize; i++) {
      for (int j = 0; j < boardSize; j++) {
        Cell current = this.grid.get(i).get(j);
        if (i == 0) {
          current.top = null;
        }
        else {
          current.top = grid.get(i - 1).get(j);
        }
        if (i == (boardSize - 1)) {
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
        if (j == (boardSize - 1)) {
          current.right = null;
        }
        else {
          current.right = grid.get(i).get(j + 1);
        }
      }
    }
  }

  // return a scene with all the cells and moves and timer drawn on it
  public WorldScene makeScene() {
    WorldImage background = new RectangleImage(boardSize * 15, boardSize * 15, OutlineMode.SOLID,
        Color.WHITE);
    WorldScene scene = this.getEmptyScene();
    scene.placeImageXY(background, (boardSize * CELL_SIZE) / 2, (boardSize * CELL_SIZE) / 2);
    for (int i = 0; i < boardSize; i++) {
      for (int j = 0; j < boardSize; j++) {
        Cell c = this.grid.get(i).get(j);
        scene.placeImageXY(c.drawCell(), c.x, c.y);
      }
    }
    scene.placeImageXY(new TextImage(((Integer) (this.timer / 10)).toString(), 10, Color.BLACK),
        ((this.boardSize * CELL_SIZE) - (CELL_SIZE / 2)), (CELL_SIZE / 2));
    return scene;
  }

  // resets game if "r" key is pressed
  // resetting the board calls bigBang and makes another world
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.endOfWorld("Reset");
      new BridgItWorld().bigBang((this.boardSize) * CELL_SIZE, (this.boardSize) * CELL_SIZE, 0.1);
    }
  }

  // when the mouse is clicked, updates the boardstate
  public void onMouseClicked(Posn pos) {
    for (int i = 0; i < this.boardSize; i++) {
      for (int j = 0; j < this.boardSize; j++) {
        Cell cell = this.grid.get(i).get(j);
        int x = cell.x;
        int y = cell.y;
        if (pos.x < x + CELL_SIZE / 2 && pos.x > x - CELL_SIZE / 2 && pos.y < y + CELL_SIZE / 2
            && pos.y > y - CELL_SIZE / 2) {
          if (this.p1turn && cell.canChange) {
            cell.color = Color.MAGENTA;
            cell.canChange = false;
            this.p1turn = false;
            this.pathExists = checkPath(cell);
          }
          else if (!this.p1turn && cell.canChange) {
            cell.color = Color.PINK;
            cell.canChange = false;
            this.p1turn = true;
            this.pathExists = checkPath(cell);
          }
        }
      }
    }
  }

  // updates the state of the world every tick
  // added for extra credit thank you :) 
  public void onTick() {
    this.timer++;
  }

  // returns whether or not a full path exists
  // first checks if there's a path from a magenta/pink Start cell to the current
  // then checks if there's a path from the current to a magenta/pink End cell
  public boolean checkPath(Cell cell) {
    boolean start = false;
    boolean end = false;
    if (cell.color == Color.MAGENTA) {
      for (Cell c : this.magentaStart) {
        if (hasPathBetween(cell, c)) {
          start = true;
          break;
        }
      }
      for (Cell c : this.magentaEnd) {
        if (hasPathBetween(cell, c)) {
          end = true;
          break;
        }
      }
      if (start && end) {
        this.p1Won = true;
      }
    }
    if (cell.color == Color.PINK) {
      for (Cell c : this.pinkStart) {
        if (hasPathBetween(cell, c)) {
          start = true;
          break;
        }
      }
      for (Cell c : this.pinkEnd) {
        if (hasPathBetween(cell, c)) {
          end = true;
          break;
        }
      }
    }
    return start && end;
  }

  // modified from Lecture 30 notes, returns whether or not there is a path
  // from the first given cell to the second given cell
  boolean hasPathBetween(Cell from, Cell to) {
    ArrayList<Cell> alreadySeen = new ArrayList<Cell>();
    ArrayList<Cell> worklist = new ArrayList<Cell>();

    // Initialize the worklist with the from vertex
    worklist.add(0, from);
    // As long as the worklist isn't empty...
    while (worklist.size() > 0) {
      Cell next = worklist.remove(0);
      if (next.equals(to)) {
        return true; // Success!
      }
      else if (alreadySeen.contains(next)) {
        // do nothing: we've already seen this one
      }
      else {
        // add all the neighbors of next to the worklist for further processing
        if (next.left != null && next.left.color == next.color) {
          worklist.add(0, next.left);
        }
        if (next.top != null && next.top.color == next.color) {
          worklist.add(0, next.top);
        }
        if (next.right != null && next.right.color == next.color) {
          worklist.add(0, next.right);
        }
        if (next.bottom != null && next.bottom.color == next.color) {
          worklist.add(0, next.bottom);
        }
        // add next to alreadySeen, since we're done with it
        alreadySeen.add(0, next);
      }
    }
    // We haven't found the to vertex, and there are no more to try
    return false;
  }

  // returns a final scene with the appropriate win/lose screen
  // when magenta wins you get a magenta win screen
  // when pink wins you get a pink win screen
  // please give a little extra credit for this thank you so much :) 
  public WorldScene makeAFinalScene(String message) {

    if (message.equals("Player 1 won!")) {
      WorldImage background = new RectangleImage(this.boardSize * 15, this.boardSize * 15,
          OutlineMode.SOLID, Color.MAGENTA);
      WorldScene scene = this.getEmptyScene();
      scene.placeImageXY(background, (this.boardSize * CELL_SIZE) / 2,
          (this.boardSize * CELL_SIZE) / 2);
      scene.placeImageXY(new TextImage(message, Color.WHITE), (this.boardSize * CELL_SIZE) / 2,
          (this.boardSize * CELL_SIZE) / 2);
      return scene;
    }
    else {
      WorldImage background = new RectangleImage(this.boardSize * 15, this.boardSize * 15,
          OutlineMode.SOLID, Color.PINK);
      WorldScene scene = this.getEmptyScene();
      scene.placeImageXY(background, (this.boardSize * CELL_SIZE) / 2,
          (this.boardSize * CELL_SIZE) / 2);
      scene.placeImageXY(new TextImage(message, Color.BLACK), (this.boardSize * CELL_SIZE) / 2,
          (this.boardSize * CELL_SIZE) / 2);
      return scene;
    }

  }

  // ends the world if: // pink player connected a path from the left edge to
  // the right edge // purple player connected a path from the top to the bottom
  // otherwise continue the game
  public WorldEnd worldEnds() {
    if (this.pathExists && this.p1Won) {
      return new WorldEnd(true, this.makeAFinalScene("Player 1 won!"));
    }
    else if (this.pathExists && !this.p1Won) {
      return new WorldEnd(true, this.makeAFinalScene("Player 2 won!"));
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }

}

// examples and tests of BridItWorld
class ExamplesBridIt implements IConstants {
  BridgItWorld world1;
  BridgItWorld world2;
  BridgItWorld world3;

  // reinitialize the worlds between test methods
  void initGame() {
    this.world1 = new BridgItWorld();
    this.world2 = new BridgItWorld();
    this.world3 = new BridgItWorld();

  }

  // method to run the game
  void testGame(Tester t) {
    initGame();
    this.world1.bigBang((this.world1.boardSize) * CELL_SIZE, (this.world1.boardSize) * CELL_SIZE,
        0.1);
  }

  // tests the assignNeighbors method
  void testAssignNeighbors(Tester t) {
    initGame();
    t.checkExpect(this.world1.grid.get(0).get(0).right.color, Color.MAGENTA);
    t.checkExpect(this.world1.grid.get(0).get(0).bottom.color, Color.PINK);
    t.checkExpect(this.world1.grid.get(0).get(0).top, null);
    t.checkExpect(this.world1.grid.get(0).get(0).left, null);

    t.checkExpect(this.world1.grid.get(this.world1.boardSize - 1).get(0).bottom, null);
    t.checkExpect(this.world1.grid.get(this.world1.boardSize - 1).get(0).left, null);
    t.checkExpect(this.world1.grid.get(this.world1.boardSize - 1).get(0).right.color,
        Color.MAGENTA);
    t.checkExpect(this.world1.grid.get(this.world1.boardSize - 1).get(0).top.color, Color.PINK);

    t.checkExpect(this.world1.grid.get(0).get(this.world1.boardSize - 1).right, null);
    t.checkExpect(this.world1.grid.get(0).get(this.world1.boardSize - 1).top, null);
    t.checkExpect(this.world1.grid.get(0).get(this.world1.boardSize - 1).bottom.color, Color.PINK);
    t.checkExpect(this.world1.grid.get(0).get(this.world1.boardSize - 1).left.color, Color.MAGENTA);

    t.checkExpect(
        this.world1.grid.get(this.world1.boardSize - 1).get(this.world1.boardSize - 1).bottom,
        null);
    t.checkExpect(
        this.world1.grid.get(this.world1.boardSize - 1).get(this.world1.boardSize - 1).right, null);
    t.checkExpect(
        this.world1.grid.get(this.world1.boardSize - 1).get(this.world1.boardSize - 1).left.color,
        Color.MAGENTA);
    t.checkExpect(
        this.world1.grid.get(this.world1.boardSize - 1).get(this.world1.boardSize - 1).top.color,
        Color.PINK);

    t.checkExpect(this.world1.grid.get(6).get(0).left, null);
    t.checkExpect(this.world1.grid.get(4).get(4).right.color, Color.MAGENTA);
    t.checkExpect(this.world1.grid.get(5).get(3).right.color, Color.PINK);
    t.checkExpect(this.world1.grid.get(6).get(4).canChange, true);
    t.checkExpect(this.world1.grid.get(6).get(4).color, Color.WHITE);
    t.checkExpect(this.world1.grid.get(2).get(1).left.canChange, false);
    t.checkExpect(this.world1.grid.get(2).get(1).right.canChange, true);
    t.checkExpect(this.world1.grid.get(2).get(1).canChange, false);
    t.checkExpect(this.world1.grid.get(1).get(2).canChange, false);

  }

  // tests the drawCell method
  void testDrawCell(Tester t) {
    initGame();
    t.checkExpect(this.world1.grid.get(0).get(0).drawCell(),
        new RectangleImage(CELL_SIZE, CELL_SIZE, OutlineMode.SOLID, Color.WHITE));
    t.checkExpect(this.world1.grid.get(3).get(2).drawCell(),
        new RectangleImage(CELL_SIZE, CELL_SIZE, OutlineMode.SOLID, Color.PINK));
    t.checkExpect(this.world1.grid.get(0).get(3).drawCell(),
        new RectangleImage(CELL_SIZE, CELL_SIZE, OutlineMode.SOLID, Color.MAGENTA));
  }

  // tests the makeBoard method
  void testMakeBoard(Tester t) {
    initGame();
    t.checkExpect(this.world1.grid.get(0).get(0).canChange, false);
    t.checkExpect(this.world1.grid.get(0).get(0).color, Color.WHITE);
    t.checkExpect(this.world1.grid.get(2).get(2).canChange, true);
    t.checkExpect(this.world1.grid.get(2).get(2).color, Color.WHITE);
    t.checkExpect(this.world1.grid.get(this.world1.boardSize - 1).get(2).canChange, false);
    t.checkExpect(this.world1.grid.get(this.world1.boardSize - 1).get(2).color, Color.WHITE);
    t.checkExpect(this.world1.grid.get(this.world1.boardSize - 1).get(3).color, Color.MAGENTA);
    t.checkExpect(this.world1.grid.get(this.world1.boardSize - 1).get(3).canChange, false);
    t.checkExpect(this.world1.grid.get(3).get(4).color, Color.PINK);
    t.checkExpect(this.world1.grid.get(3).get(4).canChange, false);

  }

  // tests the makeCell method
  void testMakeCell(Tester t) {
    initGame();
    t.checkExpect(this.world1.makeCell(Color.WHITE, 0, 1, true),
        new Cell(39, 13, Color.WHITE, true));
    t.checkExpect(this.world1.makeCell(Color.MAGENTA, 2, 2, false),
        new Cell(65, 65, Color.MAGENTA, false));
    t.checkExpect(this.world1.makeCell(Color.WHITE, 0, 3, false),
        new Cell(91, 13, Color.WHITE, false));
    t.checkExpect(this.world1.makeCell(Color.PINK, 1, 0, false),
        new Cell(13, 39, Color.PINK, false));
  }

  // tests the makeScene method
  // initializes a new world, sets the boardSize of that world to 1
  // recalls makeBoard with the new boardSize, then is tested
  void testMakeScene(Tester t) {
    initGame();
    this.world2.boardSize = 1;
    this.world2.makeBoard();
    WorldImage background = new RectangleImage(this.world1.boardSize * 15,
        this.world2.boardSize * 15, OutlineMode.SOLID, Color.WHITE);
    WorldScene scene = this.world2.getEmptyScene();
    scene.placeImageXY(background, (this.world2.boardSize * CELL_SIZE) / 2,
        (this.world2.boardSize * CELL_SIZE) / 2);
    scene.placeImageXY(new RectangleImage(CELL_SIZE, CELL_SIZE, OutlineMode.SOLID, Color.WHITE),
        (CELL_SIZE / 2) + (1 * CELL_SIZE), (CELL_SIZE / 2) + (1 * CELL_SIZE));
    scene.placeImageXY(new TextImage(((Integer) (this.world2.timer / 10)).toString(), 10, 
        Color.BLACK), ((this.world2.boardSize * CELL_SIZE) - (CELL_SIZE / 2)), (CELL_SIZE / 2));
    

    t.checkExpect(this.world2.makeScene(), scene);
  }

  // tests the onKeyEvent method
  // testing this method causes another board to be produced
  // because onKeyEvent calls bigBang
  void testOnKeyEvent(Tester t) {
    initGame();
    this.world1.onKeyEvent("r");
    t.checkExpect(this.world1.grid.get(1).get(1).color, Color.WHITE);
    this.world1.onMouseClicked(new Posn(39, 39));
    this.world1.onKeyEvent("e");
    t.checkExpect(this.world1.grid.get(1).get(1).color, Color.MAGENTA);

  }

  // tests the onMouseClicked method
  void testOnMouseClicked(Tester t) {
    initGame();

    // change to magenta
    t.checkExpect(this.world1.grid.get(1).get(1).color, Color.WHITE);
    t.checkExpect(this.world1.grid.get(1).get(1).canChange, true);
    t.checkExpect(this.world1.p1turn, true);
    t.checkExpect(this.world1.pathExists, false);
    this.world1.onMouseClicked(new Posn(39, 39));
    t.checkExpect(this.world1.grid.get(1).get(1).color, Color.MAGENTA);
    t.checkExpect(this.world1.grid.get(1).get(1).canChange, false);
    t.checkExpect(this.world1.p1turn, false);
    t.checkExpect(this.world1.pathExists, false);

    // change to pink
    t.checkExpect(this.world1.grid.get(2).get(2).color, Color.WHITE);
    t.checkExpect(this.world1.grid.get(2).get(2).canChange, true);
    t.checkExpect(this.world1.p1turn, false);
    t.checkExpect(this.world1.pathExists, false);
    this.world1.onMouseClicked(new Posn(65, 65));
    t.checkExpect(this.world1.grid.get(2).get(2).color, Color.PINK);
    t.checkExpect(this.world1.grid.get(2).get(2).canChange, false);
    t.checkExpect(this.world1.p1turn, true);
    t.checkExpect(this.world1.pathExists, false);

    // edge case
    t.checkExpect(this.world1.grid.get(0).get(0).color, Color.WHITE);
    t.checkExpect(this.world1.grid.get(0).get(0).canChange, false);
    t.checkExpect(this.world1.p1turn, true);
    t.checkExpect(this.world1.pathExists, false);
    t.checkExpect(this.world1.pathExists, false);
    this.world1.onMouseClicked(new Posn(0, 0));
    t.checkExpect(this.world1.grid.get(0).get(0).color, Color.WHITE);
    t.checkExpect(this.world1.grid.get(0).get(0).canChange, false);
    t.checkExpect(this.world1.p1turn, true);
    t.checkExpect(this.world1.pathExists, false);

    // check that pathExists updates
    this.world2.onMouseClicked(new Posn(247, 39));
    this.world2.onMouseClicked(new Posn(39, 39));
    this.world2.onMouseClicked(new Posn(247, 91));
    this.world2.onMouseClicked(new Posn(91, 39));
    this.world2.onMouseClicked(new Posn(247, 143));
    this.world2.onMouseClicked(new Posn(143, 39));
    this.world2.onMouseClicked(new Posn(247, 195));
    this.world2.onMouseClicked(new Posn(195, 39));
    this.world2.onMouseClicked(new Posn(247, 247));
    t.checkExpect(this.world2.pathExists, true);

  }

  // tests the onTick method
  void testOnTick(Tester t) {
    initGame();
    this.world1.onTick();
    t.checkExpect(this.world1.timer, 1);

  }

  // tests the worldEnds method
  void testWorldEnds(Tester t) {
    initGame();
    this.world1.pathExists = true;
    this.world1.p1Won = true;
    t.checkExpect(this.world1.worldEnds(),
        new WorldEnd(true, this.world1.makeAFinalScene("Player 1 won!")));
    this.world1.p1Won = false;
    t.checkExpect(this.world1.worldEnds(),
        new WorldEnd(true, this.world1.makeAFinalScene("Player 2 won!")));
    this.world1.pathExists = false;
    t.checkExpect(this.world1.worldEnds(), new WorldEnd(false, this.world1.makeScene()));

  }

  // tests the checkPath method
  void testCheckPath(Tester t) {
    initGame();
    // magenta wins
    this.world2.onMouseClicked(new Posn(247, 39));
    this.world2.onMouseClicked(new Posn(39, 39));
    this.world2.onMouseClicked(new Posn(247, 91));
    this.world2.onMouseClicked(new Posn(91, 39));
    this.world2.onMouseClicked(new Posn(247, 143));
    this.world2.onMouseClicked(new Posn(143, 39));
    this.world2.onMouseClicked(new Posn(247, 195));
    this.world2.onMouseClicked(new Posn(195, 39));
    this.world2.onMouseClicked(new Posn(247, 247));
    t.checkExpect(this.world2.checkPath(this.world2.grid.get(9).get(9)), true);

    // pink wins
    this.world3.onMouseClicked(new Posn(247, 39));
    this.world3.onMouseClicked(new Posn(39, 247));
    this.world3.onMouseClicked(new Posn(247, 91));
    this.world3.onMouseClicked(new Posn(91, 247));
    this.world3.onMouseClicked(new Posn(247, 143));
    this.world3.onMouseClicked(new Posn(143, 247));
    this.world3.onMouseClicked(new Posn(247, 195));
    this.world3.onMouseClicked(new Posn(195, 247));
    this.world3.onMouseClicked(new Posn(39, 39));
    this.world3.onMouseClicked(new Posn(247, 247));
    t.checkExpect(this.world3.checkPath(this.world3.grid.get(9).get(9)), true);

    // nobody has won yet
    this.world1.onMouseClicked(new Posn(247, 39));
    this.world1.onMouseClicked(new Posn(39, 247));
    this.world1.onMouseClicked(new Posn(247, 91));
    this.world1.onMouseClicked(new Posn(91, 247));
    t.checkExpect(this.world1.checkPath(this.world1.grid.get(1).get(9)), false);

  }

  // tests the hasPathBetween method
  void testHasPathBetween(Tester t) {
    initGame();

    // path exists between two magenta cells
    this.world2.onMouseClicked(new Posn(247, 39));
    this.world2.onMouseClicked(new Posn(39, 39));
    this.world2.onMouseClicked(new Posn(247, 91));
    this.world2.onMouseClicked(new Posn(91, 39));
    this.world2.onMouseClicked(new Posn(247, 143));
    this.world2.onMouseClicked(new Posn(143, 39));
    this.world2.onMouseClicked(new Posn(247, 195));
    this.world2.onMouseClicked(new Posn(195, 39));
    this.world2.onMouseClicked(new Posn(247, 247));
    t.checkExpect(
        this.world2.hasPathBetween(this.world2.magentaEnd.get(4), this.world2.magentaStart.get(4)),
        true);

    // path exists between two pink cells
    this.world3.onMouseClicked(new Posn(247, 39));
    this.world3.onMouseClicked(new Posn(39, 247));
    this.world3.onMouseClicked(new Posn(247, 91));
    this.world3.onMouseClicked(new Posn(91, 247));
    this.world3.onMouseClicked(new Posn(247, 143));
    this.world3.onMouseClicked(new Posn(143, 247));
    this.world3.onMouseClicked(new Posn(247, 195));
    this.world3.onMouseClicked(new Posn(195, 247));
    this.world3.onMouseClicked(new Posn(39, 39));
    this.world3.onMouseClicked(new Posn(247, 247));
    t.checkExpect(
        this.world3.hasPathBetween(this.world3.grid.get(9).get(9), this.world3.pinkStart.get(4)),
        true);

    // no path exists between these two cells
    t.checkExpect(
        this.world1.hasPathBetween(this.world1.magentaEnd.get(2), this.world1.magentaStart.get(4)),
        false);

  }

  // tests the makeAFinalScene method
  void testMakeAFinalScene(Tester t) {
    initGame();
    WorldImage background = new RectangleImage(this.world1.boardSize * 15,
        this.world1.boardSize * 15, OutlineMode.SOLID, Color.MAGENTA);
    WorldScene scene = this.world1.getEmptyScene();
    scene.placeImageXY(background, (this.world1.boardSize * CELL_SIZE) / 2,
        (this.world1.boardSize * CELL_SIZE) / 2);
    scene.placeImageXY(new TextImage("Player 1 won!", Color.WHITE),
        (this.world1.boardSize * CELL_SIZE) / 2, (this.world1.boardSize * CELL_SIZE) / 2);
    t.checkExpect(this.world1.makeAFinalScene("Player 1 won!"), scene);

    WorldImage background2 = new RectangleImage(this.world1.boardSize * 15,
        this.world1.boardSize * 15, OutlineMode.SOLID, Color.PINK);
    WorldScene scene2 = this.world1.getEmptyScene();
    scene2.placeImageXY(background2, (this.world1.boardSize * CELL_SIZE) / 2,
        (this.world1.boardSize * CELL_SIZE) / 2);
    scene2.placeImageXY(new TextImage("Player 2 won!", Color.BLACK),
        (this.world1.boardSize * CELL_SIZE) / 2, (this.world1.boardSize * CELL_SIZE) / 2);
    t.checkExpect(this.world1.makeAFinalScene("Player 2 won!"), scene2);

  }

}

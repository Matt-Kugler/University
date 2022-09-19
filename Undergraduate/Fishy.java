
import tester.Tester;
import javalib.funworld.*;
import javalib.worldimages.*;
import java.awt.Color;
import java.util.Random;

//interface for any fish
interface IFish {
  
  //returns true if this fish can eat the given fish, 
  //based on the size of the fish and their distance from one another
  boolean canEat(AFish other);
}

//interface for list of fish
interface ILoFish {
  
  //returns a new ILoFish that is all of the current ILoFish for which the predicate is true
  ILoFish find(ILoFishPredicate pred);
  
  //returns a new ILoFish that is all of the current ILoFish for which the predicate is true
  ILoFish find2(ILoFishPredicate pred, AFish player);
  
  //changes the x-coordinate of each fish in the list (to be called every tick)
  ILoFish move();
  
  //generates new fish to the list randomly
  ILoFish generateFish(Random rand);
  
  //counts the total number of fish
  int countFish();

  //returns a new WorldImage of all fish in the list
  WorldScene drawBackground(WorldScene scene);
  
  //returns whether or not any background fish has eaten the player fish
  boolean backgroundWins(AFish player);
  
  //checks if the player can eat the background fish
  boolean backgroundEaten(AFish player);
  
  //adds the size of the backgroundfish to the playerfish
  int addSize();
}

//interface for predicates for ILoFishs
interface ILoFishPredicate {
  
  //applies the predicate to the bgfish
  boolean apply(BackgroundFish bgfish);
  
  //compares the bgfish and playerfish
  boolean compare(BackgroundFish bgfish, AFish player);
}

//predicate to determine if current fish is onscreen based on its current x - coordinate
class Onscreen implements ILoFishPredicate {
  //returns true if fish is on the screen
  public boolean apply(BackgroundFish bgfish) {
    return bgfish.x <= 500 && bgfish.x >= 0;
  }

  //returns false
  public boolean compare(BackgroundFish bgfish, AFish player) {
    return false;
  }
}

//predicate to determine if current fish is going left
class GoingLeft implements ILoFishPredicate {
  //returns true if fish is on the screen
  public boolean apply(BackgroundFish bgfish) {
    return bgfish.goingLeft;
  }

  //returns false
  public boolean compare(BackgroundFish bgfish, AFish player) {
    return false;
  }
}

//predicate to determine if current fish is going right
class GoingRight implements ILoFishPredicate {
  //returns true if fish is on the screen
  public boolean apply(BackgroundFish bgfish) {
    return !bgfish.goingLeft;
  }

  //returns false
  public boolean compare(BackgroundFish bgfish, AFish player) {
    return false;
  }
}

//predicate to determine if current fish has been eaten by player
class Eaten implements ILoFishPredicate {
  
  //returns false
  public boolean apply(BackgroundFish bgfish) {
    return false;
  }

  //returns true if fish has been eaten
  public boolean compare(BackgroundFish bgfish, AFish player) {
    return player.canEat(bgfish);
  }
}

//predicate to determine if current fish has not been eaten by player
class Uneaten implements ILoFishPredicate {
  //returns false
  public boolean apply(BackgroundFish bgfish) {
    return false;
  }

  //returns true if fish has been eaten
  public boolean compare(BackgroundFish bgfish, AFish player) {
    return !player.canEat(bgfish);
  }
}

//abstract class for any fish
abstract class AFish implements IFish {
  int x;
  int y;
  int size;
  
  AFish(int x, int y, int size) {
    this.x = x;
    this.y = y;
    this.size = size;
  }
  
  //returns true if this fish can eat the given fish, 
  //based on the size of the fish and their distance from one another
  public boolean canEat(AFish other) {
    return this.size > other.size && Math.abs(this.x - other.x) <= (this.size + other.size) / 2 
        && Math.abs(this.y - other.y) <= (this.size + other.size) / 2;
  }
}

//class to represent the user-controlled player
class Player extends AFish {
  Player(int x, int y, int size) {
    super(x, y, size);
  }
   
  // produce the image of this player
  WorldImage playerImage() {
    return new RectangleImage(this.size, this.size, "solid", Color.RED);
  }
   
  //allow player to be controlled by key events (up down left right)
  public Player movePlayer(String ke) {
    if (ke.equals("right")) {
      return new Player(this.x + 5, this.y, this.size);
    } 
    else if (ke.equals("left")) {
      return new Player(this.x - 5, this.y, this.size);
    } 
    else if (ke.equals("up")) {
      return new Player(this.x, this.y - 5, this.size);
    } 
    else if (ke.equals("down")) {
      return new Player(this.x, this.y + 5, this.size);
    }
    else {
      return this;
    }
  }
  
}

//class to represent a single background fish
class BackgroundFish extends AFish {
  Color color;
  Boolean goingLeft;
  
  BackgroundFish(int x, int y, int size, Color color, Boolean goingLeft) {
    super(x, y, size);
    this.color = color;
    this.goingLeft = goingLeft;
  }
  
  // produce the image of this BackgroundFish
  WorldImage backgroundImage() {
    return new RectangleImage(this.size, this.size, "solid", this.color);
  }
  
  // changes x coordinate of backGroundFish
  BackgroundFish changeX() {
    if (goingLeft) {
      return new BackgroundFish(this.x - 5, this.y, this.size, this.color, this.goingLeft);
    }
    else {
      return new BackgroundFish(this.x + 5, this.y, this.size, this.color, this.goingLeft);
    }
  }
 

}

//class to represent an empty list of fish
class MtLoFish implements ILoFish {
  MtLoFish(){}

  //returns an MtLoFish
  public ILoFish find(ILoFishPredicate pred) {
    return this;
  }
  
  //returns an MtLoFish
  public ILoFish find2(ILoFishPredicate pred, AFish player) {
    return this;
  }

  //returns an MtLoFish
  public ILoFish move() {
    return this;
  }
  
  //returns this
  public ILoFish generateFish(Random rand) {
    return this;
  }

  //returns 0
  public int countFish() {
    return 0;
  }

  //returns a circle of size 0
  public WorldScene drawBackground(WorldScene scene) {
    return scene;
  }

  //returns false
  public boolean backgroundWins(AFish player) {
    return false;
  }

  //returns false
  public boolean backgroundEaten(AFish player) {
    return false;
  }
  
  //returns 0
  public int addSize() {
    return 0;
  }
}


//class to represent a constructed list of fish
class ConsLoFish implements ILoFish {
  BackgroundFish first;
  ILoFish rest;
  
  ConsLoFish(BackgroundFish first, ILoFish rest) {
    this.first = first;
    this.rest = rest;
  }

  //create a list of all background fish for which predicate is true
  public ILoFish find(ILoFishPredicate pred) {
    // TODO Auto-generated method stub
    if (pred.apply(this.first)) {
      return new ConsLoFish(this.first, this.rest.find(pred));
    }
    else {
      return this.rest.find(pred);
    }
  }
  
  //create a list of all background fish for which predicate is true
  public ILoFish find2(ILoFishPredicate pred, AFish player) {
    // TODO Auto-generated method stub
    if (pred.compare(this.first, player)) {
      return new ConsLoFish(this.first, this.rest.find2(pred, player));
    }
    else {
      return this.rest.find2(pred, player);
    }
  }

  //changes x coordinates of all background fish
  public ILoFish move() {
    // TODO Auto-generated method stub
    return new ConsLoFish(this.first.changeX(), this.rest.move());
  }
  
  //needs to generate a random fish if there isn't enough on the screen
  //x needs to be random but only 0 or 500
  //goingLeft needs to be random but only true or false
  public ILoFish generateFish(Random rand) {
    if (this.countFish() < 7) {
      boolean left = rand.nextBoolean();
      if (left) {
        return new ConsLoFish(new BackgroundFish(500, rand.nextInt(500),
            rand.nextInt(75), Color.CYAN, left), this);
      }
      else {
        return new ConsLoFish(new BackgroundFish(0, rand.nextInt(500),
          rand.nextInt(75), Color.CYAN, left), this);
      }
    }
    else {
      return this;
    }     
  }
 
  //count the number of fish on screen 
  public int countFish() {
    return 1 + this.rest.countFish();
  }
  
  //produces a WorldImage that is all background fish in this list
  public WorldScene drawBackground(WorldScene scene) {
    return this.rest.drawBackground(scene.placeImageXY(
        this.first.backgroundImage(), this.first.x, this.first.y));
  }

  //returns true if any fish in the list can eat the fish
  public boolean backgroundWins(AFish player) {
    return this.first.canEat(player) || this.rest.backgroundWins(player);
  }

  //returns true if background fish is eaten by player
  public boolean backgroundEaten(AFish player) {
    return player.canEat(this.first) || this.rest.backgroundEaten(player);
  }
  
  public int addSize() {
    return this.first.size + this.rest.addSize();
  }
}



//represent the world of fish
class FishyWorld extends World {
  int width = 500;
  int height = 500;
  Player fish;
  ILoFish bgfish;
  
  //variable for testing random
  Random rand;
  
  //the constructor for real random games
  FishyWorld(Player fish, ILoFish bgfish) {
    this(new Random(), fish, bgfish);
  }
  
  //constructor for testing with a specified random object
  FishyWorld(Random rand, Player fish, ILoFish bgfish) {
    super();
    this.rand = rand;
    this.fish = fish;
    this.bgfish = bgfish;
  }
   
  //makes (draws) the current scene
  public WorldScene makeScene() {
    return this
        .bgfish.drawBackground(this.getEmptyScene().placeImageXY(
            this.fish.playerImage(), this.fish.x,
            this.fish.y));
        
  }

  //runs every tick; checks if player or any background fish are off screen and adjusts accordingly
  public World onTick() {
    
    //loop the player around the sides of the screen
    if (this.fish.x >= (500 + this.fish.size / 2)) {
      return new FishyWorld(this.rand, new Player(0, this.fish.y, this.fish.size + (int)Math.sqrt((
          this.bgfish.find2(new Eaten(), this.fish).addSize()))), 
          this.bgfish.find(new Onscreen()).find2(new Uneaten(), fish).generateFish(
              this.rand).move());
    }
    //loop the player around the sides of the screen
    else if (this.fish.x <= (0 - this.fish.size / 2)) {
      return new FishyWorld(this.rand, 
          new Player(500, this.fish.y, this.fish.size + (int)Math.sqrt((
          this.bgfish.find2(new Eaten(), this.fish).addSize()))), 
          this.bgfish.find(new Onscreen()).find2(new Uneaten(), fish).generateFish(
              this.rand).move());
    }
    
    //loop the player around the top of the screen 
    else if (this.fish.y >= (500 - this.fish.size / 2)) {
      return new FishyWorld(this.rand, new Player(this.fish.x, 0, this.fish.size  
          + (int)Math.sqrt((
          this.bgfish.find2(new Eaten(), this.fish).addSize()))), 
          this.bgfish.find(new Onscreen()).find2(new Uneaten(), fish).generateFish(
              this.rand).move());
    }
    //loop the player around the top of the screen 
    else if (this.fish.y <= (0 - this.fish.size / 2)) {
      return new FishyWorld(this.rand, new Player(this.fish.x, 500, this.fish.size  
          + (int)Math.sqrt((
          this.bgfish.find2(new Eaten(), this.fish).addSize()))), 
          this.bgfish.find(new Onscreen()).find2(new Uneaten(), fish).generateFish(
              this.rand).move());
    }
    
    // move the background fish
    // check if the fish are offscreen and remove them
    else {
      return new FishyWorld(this.rand, new Player(this.fish.x, this.fish.y, this.fish.size 
          + (int)Math.sqrt((this.bgfish.find2(new Eaten(), this.fish).addSize()))), 
          this.bgfish.find(new Onscreen()).find2(new Uneaten(), fish).generateFish(
              this.rand).move());
    }
  }
  
  // move the fish by making a new fish world where the fish is changed by some key amount
  public World onKeyEvent(String ke) {
    // move the fish
    // make a new fish world where the fish is changed by some key
    // amount
    return new FishyWorld(this.fish.movePlayer(ke), this.bgfish);
  }

  // constantly checks if the world is gonna end
  public WorldEnd worldEnds() {
    // if the user got eaten by a bigger fish
    // you lost
    if (this.bgfish.backgroundWins(this.fish)) {
      return new WorldEnd(true, this.makeScene().placeImageXY(
          new TextImage("A bigger background fish ate you!", 13, FontStyle.BOLD_ITALIC, Color.red),
          250,250));
    } 
    else if (this.fish.size > 75) {
      return new WorldEnd(true, this.makeScene().placeImageXY(
          new TextImage("YOU WON!!!!!!", 13, FontStyle.BOLD_ITALIC, Color.red),
          100, 250));
    }
    else {
      //or if the fish is the biggest fish in the pond
      // you won 
      return new WorldEnd(false, this.makeScene());
    }
  }
}



//examples of fish related objects and tests
class ExamplesFishy {
  Random rand = new Random(100);  
  
  Random rands = new Random(50);
  
  ILoFish mt = new MtLoFish();
  ILoFish background1 = new ConsLoFish(
      new BackgroundFish(0, 200, 40, Color.GREEN, false), mt);
  ILoFish background2 = new ConsLoFish(
      new BackgroundFish(500, 300, 10, Color.BLUE, true), background1);
  ILoFish background3 = new ConsLoFish(
      new BackgroundFish(0, 400, 30, Color.YELLOW, false), background2);
  ILoFish background4 = new ConsLoFish(
      new BackgroundFish(260, 260, 50, Color.BLUE, false), background3);
  
  ILoFish deadfish = new ConsLoFish(new BackgroundFish(250, 250, 600, Color.BLUE, false), mt);
  
  AFish player = new Player(250, 250, 25);    
  
  FishyWorld s = new FishyWorld(rands, new Player(250, 250, 25), this.background1);
  
  FishyWorld w = new FishyWorld(new Player(250, 250, 25), this.background1);
  
  FishyWorld offleft = new FishyWorld(rands, new Player(-50, 250, 25), this.background1);
  
  FishyWorld offright = new FishyWorld(rands, new Player(700, 250, 25), this.background1);
  
  FishyWorld offtop = new FishyWorld(rands, new Player(250, 800, 25), this.background1);

  FishyWorld offbottom = new FishyWorld(rands, new Player(250, -50, 25), this.background1);
  
  FishyWorld fishwin = new FishyWorld(rands, new Player(250, 250, 800), this.background1);
  
  FishyWorld fishdead = new FishyWorld(rands, new Player(250, 250, 25), this.deadfish);
  
  //test the running of the game
  boolean testFishyWorld(Tester t) {
    
    // run the game
    return w.bigBang(500, 500, 0.07);
         
  }
  
  //test the canEat method
  boolean testCanEat(Tester t) {
    return t.checkExpect((new BackgroundFish(
        0, 200, 40, Color.GREEN, false).canEat(this.player)), false)
        && t.checkExpect((new BackgroundFish(
            260, 260, 40, Color.GREEN, false).canEat(this.player)), true)
        && t.checkExpect((new BackgroundFish(
            250, 300, 30, Color.GREEN, false).canEat(this.player)), false);
  }
  
  //test the find method
  boolean testFind(Tester t) {
    return t.checkExpect(mt.find(new Onscreen()), mt)
        && t.checkExpect(background1.find(new Onscreen()), 
            new ConsLoFish(new BackgroundFish(0, 200, 40, Color.GREEN, false), mt))
        && t.checkExpect(background4.find(new Onscreen()), 
            new ConsLoFish(new BackgroundFish(260, 260, 50, Color.BLUE, false),
            new ConsLoFish(new BackgroundFish(0, 400, 30, Color.YELLOW, false), 
                new ConsLoFish(new BackgroundFish(500, 300, 10, Color.BLUE, true),
                    new ConsLoFish(new BackgroundFish(0, 200, 40, Color.GREEN, false), mt)))));
  }
  
  //test the find2 method
  boolean testFind2(Tester t) {
    return t.checkExpect(mt.find2(new Eaten(), this.player), mt)
        && t.checkExpect(background1.find2(new Eaten(), this.player), mt)
        && t.checkExpect(background3.find2(new Eaten(), this.player), mt)
        && t.checkExpect(background1.find2(new Uneaten(), this.player), background1)
        && t.checkExpect(mt.find2(new Uneaten(), this.player), mt);
  }
  
  //test the GenerateFish method
  boolean testGenerateFish(Tester t) {
    return t.checkExpect(mt.generateFish(this.rand), new MtLoFish())      
        && t.checkExpect(background2.generateFish(this.rand), 
            new ConsLoFish(new BackgroundFish(500, 250, 49, Color.CYAN, true),
            new ConsLoFish(new BackgroundFish(500, 300, 10, Color.BLUE, true), 
                new ConsLoFish(new BackgroundFish(
                    0, 200, 40, Color.GREEN, false), new MtLoFish()))))
        && t.checkExpect(background3.generateFish(this.rand), 
            new ConsLoFish(new BackgroundFish(500, 291, 66, Color.CYAN, true),
            new ConsLoFish(new BackgroundFish(0, 400, 30, Color.YELLOW, false),
              new ConsLoFish(new BackgroundFish(500, 300, 10, Color.BLUE, true), 
                new ConsLoFish(new BackgroundFish(0, 200, 40, Color.GREEN, false), 
                    new MtLoFish())))));

  }
  
  //test the countFish method
  boolean testCountFish(Tester t) {
    return t.checkExpect(mt.countFish(), 0)
        && t.checkExpect(background2.countFish(), 2)
        && t.checkExpect(background4.countFish(), 4);

  }
  
  //test the drawBackground method
  boolean testDrawBackground(Tester t) {
    return t.checkExpect(this.mt.drawBackground(
        this.s.getEmptyScene()), new WorldScene(0, 0)
        .placeImageXY(new RectangleImage(0, 0, "outline", Color.WHITE), 0, 0))
        && t.checkExpect(this.background1.drawBackground(
            this.s.getEmptyScene()), new WorldScene(0, 0)
            .placeImageXY(new RectangleImage(0, 0, "outline", Color.WHITE), 0, 0)
                .placeImageXY(new RectangleImage(40, 40, "solid", Color.GREEN), 0, 200))
        && t.checkExpect(this.background2.drawBackground(
            this.s.getEmptyScene()), new WorldScene(0, 0)
            .placeImageXY(new RectangleImage(0, 0, "outline", Color.WHITE), 0, 0)
                .placeImageXY(new RectangleImage(40, 40, "solid", Color.BLUE), 0, 200)
                  .placeImageXY(new RectangleImage(40, 40, "solid", Color.GREEN), 0, 200));
  }
  
  //test the backgroundWins method
  boolean testBackgroundWins(Tester t) {
    return t.checkExpect(this.background1.backgroundWins(this.player), false)
        && t.checkExpect(this.background4.backgroundWins(this.player), true)
        && t.checkExpect(this.mt.backgroundWins(this.player), false);
  }
  
  //test the backgroundEaten method
  boolean testBackgroundEaten(Tester t) {
    return t.checkExpect(this.background1.backgroundEaten(this.player), false)
        && t.checkExpect(this.background4.backgroundEaten(this.player), false)
        && t.checkExpect(this.mt.backgroundEaten(this.player), false);
  }
  
  //test the addSize method
  boolean testAddSize(Tester t) {
    return t.checkExpect(this.mt.addSize(), 0)
        && t.checkExpect(this.background1.addSize(), 40)
        && t.checkExpect(this.background2.addSize(), 50);
        
  }
  
  //test the apply method
  boolean testApply(Tester t) {
    return t.checkExpect(new Eaten().apply(
        new BackgroundFish(200, 200, 40, Color.GREEN, false)), false)
        && t.checkExpect(new Uneaten().apply(
            new BackgroundFish(800, 200, 40, Color.GREEN, true)), false)
        && t.checkExpect(new GoingLeft().apply(
            new BackgroundFish(200, 200, 40, Color.GREEN, false)), false)
        && t.checkExpect(new GoingLeft().apply(
            new BackgroundFish(200, 200, 40, Color.GREEN, true)), true)
        && t.checkExpect(new GoingRight().apply(
            new BackgroundFish(200, 200, 40, Color.GREEN, false)), true)
        && t.checkExpect(new GoingRight().apply(
            new BackgroundFish(200, 200, 40, Color.GREEN, true)), false)
        && t.checkExpect(new Onscreen().apply(
            new BackgroundFish(200, 200, 40, Color.GREEN, true)), true)
        && t.checkExpect(new Onscreen().apply(
            new BackgroundFish(800, 200, 40, Color.GREEN, true)), false);

  }
  
  //test the compare method
  boolean testCompare(Tester t) {
    return t.checkExpect(new Eaten().compare(
        new BackgroundFish(200, 200, 40, Color.GREEN, false), 
        new Player(250, 250, 20)), false)
        && t.checkExpect(new Eaten().compare(
            new BackgroundFish(200, 200, 40, Color.GREEN, false), 
            new Player(250, 250, 80)), true)
        && t.checkExpect(new Uneaten().compare(
            new BackgroundFish(200, 200, 40, Color.GREEN, false), 
            new Player(250, 250, 80)), false) 
        && t.checkExpect(new Uneaten().compare(
            new BackgroundFish(200, 200, 40, Color.GREEN, false), 
            new Player(250, 250, 10)), true) 
        && t.checkExpect(new GoingLeft().compare(
            new BackgroundFish(200, 200, 40, Color.GREEN, false),
            new Player(250, 250, 10)), false)
        && t.checkExpect(new GoingRight().compare(
            new BackgroundFish(200, 200, 40, Color.GREEN, false),
            new Player(250, 250, 10)), false)
        && t.checkExpect(new Onscreen().compare(
            new BackgroundFish(200, 200, 40, Color.GREEN, false),
            new Player(250, 250, 10)), false);
  }
  
  //test the changeX method
  boolean testChangeX(Tester t) {
    return t.checkExpect(new BackgroundFish(0, 200, 40, Color.GREEN, false).changeX(), 
        new BackgroundFish(5, 200, 40, Color.GREEN, false))
        && t.checkExpect(new BackgroundFish(500, 100, 40, Color.GREEN, true).changeX(), 
            new BackgroundFish(495, 100, 40, Color.GREEN, true))
        && t.checkExpect(new BackgroundFish(60, 400, 10, Color.BLUE, false).changeX(), 
            new BackgroundFish(65, 400, 10, Color.BLUE, false));
    
  }
  
  //test the playerImage method
  boolean testPlayerImage(Tester t) {
    return t.checkExpect(new Player(250, 250, 25).playerImage(), 
        new RectangleImage(25, 25, "solid", Color.RED))
        && t.checkExpect(new Player(20, 20, 20).playerImage(), 
            new RectangleImage(20, 20, "solid", Color.RED))
        && t.checkExpect(new Player(0, 0, 0).playerImage(), 
            new RectangleImage(0, 0, "solid", Color.RED));
        
  }
  
  //test the backgroundImage method
  boolean testBackgroundImage(Tester t) {
    return t.checkExpect(new BackgroundFish(0, 0, 0, Color.GREEN, false).backgroundImage(), 
        new RectangleImage(0, 0, "solid", Color.GREEN))
        && t.checkExpect(new BackgroundFish(40, 40, 40, Color.BLUE, false).backgroundImage(), 
            new RectangleImage(40, 40, "solid", Color.BLUE))
        && t.checkExpect(new BackgroundFish(100, 100, 100, Color.BLACK, false).backgroundImage(), 
            new RectangleImage(100, 100, "solid", Color.BLACK));
  }
  
  //test the movePlayer method
  boolean testMovePlayer(Tester t) {
    return t.checkExpect(new Player(250, 250, 25).movePlayer("right"), new Player(255, 250, 25))
        && t.checkExpect(new Player(250, 250, 25).movePlayer("left"), new Player(245, 250, 25))
        && t.checkExpect(new Player(250, 250, 25).movePlayer("up"), new Player(250, 245, 25))
        && t.checkExpect(new Player(250, 250, 25).movePlayer("down"), new Player(250, 255, 25))
        && t.checkExpect(new Player(250, 250, 25).movePlayer("macarena"), new Player(250, 250, 25));

  }
  
  //test the makeScene method
  boolean testMakeScene(Tester t) {
    return t.checkExpect(s.makeScene(), new WorldScene(0, 0)
        .placeImageXY(new RectangleImage(0, 0, "outline", Color.WHITE), 0, 0)
        .placeImageXY(new RectangleImage(25, 25, "solid", Color.RED), 250, 250)
        .placeImageXY(new RectangleImage(40, 40, "solid", Color.GREEN), 0, 200))
        && t.checkExpect(new FishyWorld(rands, new Player(
            250, 250, 25), this.mt).makeScene(),
            new WorldScene(0, 0)
            .placeImageXY(new RectangleImage(0, 0, "outline", Color.WHITE), 0, 0)
            .placeImageXY(new RectangleImage(25, 25, "solid", Color.RED), 250, 250))
        && t.checkExpect(new FishyWorld(rands, new Player(
            250, 250, 25), this.background3).makeScene(),
            new WorldScene(0, 0)
            .placeImageXY(new RectangleImage(0, 0, "outline", Color.WHITE), 0, 0)
            .placeImageXY(new RectangleImage(25, 25, "solid", Color.RED), 250, 250));
            
  }
  
  //test the onTick method
  boolean testOnTick(Tester t) { 
    return t.checkExpect(this.s.onTick(), new FishyWorld(
        this.rands, new Player(250, 250, 25), 
        new ConsLoFish(new BackgroundFish(495, 388, 68, Color.CYAN, true),
            new ConsLoFish(new BackgroundFish(5, 200, 40, Color.GREEN, false),
               new MtLoFish()))))
        && t.checkExpect(this.offleft.onTick(), new FishyWorld(
            this.rands, new Player(500, 250, 25), 
            new ConsLoFish(new BackgroundFish(495, 251, 61, Color.CYAN, true),
                new ConsLoFish(new BackgroundFish(5, 200, 40, Color.GREEN, false),
                   new MtLoFish()))))
        && t.checkExpect(this.offright.onTick(), new FishyWorld(
            this.rands, new Player(0, 250, 25), 
            new ConsLoFish(new BackgroundFish(495, 58, 66, Color.CYAN, true),
                new ConsLoFish(new BackgroundFish(5, 200, 40, Color.GREEN, false),
                   new MtLoFish()))))
        && t.checkExpect(this.offtop.onTick(), new FishyWorld(
            this.rands, new Player(250, 0, 25), 
            new ConsLoFish(new BackgroundFish(495, 200, 62, Color.CYAN, true),
                new ConsLoFish(new BackgroundFish(5, 200, 40, Color.GREEN, false),
                   new MtLoFish()))))
        && t.checkExpect(this.offbottom.onTick(), new FishyWorld(
            this.rands, new Player(250, 500, 25), 
            new ConsLoFish(new BackgroundFish(5, 155, 53, Color.CYAN, false),
                new ConsLoFish(new BackgroundFish(5, 200, 40, Color.GREEN, false),
                   new MtLoFish()))));

  }
  
  //test the onKeyEvent method
  boolean testOnKeyEvent(Tester t) {
    return t.checkExpect(this.s.onKeyEvent("right"), new FishyWorld(
        this.rands, new Player(255, 250, 25),
        new ConsLoFish(new BackgroundFish(0, 200, 40, Color.GREEN, false), new MtLoFish())))
        && t.checkExpect(this.s.onKeyEvent("left"), new FishyWorld(
            this.rands, new Player(245, 250, 25),
            new ConsLoFish(new BackgroundFish(0, 200, 40, Color.GREEN, false), new MtLoFish())))
        && t.checkExpect(this.s.onKeyEvent("up"), new FishyWorld(
            this.rands, new Player(250, 245, 25),
            new ConsLoFish(new BackgroundFish(0, 200, 40, Color.GREEN, false), new MtLoFish())))
        && t.checkExpect(this.s.onKeyEvent("down"), new FishyWorld(
            this.rands, new Player(250, 255, 25),
            new ConsLoFish(new BackgroundFish(0, 200, 40, Color.GREEN, false), new MtLoFish())))
        && t.checkExpect(this.s.onKeyEvent("macarena"), new FishyWorld(
            this.rands, new Player(250, 250, 25),
            new ConsLoFish(new BackgroundFish(0, 200, 40, Color.GREEN, false), new MtLoFish())));
  }
  
  //test the move method
  boolean testMove(Tester t) {
    return t.checkExpect(this.mt.move(), this.mt)
        && t.checkExpect(this.background1.move(), new ConsLoFish(
            new BackgroundFish(5, 200, 40, Color.GREEN, false),
            new MtLoFish()))
        && t.checkExpect(this.background2.move(), new ConsLoFish(
            new BackgroundFish(495, 300, 10, Color.BLUE, true), 
            new ConsLoFish(new BackgroundFish(5, 200, 40, Color.GREEN, false), new MtLoFish())));
    
  }
  
  //test the worldEnds method
  boolean testWorldEnds(Tester t) {
    return t.checkExpect(this.fishdead.worldEnds(), new WorldEnd(
        true, this.fishdead.makeScene().placeImageXY(
          new TextImage("A bigger background fish ate you!", 13, FontStyle.BOLD_ITALIC, Color.red),
          250,250)))
        && t.checkExpect(this.fishwin.worldEnds(), new WorldEnd(
            true, this.fishwin.makeScene().placeImageXY(
          new TextImage("YOU WON!!!!!!", 13, FontStyle.BOLD_ITALIC, Color.red),
          100, 250)))
        && t.checkExpect(this.s.worldEnds(), new WorldEnd(
            false, this.s.makeScene()));
        
  }
  
}





//imports
import tester.Tester;
import java.util.Comparator;

//interface for List operations
//we're not using any list operations (filter, fold, map) here, 
//so we just implemented the interface to create the buildList method
interface IList<T> {}

//empty list class
class MtList<T> implements IList<T> {
  
  MtList() {}
 
}

//class for a conslist
class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;
  
  ConsList(T first,IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }
}

//class to represent a book with a title, author, and price
class Book {
  String title;
  String author;
  int price;
  
  Book(String title, String author, int price) {
    this.title = title;
    this.author = author;
    this.price = price;
  }
}

//our abstract class of a binary search tree
abstract class ABST<T> {
  
  //the comparator used for BST's
  Comparator<T> order;
 
  
  ABST(Comparator<T> order) {
    this.order = order;
  }
  
  //methods that use BST's
  abstract ABST<T> insert(T item);
  
  abstract boolean present(T t1);
  
  abstract T getLeftmost();
  
  abstract T getLeftmostHelp(T acc);
  
  abstract ABST<T> getRight();
  
  abstract ABST<T> getRightHelp(T acc);
  
  abstract boolean sameTree(ABST<T> other);
  
  abstract boolean sameTreeHelp(Node<T> other);
  
  abstract boolean sameTreeHelp(Leaf<T> other);
  
  abstract boolean sameData(ABST<T> other);
  
  abstract IList<T> buildList();
  
}

//the leaf case of BST's
class Leaf<T> extends ABST<T> {

  Leaf(Comparator<T> order) {
    super(order);
  }

  //insert an item into the binary search tree
  public ABST<T> insert(T item) {
    return new Node<T>(this.order, item, new Leaf<T>(this.order), 
        new Leaf<T>(this.order));
  }

  //returns whether the given item is present in the BST
  boolean present(T t1) {
    return false;
  }

  //throws a runtime exception if you get the leftmost of an empty tree
  T getLeftmost() {
    throw new RuntimeException("No leftmost item of an empty tree");
  }
  
  //returns the current leftmost of the tree
  T getLeftmostHelp(T acc) {
    return acc;
  }

  //throws a runtime exception if you get the right of an empty tree
  ABST<T> getRight() {
    throw new RuntimeException("No right of an empty tree");
  }
  
  //if the leftmost is a leaf, just return it
  ABST<T> getRightHelp(T acc) {
    return this;
  }

  //dynamically dispatch to confirm other is also a leaf
  boolean sameTree(ABST<T> other) {
    return other.sameTreeHelp(this);
  }
  
  //if other is a node, they're not the same tree
  boolean sameTreeHelp(Node<T> other) {
    return false;
  }

  //if other is a leaf, they're the same tree by our definition
  boolean sameTreeHelp(Leaf<T> other) {
    return true;
  }


  //a leaf will be present in the other tree
  boolean sameData(ABST<T> other) {
    return true;
  }

  //if a leaf called build list you're at the end of the tree
  IList<T> buildList() {
    return new MtList<T>();
  }
}

//the node case of BST's
class Node<T> extends ABST<T> {
  T data;
  ABST<T> left;
  ABST<T> right;
  
  Node(Comparator<T> order, T data, ABST<T> left, ABST<T> right) {
    super(order);
    this.data = data;
    this.left = left;
    this.right = right;
  }

  //insert an item into the binary search tree
  public ABST<T> insert(T item) {
    if (order.compare(item, this.data) < 0) {
      return new Node<T>(this.order, this.data, this.left.insert(item),
          this.right);
    }
    else {
      return new Node<T>(this.order, this.data, this.left, 
          this.right.insert(item));
    }
    
  }

  //returns whether the given item is present in the BST
  boolean present(T t1) {
    return order.compare(t1, this.data) == 0 
        || left.present(t1) 
        || right.present(t1);
  }

  //returns the value of the leftmost item 
  T getLeftmost() {
    return this.getLeftmostHelp(this.data);  
  }
  
  //replaces the accumulator with the current leftmost,
  //leaf class returns the most leftmost 
  T getLeftmostHelp(T acc) {
    return this.left.getLeftmostHelp(this.data);
  }

  //returns all but the leftmost item of the tree
  ABST<T> getRight() {
    return this.getRightHelp(this.getLeftmost());
  }
  
  //checks if you're at the leftmost of the tree, 
  //returns all but the leftmost
  ABST<T> getRightHelp(T leftMost) {
    if (this.order.compare(this.data, leftMost) == 0) {
      return this.right;
    }
    else {
      return new Node<T>(this.order, this.data,
        this.left.getRightHelp(leftMost), this.right);
    }
  }

  //checks whether this BST is the same as the given one
  //that is: they have matching structure and matching data 
  //in all nodes
  boolean sameTree(ABST<T> other) {
    return other.sameTreeHelp(this);
  }
  
  //check if this node is the same as the other
  boolean sameTreeHelp(Node<T> other) {
    return this.order.compare(this.data, other.data) == 0
        && this.left.sameTree(other.left)
        && this.right.sameTree(other.right);
  }
  
  //if this is a node and other is a leaf, they're not the same tree
  boolean sameTreeHelp(Leaf<T> other) {
    return false;
  }

  //check if every node in this is present in other
  boolean sameData(ABST<T> other) {
    return other.present(this.data) 
        && this.left.sameData(other) 
        && this.right.sameData(other);
  }

  //build a list of the elements of the BST in the sorted order
  IList<T> buildList() {
    return new ConsList<T>(this.getLeftmost(), this.getRight().buildList());
  }
 
}

//compare the books by title
class BooksByTitle implements Comparator<Book> {
  public int compare(Book t1, Book t2) {
    return t1.title.compareTo(t2.title);
  }

}
  
//compare the books by author
class BooksByAuthor implements Comparator<Book> {
  public int compare(Book t1, Book t2) {
    return t1.author.compareTo(t2.author);
  }
  
}

//compare the books by price
class BooksByPrice implements Comparator<Book> {
  public int compare(Book t1, Book t2) {
    return t1.price - t2.price;
  }
  
}

//examples of BST's and Tests
class ExamplesABST {
  
  //examples of books
  Book book1 = new Book("Book1", "Author1", 100);
  Book book2 = new Book("Book2", "Author2", 150);
  Book book3 = new Book("Book3", "Author3", 200);
  Book book4 = new Book("Book4", "Author4", 250);
  Book book5 = new Book("Book5", "Author5", 300);
  
  //comparator objects
  Comparator<Book> booksbytitle = new BooksByTitle();
  Comparator<Book> booksbyauthor = new BooksByAuthor();
  Comparator<Book> booksbyprice = new BooksByPrice();
 
  //empty BST leafs
  ABST<Book> leafbbt = new Leaf<Book>(booksbytitle);
  ABST<Book> leafbba = new Leaf<Book>(booksbyauthor);
  ABST<Book> leafbbp = new Leaf<Book>(booksbyprice);
  
  //booksbytitle BST examples
  ABST<Book> bbt1 = new Node<Book>(this.booksbytitle, book1, leafbbt, leafbbt);
  ABST<Book> bbt2 = new Node<Book>(this.booksbytitle, book2, bbt1, leafbbt);
  ABST<Book> bbt4 = new Node<Book>(this.booksbytitle, book4, leafbbt, leafbbt);
  ABST<Book> bbt3 = new Node<Book>(this.booksbytitle, book3, bbt2, bbt4);
  ABST<Book> bbtB = new Node<Book>(this.booksbytitle, this.book3, this.bbt2, this.bbt4);
  ABST<Book> bbtbad = new Node<Book>(this.booksbytitle, book3, bbt4, bbt1);
  ABST<Book> bbtC = new Node<Book>(this.booksbytitle, book2, bbt1, 
      new Node<Book>(this.booksbytitle, book4, 
          new Node<Book>(this.booksbytitle, book3, leafbbt, leafbbt), leafbbt));
  ABST<Book> bbtD = new Node<Book>(this.booksbytitle, book3, bbt1, 
      new Node<Book>(this.booksbytitle, book4, leafbbt,
          new Node<Book>(this.booksbytitle, book5, leafbbt, leafbbt)));
  
  
  //booksbyauthor BST examples
  ABST<Book> bba1 = new Node<Book>(this.booksbyauthor, book1, leafbba, leafbba);
  ABST<Book> bba2 = new Node<Book>(this.booksbyauthor, book2, bba1, leafbba);
  ABST<Book> bba4 = new Node<Book>(this.booksbyauthor, book4, leafbba, leafbba);
  ABST<Book> bba3 = new Node<Book>(this.booksbyauthor, book3, bba2, bba4);
  ABST<Book> bbabad = new Node<Book>(this.booksbyauthor, book3, bba4, bba1);
  
  //booksbyprice BST examples 
  ABST<Book> bbp1 = new Node<Book>(this.booksbyprice, book1, leafbbp, leafbbp);
  ABST<Book> bbp2 = new Node<Book>(this.booksbyprice, book2, bbp1, leafbbp);
  ABST<Book> bbp4 = new Node<Book>(this.booksbyprice, book4, leafbbp, leafbbp);
  ABST<Book> bbp3 = new Node<Book>(this.booksbyprice, book3, bbp2, bbp4);
  ABST<Book> bbpbad = new Node<Book>(this.booksbyprice, book3, bbp4, bbp1);
  
  //examples of IList of books
  IList<Book> mt = new MtList<Book>();
  IList<Book> bbtlistA = new ConsList<Book>(this.book1,
      new ConsList<Book>(this.book2,
          new ConsList<Book>(this.book3,
              new ConsList<Book>(this.book4, this.mt))));
  IList<Book> bbtlistD = new ConsList<Book>(this.book1,
      new ConsList<Book>(this.book3,
          new ConsList<Book>(this.book4,
              new ConsList<Book>(this.book5, this.mt))));
  
  //test the insert method
  boolean testInsert(Tester t) {
    return t.checkExpect(leafbbt.insert(this.book1), this.bbt1)
        && t.checkExpect(bbt2.insert(book4), new Node<Book>(this.booksbytitle,
            book2, bbt1, new Node<Book>(this.booksbytitle, book4, leafbbt, leafbbt)));
  }
  
  //test the present method
  boolean testPresent(Tester t) {
    return t.checkExpect(bbt2.present(book4), false) 
        && t.checkExpect(bbt3.present(book1), true); 
  }
  
  //test the getleftmost method
  boolean testgetLeftmost(Tester t) {
    return t.checkException(new RuntimeException(
        "No leftmost item of an empty tree"), leafbbt, "getLeftmost")
        && t.checkExpect(bbt2.getLeftmost(), book1)
        && t.checkExpect(bbt3.getLeftmost(), book1)
        && t.checkExpect(new Node<Book>(this.booksbytitle, book2, this.leafbbt, 
                new Node<Book>(this.booksbytitle, book4, 
                    new Node<Book>(this.booksbytitle, book3, leafbbt, leafbbt), 
                    leafbbt)).getLeftmost(),
            this.book2);
        
    
  }
  
  //test the getrightmost method
  boolean testGetRight(Tester t) {
    return t.checkException(new RuntimeException(
        "No right of an empty tree"), leafbbt, "getRight")
        && t.checkExpect(bbt2.getRight(), new Node<Book>(this.booksbytitle, book2,
            leafbbt, leafbbt))
        && t.checkExpect(bbt3.getRight(), new Node<Book>(this.booksbytitle, book3,
            new Node<Book>(this.booksbytitle, book2, leafbbt, leafbbt), 
            new Node<Book>(this.booksbytitle, book4, leafbbt, leafbbt)))
        && t.checkExpect(this.bbtC.getRight(), 
            new Node<Book>(this.booksbytitle, book2, this.leafbbt, 
                new Node<Book>(this.booksbytitle, book4, 
                    new Node<Book>(this.booksbytitle, book3, leafbbt, leafbbt), leafbbt)));
    
  }

  //test the sametree method
  boolean testSameTree(Tester t) {
    return t.checkExpect(this.leafbba.sameTree(this.bbt1), false)
        && t.checkExpect(this.leafbbt.sameTree(this.leafbbt), true)
        && t.checkExpect(this.bbt3.sameTree(this.bbtB), true)
        && t.checkExpect(this.bbt1.sameTree(
            new Node<Book>(this.booksbytitle, this.book2, this.leafbbt, this.leafbbt)), false)
        && t.checkExpect(this.bbt3.sameTree(this.bbtC), false)
        && t.checkExpect(this.bbt3.sameTree(this.bbtD), false)
        && t.checkExpect(this.bbt3.sameTree(this.bba3), true);
  }
  
  //test the samedata method
  boolean testSameData(Tester t) {
    return t.checkExpect(this.leafbba.sameData(this.bbt1), true)
        && t.checkExpect(this.leafbbt.sameData(this.leafbbt), true)
        && t.checkExpect(this.bbt3.sameData(this.bbtB), true)
        && t.checkExpect(this.bbt1.sameData(
            new Node<Book>(this.booksbytitle, this.book2, this.leafbbt, this.leafbbt)), false)
        && t.checkExpect(this.bbt3.sameData(this.bbtC), true)
        && t.checkExpect(this.bbt3.sameData(this.bbtD), false)
        && t.checkExpect(this.bbt3.sameData(this.bba3), true);
  }
  
  //test the buildlist method
  boolean testBuildList(Tester t) {
    return t.checkExpect(this.leafbbt.buildList(), this.mt)
        && t.checkExpect(this.bbt3.buildList(), this.bbtlistA)
        && t.checkExpect(this.bbtD.buildList(), this.bbtlistD);
  }
  
  // getleftmosthelp 
  // getrighthelp
  // sametreehelp (nodes and leaves)
  

}












import tester.Tester;
import java.util.function.Function;
import java.util.function.BiFunction;


//class to represent function that negates a given number
class Neg implements Function<Double, Double> {
  public Double apply(Double num) {
    return -1 * num;
  }
}

//class to represent function that squares a given number
class Sqr implements Function<Double, Double> {
  public Double apply(Double num) {
    return num * num;
  }
  
}

//class to represent the function that adds two numbers
class Plus implements BiFunction<Double, Double, Double> {

  public Double apply(Double left, Double right) {
    return left + right;
  }
  
}

//class to represent the function that subtracts two numbers
class Minus implements BiFunction<Double, Double, Double> {

  public Double apply(Double left, Double right) {
    return left - right;
  }

}

//class to represent the function that multiplies two numbers
class Mul implements BiFunction<Double, Double, Double> {

  public Double apply(Double left, Double right) {
    return left * right;
  }

}

//class to represent the function that divides two numbers
class Div implements BiFunction<Double, Double, Double> {

  public Double apply(Double left, Double right) {
    return left / right;
  }

}

//interface to represent an arithmetic expression
interface IArith {
  <R> R accept(IArithVisitor<R> visitor);
}

//class to represent a constant (in this case, a double)
class Const implements IArith {
  double num;
  
  Const(double num) {
    this.num = num;
  }

  @Override
  public <R> R accept(IArithVisitor<R> visitor) {
    return visitor.visitConst(this);
  }
}

//class to represent a unary formula (an arithmetic expression that is applied to one IArith)
class UnaryFormula implements IArith {
  Function<Double, Double> func;
  String name;
  IArith child;
  
  UnaryFormula(Function<Double, Double> func, String name, IArith child) {
    this.func = func;
    this.name = name;
    this.child = child;
  }

  //accepts the visitor for the UF case
  public <R> R accept(IArithVisitor<R> visitor) {
    return visitor.visitUF(this);
  }
}

//class to represent a binary formula (arithmetic expression applied to two IAriths)
class BinaryFormula implements IArith {
  BiFunction<Double, Double, Double> func;
  String name;
  IArith left;
  IArith right;
  
  BinaryFormula(BiFunction<Double, Double, Double> func, String name, IArith left, IArith right) {
    this.func = func;
    this.name = name;
    this.left = left;
    this.right = right;
  }

  //accepts the visitor for the BF case
  public <R> R accept(IArithVisitor<R> visitor) {
    return visitor.visitBF(this);
  }
}

//IArithVistior interface
interface IArithVisitor<R> {
  R apply(IArith arith);
  
  R visitConst(Const c);

  R visitUF(UnaryFormula uf);
  
  R visitBF(BinaryFormula bf);
}

class EvalVisitor implements IArithVisitor<Double> {

  //evaluates the constant case
  public Double visitConst(Const c) {
    return c.num;
  }

  //evaluates the uf case
  public Double visitUF(UnaryFormula uf) {
    return uf.func.apply(this.apply(uf.child));
  }

  //evaluates the bf case
  public Double visitBF(BinaryFormula bf) {
    return bf.func.apply(this.apply(bf.left), this.apply(bf.right));
  }

  //calls accept for the correct case
  public Double apply(IArith arith) {
    return arith.accept(this);
  } 
}

//visits an IArith and produces a string showing fully parenthesized 
//expression in Racket-like prefix notation
class PrintVisitor implements IArithVisitor<String> {

  //string of constant case
  public String visitConst(Const c) {
    return Double.toString(c.num);
  }

  //string of uf case
  public String visitUF(UnaryFormula uf) {
    return "(" + uf.name + " " + this.apply(uf.child) + ")";
  }

  //string of bf case
  public String visitBF(BinaryFormula bf) {
    return "(" + bf.name + " " + this.apply(bf.left) + " " + this.apply(bf.right) + ")";
  }

  //calls accept on the IArith
  public String apply(IArith arith) {
    return arith.accept(this);
  } 
}

//doubles every constant in the tree
class DoublerVisitor implements IArithVisitor<IArith> {

  //doubles the constant 
  public IArith visitConst(Const c) {
    return new Const(2 * c.num);
  }

  //doubles all the constants in the given uf
  public IArith visitUF(UnaryFormula uf) {
    return new UnaryFormula(uf.func, uf.name, this.apply(uf.child));
  }

  //doubles all the constants in the given bf
  public IArith visitBF(BinaryFormula bf) {
    return new BinaryFormula(bf.func, bf.name, this.apply(bf.left), this.apply(bf.right));
  }

  //calls apply on the IArith
  public IArith apply(IArith arith) {
    return arith.accept(this);
  } 
}

//checks if a negative number is ever encountered 
//true if negative never encountered
class NoNegativeResults implements IArithVisitor<Boolean> {

  //check for negative constant
  public Boolean visitConst(Const c) {
    return c.num >= 0.0;
  }

  //check for negative in uf case
  public Boolean visitUF(UnaryFormula uf) {
    IArithVisitor<Double> eval = new EvalVisitor();
    return this.apply(uf.child) && uf.accept(eval) >= 0.0;
    
  }

  //check for negative in bf case
  public Boolean visitBF(BinaryFormula bf) {
    IArithVisitor<Double> eval = new EvalVisitor();
    return this.apply(bf.left) && this.apply(bf.right) && bf.accept(eval) >= 0.0;
    
  }

  //call accept on the given IArith
  public Boolean apply(IArith arith) {
    return arith.accept(this);
  } 
}

//examples for the problem
class ExamplesVisitors {
  
  //constant examples
  IArith cons1 = new Const(1.0);
  IArith cons2 = new Const(2.0);
  IArith cons4 = new Const(4.0);
  IArith cons0 = new Const(0.0);
  IArith cons5 = new Const(5.0);
  IArith cons1Neg = new Const(-1.0);
  IArith cons10Neg = new Const(-10.0);
  
  //uf examples
  IArith uf1 = new UnaryFormula(new Neg(), "neg", this.cons1);
  IArith uf2 = new UnaryFormula(new Sqr(), "sqr", this.cons2);
  
  //bf examples
  IArith bf1 = new BinaryFormula(new Mul(), "mul", this.cons1, this.uf2);
  IArith bf2 = new BinaryFormula(new Plus(), "plus", this.cons1, this.cons0);
  IArith bf3 = new BinaryFormula(new Div(), "div", this.bf1, this.bf2);
  IArith bf4 = new BinaryFormula(new Minus(), "minus", this.cons1, this.cons4);
  IArith bf5 = new BinaryFormula(new Minus(), "minus", this.cons2, new Const(3.0));
  IArith bf6 = new BinaryFormula(new Plus(), "plus", 
      this.cons2, new UnaryFormula(new Neg(), "neg", new Const(3.0)));
  
  //objects to run tests on each of the methods
  IArithVisitor<Double> eval = new EvalVisitor();
  IArithVisitor<String> print = new PrintVisitor();
  IArithVisitor<IArith> doubler = new DoublerVisitor();
  IArithVisitor<Boolean> noNeg = new NoNegativeResults();
  
  //test the eval method
  boolean testEvalApply(Tester t) {
    return t.checkExpect(this.eval.apply(this.cons1), 1.0)
        && t.checkExpect(this.eval.apply(this.uf1), -1.0)
        && t.checkExpect(this.eval.apply(this.uf2), 4.0)
        && t.checkExpect(this.eval.apply(this.bf1), 4.0)
        && t.checkExpect(this.eval.apply(this.bf2), 1.0)
        && t.checkExpect(this.eval.apply(this.bf3), 4.0);
  }
  
  //test the print method
  boolean testPrintApply(Tester t) {
    return t.checkExpect(this.print.apply(this.cons1), "1.0")
        && t.checkExpect(this.print.apply(this.uf1), "(neg 1.0)")
        && t.checkExpect(this.print.apply(this.uf2), "(sqr 2.0)")
        && t.checkExpect(this.print.apply(this.bf1), "(mul 1.0 (sqr 2.0))")
        && t.checkExpect(this.print.apply(this.bf2), "(plus 1.0 0.0)")
        && t.checkExpect(this.print.apply(this.bf3), "(div (mul 1.0 (sqr 2.0)) (plus 1.0 0.0))");
  }
  
  //test the doubler method
  boolean testDoublerApply(Tester t) {
    return t.checkExpect(this.doubler.apply(this.cons1), this.cons2)
        && t.checkExpect(this.doubler.apply(this.uf1), 
            new UnaryFormula(new Neg(), "neg", new Const(2.0)))
        && t.checkExpect(this.doubler.apply(this.uf2), 
            new UnaryFormula(new Sqr(), "sqr", new Const(4.0)))
        && t.checkExpect(this.doubler.apply(this.bf1), 
            new BinaryFormula(new Mul(), "mul", new Const(2.0), 
            new UnaryFormula(new Sqr(), "sqr", new Const(4.0))));
  }
  
  //test the negative results method
  boolean testNoNegativeResultsApply(Tester t) {
    return t.checkExpect(this.noNeg.apply(this.cons1), true)
        && t.checkExpect(this.noNeg.apply(this.cons1Neg), false)
        && t.checkExpect(this.noNeg.apply(this.uf1), false)
        && t.checkExpect(this.noNeg.apply(this.uf2), true)
        && t.checkExpect(this.noNeg.apply(this.bf4), false)
        && t.checkExpect(this.noNeg.apply(this.bf2), true)
        && t.checkExpect(this.noNeg.apply(this.bf5), false)
        && t.checkExpect(this.noNeg.apply(this.bf6), false);
  }
  
  
  
  
}
//imports
import java.util.*;
import tester.Tester;

/**
 * A class that defines a new permutation code, as well as methods for encoding
 * and decoding of the messages that use this code.
 */
class PermutationCode {

  // The original list of characters to be encoded
  ArrayList<Character> alphabet = new ArrayList<Character>(
      Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 
          'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
          'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'));

  // define the code arraylist
  ArrayList<Character> code = new ArrayList<Character>(26);

  // A random number generator
  Random rand = new Random();

  // Create a new instance of the encoder/decoder with a new permutation code
  PermutationCode() {
    this.code = this.initEncoder();
  }

  PermutationCode(Random rand) {
    this.rand = rand;
  }

  // Create a new instance of the encoder/decoder with the given code
  PermutationCode(ArrayList<Character> code) {
    this.code = code;
  }

  // Initialize the encoding permutation of the characters
  ArrayList<Character> initEncoder() {
    ArrayList<Character> copy = new ArrayList<Character>(26);
    for (int i = 0; i < alphabet.size(); i++) {
      copy.add(i, this.alphabet.get(i));
    }
    for (int j = 0; j < alphabet.size(); j++) {
      this.code.add(j, copy.remove(this.rand.nextInt(copy.size())));
    }
    return this.code;
  }

  // produce an encoded String from the given String
  String encode(String source) {
    return code(source, this.code, this.alphabet);
  }

  // produce a decoded String from the given String
  String decode(String code) {
    return code(code, this.alphabet, this.code);
  }
 
  //abstraction to transcribe a sample from one "alphabet" to another
  String code(String sample, ArrayList<Character> l1, ArrayList<Character> l2) {
    String newString = "";
    for (int i = 0; i < sample.length(); i++) {
      newString += l1.get(l2.indexOf(sample.charAt(i)));
    }
    return newString;
  }
}

//examples for permutations
class ExamplesPermutations {
  ArrayList<Character> code = new ArrayList<Character>(
      Arrays.asList('b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
          'k', 'l', 'm', 'n', 'o', 'p', 'q',
          'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'a'));
  ArrayList<Character> randomKey = new ArrayList<Character>(
      Arrays.asList('p', 'f', 'x', 'n', 'v', 'q', 'w', 'r', 'd', 
          'i', 'h', 'z', 'b', 't', 'g', 'l', 'o', 'c', 'y', 's', 
          'u', 'm', 'e', 'k', 'a', 'j'));

  // code examples
  PermutationCode perm1 = new PermutationCode(this.code);
  Random rand1 = new Random(10);
  Random rand2 = new Random(1);
  PermutationCode randomTest1 = new PermutationCode(this.rand1);
  PermutationCode randomTest2 = new PermutationCode(this.rand2);
  PermutationCode randomWithKey = new PermutationCode(this.randomKey);

  // test the decode method
  boolean testDecode(Tester t) {
    return t.checkExpect(this.perm1.decode("bcfg"), "abef") &&
           t.checkExpect(this.perm1.decode("hello"), "gdkkn") &&
           t.checkExpect(this.randomWithKey.decode("pfxnv"), "abcde");
  }

  // test the encode method
  boolean testEncode(Tester t) {
    return t.checkExpect(this.perm1.encode("abcde"), "bcdef") &&
           t.checkExpect(this.perm1.encode("gdkkn"), "hello") &&
           t.checkExpect(this.randomWithKey.encode("abcde"), "pfxnv");
  }

  // test the initEncoder method
  boolean testInitEncoder(Tester t) {
    return t.checkExpect(this.randomTest1.initEncoder(), this.randomKey) &&
           t.checkExpect(this.randomTest2.initEncoder(), new ArrayList<Character>(
               Arrays.asList('r', 'n', 'h', 'o', 'y', 'q', 't', 'u', 'l', 
                   'v', 'a', 'x', 'g', 'i', 'k', 'c', 'e', 'j', 'z', 's', 
                   'f', 'm', 'd', 'w', 'b', 'p')));
  }
  
  boolean testcode(Tester t) {
    return t.checkExpect(this.perm1.code("", this.code, this.randomKey), "") &&
           t.checkExpect(this.perm1.code("c",this.code, this.randomKey), "s");
    
  }
}




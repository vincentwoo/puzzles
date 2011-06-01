import java.io.FileInputStream;
import java.io.IOException;
import java.lang.Math;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Spellcheck {

  public Spellcheck(String dictionaryPath) {
    this.dictionaryPath = dictionaryPath;
    this.dictionary = new HashMap<String,String>();
    this.actualDictionary = new HashMap<String,String>();
    loadDictionary();
  }

  String dictionaryPath; // Path to file of dictionary word list. e.g. "/usr/share/dict/words"
  Map<String,String> dictionary; // Reduced word form to dictionary spelling mapping. e.g. "h*b*rt" => "Hubert"
  Map<String,String> actualDictionary; // Lower-cased word to dictionary spelling maping. e.g. "hubert" => "Hubert"
 
  /**
   * Used internally to populate dictionary from dictionaryPath.
   * Complexity: O(n) where n is the number of words in the dictionary.
   */ 
  private void loadDictionary() {
    Scanner scanner = null;
    try {
      scanner = new Scanner(new FileInputStream(dictionaryPath));
      while(scanner.hasNextLine()) {
        String word = scanner.nextLine();
        dictionary.put(reducedWordForm(word), word);
        actualDictionary.put(word.toLowerCase(), word);
      }
    } catch(IOException e) {
      System.err.println(e.toString());
      e.printStackTrace();
    } finally {
      if(scanner != null) {
        scanner.close();
      }
    }
  }

  /**
   * Query the spellchecker with a given input string and returns a suggestion. 
   * If no word can be suggested, "NO SUGGESTION" will be returned.
   * Complexity: O(n) where n is the length of the input string
   */
  public String spellcheck(String word) {
    // Check if the word is actually in the dictionary or if there are capitalization errors
    // to prevent against occurrences like "food" being corrected to "fad"
    String capitalizedWord = actualDictionary.get(word.toLowerCase());
    if(capitalizedWord != null) {
      return capitalizedWord;
    }
    // Check against the reduced word form dictionary for a next best guess
    String reducedWordForm = reducedWordForm(word);
    String dictionaryLookup = dictionary.get(reducedWordForm);
    if(dictionaryLookup != null) {
      return dictionaryLookup;
    }
    return "NO SUGGESTION";
  }
  
  /**
   * Returns the reduced word form for a given input string where all letters are transformed in the following steps:
   * 1. All letters set to lower-case
   * 2. All vowels are converted to '*'
   * 3. All duplicate letters are removed
   * Complexity: O(n) where n is the length of the input string
   */
  public static String reducedWordForm(String word) {
    return reduceDuplicateLetters(reduceVowels(word.toLowerCase()));
  }

  /**
   * Internally used to change all vowels into '*' symbols.
   * i.e. "food" => "f**d"
   * Complexity: O(n) where n is the length of the input string
   */
  public static String reduceVowels(String word) {
    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < word.length(); i++) {
      char c = word.charAt(i);
      if(isVowel(c)) {
        sb.append("*");
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  /**
   * Internally used to remove all instances of duplicate letters.
   * i.e. "f**d" => "f*d"
   * Complexity: O(n) where n is the length of the input string   
   */  
  public static String reduceDuplicateLetters(String word) {
    StringBuffer sb = new StringBuffer();
    if(word.length() <= 1) {
      return word;
    }
    char lastCharacter = word.charAt(0);
    sb.append(lastCharacter);
    for(int i = 1; i < word.length(); i++) {
      char c = word.charAt(i);
      if(c != lastCharacter) {
        sb.append(c);
        lastCharacter = c;
      }
    }
    return sb.toString();
  }

  /**
   * Check if the given character is an English vowel (a, e, i, o, u).
   */
  private static boolean isVowel(char c) {
    return ((c == 'a') || (c == 'e') || (c == 'i') || (c == 'o') || (c == 'u'));
  }

  public static void main(String[] args) {
    String dictionaryPath = "/usr/share/dict/words";
    if(args.length == 2 && "-d".equals(args[0])) {
      dictionaryPath = args[1];
    }
    Spellcheck sc = new Spellcheck(dictionaryPath);
    String input;
    String prompt = "> ";
    System.out.print(prompt);
    Scanner scanner = new Scanner(System.in);
    while(scanner.hasNextLine()) {
      input = scanner.nextLine();
      System.out.println(sc.spellcheck(input));
      System.out.print(prompt);
    }
    System.out.println();
  }
}

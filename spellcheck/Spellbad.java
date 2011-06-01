import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Spellbad {
  
  public Spellbad(String dictionaryPath) {
    this.dictionaryPath = dictionaryPath;
  }
  
  String dictionaryPath;
  static Random random = new Random(System.currentTimeMillis());
  static int additionalLetters = 1; // Maximum number of additional letters to randomly add
  static String[] vowels = {"a", "e", "i", "o", "u"};
  
  public String spellbad() {
    Scanner scanner = null;
    String chosenWord = null;
    int i = 2;
    try {
      scanner = new Scanner(new FileInputStream(dictionaryPath));
      while(scanner.hasNextLine()) {
        String word = scanner.nextLine();
        if(chosenWord == null) {
          chosenWord = word;
        } else {
          if(random.nextFloat() <= 1.0/i) {
            chosenWord = word;
          }
        }
        i++;
      }
    } catch(IOException e) {
      System.err.println(e.toString());
      e.printStackTrace();
    } finally {
      if(scanner != null) {
        scanner.close();
      }
    }
    return changeCase(changeVowels(repeatLetters(chosenWord.toLowerCase())));
  }
  
  public static String changeCase(String word) {
    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < word.length(); i++) {
      char c = word.charAt(i);
      if(random.nextFloat() <= 0.5) {
        sb.append(Character.toString(c).toLowerCase());
      } else {
        sb.append(Character.toString(c).toUpperCase()); 
      }
    }
    return sb.toString();
  }
  
  public static String changeVowels(String word) {
    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < word.length(); i++) {
      char c = word.charAt(i);
      if(isVowel(c)) {
        if(random.nextFloat() <= 0.5) {
          String randomVowel = vowels[random.nextInt(vowels.length)];
          sb.append(randomVowel);
        } else {
          sb.append(c);
        }
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }
  
  public static String repeatLetters(String word) {
    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < word.length(); i++) {
      char c = word.charAt(i);
      sb.append(c);
      for(int j = 0; j < random.nextInt(additionalLetters + 1); j++) {
        if(random.nextFloat() <= 0.5) {
          sb.append(c);
        }
      }
    }
    return sb.toString();
  }
  
  public static boolean isVowel(char c) {
    return ((c == 'a') || (c == 'e') || (c == 'i') || (c == 'o') || (c == 'u'));
  }
  
  public static void main(String[] args) {
    String dictionaryPath = "/usr/share/dict/words";
    if(args.length == 2 && "-d".equals(args[0])) {
      dictionaryPath = args[1];
    }
    String word = new Spellbad(dictionaryPath).spellbad();
//    System.err.println(word);
    System.out.println(word);
  }
}
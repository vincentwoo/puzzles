import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

public class WordFriends {

  public static int hammingDistance(String word1, String word2) throws IllegalArgumentException {
    if(word1.length() != word2.length()) {
      throw new IllegalArgumentException("Can't calculate Hamming distance between 2 words of unequal length: \"" + word1 + "\" and \"" + word2 + "\"");
    }
    int distance = 0;
    for(int i = 0; i < word1.length(); i++) {
      if(word1.charAt(i) != word2.charAt(i)) {
        distance++;
      }
    }
    return distance;
  }

  public static int fastHammingDistance(String word1, String word2, int stop) {
    int distance = 0;
    for(int i = 0; i < word1.length(); i++) {
      if(word1.charAt(i) != word2.charAt(i)) {
        distance++;
      }
      if(distance == stop) {
        return distance;
      }
    }
    return distance;
  }


  public static Set<String> oneCharacterRemovedSet(String word) {
    Set<String> set = oneCharacterRemovedSetCache.get(word);
    if(set == null) {
      set = new HashSet<String>();
      for(int i = 0; i < word.length(); i++) {
        if(i == 0) {
      	  set.add(word.substring(1, word.length()));
        } else if(i == word.length() - 1) {
          set.add(word.substring(0, word.length()-1));
        } else {
          set.add(word.substring(0, i) + word.substring(i+1, word.length()));
        }
      }
      oneCharacterRemovedSetCache.put(word, set);
    }
    return set;
  }

  public WordFriends(String word) {
    this.word = word;
    this.wordList = new HashSet<String>();
    this.wordFriends = new HashMap<String,String>();
    this.queue = new LinkedList<String>();
    queue.add(word);
  }

  public void loadWordList() {
    Scanner scanner = null;
    try {
      scanner = new Scanner(new FileInputStream("word.list"));
      while(scanner.hasNextLine()) {
        wordList.add(scanner.nextLine());
      }
    } catch(Exception e) {
      System.err.println(e.toString());
      e.printStackTrace();
    } finally {
      if(scanner != null) {
        scanner.close();
      }
    }
    wordList.remove("causes");
  }

  static Map<String,Set<String>> oneCharacterRemovedSetCache = new HashMap<String,Set<String>>();
  String word;
  Set<String> wordList;
  Map<String,String> wordFriends;
  Queue<String> queue;

  public void findWordFriends() {
    while(queue.size() != 0) {
      String word = queue.remove();
      Set<String> wordListCopy = new HashSet<String>(wordList);
      Iterator<String> wordListIterator = wordListCopy.iterator();
      while(wordListIterator.hasNext()) {
        String wordFromWordList = wordListIterator.next();
      	if(!wordFriends.keySet().contains(wordFromWordList)) {
          if(wordFriends(word, wordFromWordList)) {
            System.out.println(wordFromWordList);
            wordFriends.put(wordFromWordList, word);
            queue.add(wordFromWordList);
            wordList.remove(wordFromWordList);
          }
        }
      }
    }
  }
  
  public static boolean wordFriends(String word1, String word2) {
    if(word1.length() == word2.length()) {
      return fastHammingDistance(word1, word2, 2) == 1;
    } else if(word1.length() == word2.length()+1) {
      return oneCharacterRemovedSet(word1).contains(word2);
    } else if(word2.length() == word1.length()+1) {
      return oneCharacterRemovedSet(word2).contains(word1);
    } else {
      return false;
    }
  }

  public void printWordFriends() {
    System.out.println("Count: " + wordFriends.keySet().size());
  }

  public static void main(String args[]) {
    WordFriends wf = new WordFriends("causes");
    wf.loadWordList();
    wf.findWordFriends();
  }

}


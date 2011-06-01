# Problem

    Write a program that reads a large list of English words (e.g. from /usr/share/dict/words on a unix system) into memory, and then reads words from stdin, and prints either the best spelling suggestion, or "NO SUGGESTION" if no suggestion can be found. The program should print ">" as a prompt before reading each word, and should loop until killed.
    
    Your solution should be faster than O(n) per word checked, where n is the length of the dictionary. That is to say, you can't scan the dictionary every time you want to spellcheck a word.
    
    For example:
    
    > sheeeeep
    sheep
    > peepple
    people
    > sheeple
    NO SUGGESTION
    The class of spelling mistakes to be corrected is as follows:
    
    Case (upper/lower) errors: "inSIDE" => "inside"
    Repeated letters: "jjoobbb" => "job"
    Incorrect vowels: "weke" => "wake"
    Any combination of the above types of error in a single word should be corrected (e.g. "CUNsperrICY" => "conspiracy").
    
    If there are many possible corrections of an input word, your program can choose one in any way you like. It just has to be an English word that is a spelling correction of the input by the above rules.
    
    Final step: Write a second program that *generates* words with spelling mistakes of the above form, starting with correctly spelled English words. Pipe its output into the first program and verify that there are no occurrences of "NO SUGGESTION" in the output.

# Build instructions

This has been built and tested on Sun's JDK 1.6.0_24 on Mac OS X 10.6.7. It has not been tested with OpenJDK or any other OS, but I don't suspect that there would be any problems as the code is very straightforward Java.

/path/to/your/javac Spellcheck.java Spellbad.java

# Usage

## To exit the interactive prompt

Ctrl-D (EOF terminator) will exit out of the program.

## To run the interactive spellchecker with the default dictionary at /usr/share/dict/words

/path/to/your/java Spellcheck

## To run the interactive spellchecker with a custom dictionary

/path/to/your/java Spellcheck -d /path/to/your/custom/dictionary

## To run the word generator with the default directory at /usr/share/dict/words

/path/to/your/java Spellbad

## To run the word generator with a custom dictionary

/path/to/your/java Spellbad -d /path/to/your/custom/dictionary

## To test the spellchecker with the word generator

/path/to/your/java Spellbad | /path/to/your/java Spellcheck

# Samples

    hubert:spellcheck hwong$ java -version
    java version "1.6.0_24"
    Java(TM) SE Runtime Environment (build 1.6.0_24-b07-334-10M3326)
    Java HotSpot(TM) 64-Bit Server VM (build 19.1-b02-334, mixed mode)
    hubert:spellcheck hwong$ java Spellcheck
    > sheeeeep
    shop
    > peepple
    popple
    > sheeple
    NO SUGGESTION
    > inSIDE
    inside
    > jjoobbb
    job
    > weke
    woke
    > CUNsperrICY
    conspiracy
    > LLYyMPPhAANGiOeSIRciomAa
    lymphangiosarcoma
    > TrrICheOPHOONa
    tracheophone
    > ImMMoEdIoAAttO
    immediate
    > ^D
    hubert:spellcheck hwong$

# Description

## Spellchecker

The spellchecker runs in O(1) time, relative to the number of words in the dictionary, for each spellcheck query. A hash table of all the words in the dictionary (in reduced word form, explained below) is kept in memory, so a query essentially boils down to a hash table lookup. The in-memory hash table is generated when the program first starts so the dictionary is only ever read once per process. Relative to the length of the input, the spellchecker runs in O(n), where n is the length of the input string, to compute the hash code.

### Reduced word form

Since the problem does not specify what should be preferred in the case of an input such as "faad", of which there could be multiple outputs (e.g "fad", "food", or "feed"; there are more but I'm not going to list them all out), we need a method of resolving these duplicates. My solution is what I'm calling the reduced word form of the word. To generate the reduced word form, the following procedure is done:

1. Change all letters to lower-case (e.g. "CUNsperrICY" => "cunsperricy")
2. Remove all duplicates letters (e.g. "cunsperricy" => "cunspericy")
3. Replace all vowels with '*' (e.g. "cunspericy" => "c*nsp*r*cy")

By using the reduced word form of words from the dictionary as keys in a hash map with values of the proper spellings, we are able to maintain a spelling suggestion for any reduced word form that can be derived from input.

### Dictionary preparation

The dictionary preparation process is:

1. Get file handle to dictionary file
2. Read each line of the dictionary
3. Get the reduced word form of the word
4. Store the word in a hash map keyed by reduced word form 
5. Store the word in another hash map keyed by the word in lower-case

### Spellcheck query

The spellchecking process works as follows:

1. Calculate the lower-case form of the word
2. Look up the word in the hash map keyed by words in lower-case
3. If the lookup from (2) is successful, return the result
4. Otherwise, calculate the reduced word form of the word
5. Look up the word in the hash map keyed by reduced word forms
6. If the lookup from (5) is successful, return the result
7. Otherwise, return "NO SUGGESTION"

The purpose of keeping the hash map keyed by lower-case words is mostly for my own personal cosmetic taste. It solves the problem of "food" being corrected to "fad" which is technically allowed per the problem specification, but I'd rather it return the actual word, especially if it is a real dictionary word. However, I could cut the memory footprint in half without it.

## Word generator

The word generator operates as follows:

1. Pick a word randomly from the dictionary in O(n) with each word having probability 1/n of being picked
2. Mutate the word by giving each character in the word has a probability 1/2 of being duplicated
3. Each vowel in the resulting "word" has a probability 1/2 of being transformed into another vowel or itself
4. Each character in the resulting "word" has a probability 1/2 of being capitalized
5. Output the final mangled "word" to STDOUT

# Notes and next steps

1. The default dictionary on OS X (/usr/share/dict/words) has a crappy dictionary.
2. Use the same dictionary during testing :)
3. Have not tested with spaces, but it should work.
4. There is no validation on the dictionary file yet. Fix this.
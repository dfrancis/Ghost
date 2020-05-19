/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        String retWord = null;
        if (prefix.isEmpty()) {
            Random rand = new Random();
            retWord = words.get(rand.nextInt(words.size()));
        }
        else {
            //
            // Binary search to find prefix in words
            //
            Log.d("GHST", "Binary search start: prefix=" + prefix);
            int loIdx = 1;
            int hiIdx = words.size();
            int midIdx = loIdx + (hiIdx - loIdx) / 2;
            boolean done = false;
            while (!done) {
                if (hiIdx >= loIdx) {
                    Log.d("GHST", "Binary search lo=" + loIdx + " hiIdx=" + hiIdx);
                    String midWord = words.get(midIdx);
                    String midWordSubstr = midWord.substring(0, Math.min(midWord.length(), prefix.length()));
                    Log.d("GHST", "Binary search midWord=" + midWord + " midWordSubstr=" + midWordSubstr);
                    if (prefix.compareTo(midWordSubstr) > 0) {
                        loIdx = midIdx + 1;
                        midIdx = loIdx + (hiIdx - loIdx) / 2;
                    } else if (prefix.compareTo(midWordSubstr) < 0) {
                        hiIdx = midIdx - 1;
                        midIdx = loIdx + (hiIdx - loIdx) / 2;
                    } else {
                        retWord = midWord;
                        done = true;
                    }
                }
                else {
                    retWord = null;
                    done = true;
                }
            }
        }
        Log.d("GHST", "Binary search end");
        return retWord;
    }

    @Override
    public String getGoodWordStartingWith(String prefix) {
        String selected = null;

        if (prefix.isEmpty()) {
            Random rand = new Random();
            selected = words.get(rand.nextInt(words.size()));
        }
        else {
            //
            // Binary search to find prefix in words
            //
            Log.d("GHST", "Binary search start: prefix=" + prefix);
            int loIdx = 1;
            int hiIdx = words.size();
            int midIdx = loIdx + (hiIdx - loIdx) / 2;
            boolean done = false;
            while (!done) {
                if (hiIdx >= loIdx) {
                    Log.d("GHST", "Binary search lo=" + loIdx + " hiIdx=" + hiIdx);
                    String midWord = words.get(midIdx);
                    String midWordSubstr = midWord.substring(0, Math.min(midWord.length(), prefix.length()));
                    Log.d("GHST", "Binary search midWord=" + midWord + " midWordSubstr=" + midWordSubstr);
                    if (prefix.compareTo(midWordSubstr) > 0) {
                        loIdx = midIdx + 1;
                        midIdx = loIdx + (hiIdx - loIdx) / 2;
                    } else if (prefix.compareTo(midWordSubstr) < 0) {
                        hiIdx = midIdx - 1;
                        midIdx = loIdx + (hiIdx - loIdx) / 2;
                    } else {
                        selected = midWord;
                        done = true;
                    }
                }
                else {
                    selected = null;
                    done = true;
                }
            }

            if (selected != null) {
                //
                // Find the full range of words matching prefix
                //
                ArrayList<String> goodWordsEven = new ArrayList<>();
                ArrayList<String> goodWordsOdd = new ArrayList<>();
                int rangeLo = midIdx;
                int rangeHi = midIdx + 1;
                while (rangeLo > 0) {
                    String word = words.get(rangeLo);
                    if ((word.length() < prefix.length()) || (word.substring(0, prefix.length()).compareTo(prefix) != 0)) {
                        break;
                    }
                    if (word.length() % 2 == 0) {
                        goodWordsEven.add(word);
                    }
                    else {
                        goodWordsOdd.add(word);
                    }
                    --rangeLo;
                }
                while (rangeHi <= words.size()) {
                    String word = words.get(rangeHi);
                    if ((word.length() < prefix.length()) || (word.substring(0, prefix.length()).compareTo(prefix) != 0)) {
                        break;
                    }
                    if (word.length() % 2 == 0) {
                        goodWordsEven.add(word);
                    }
                    else {
                        goodWordsOdd.add(word);
                    }
                    ++rangeHi;
                }

                Random rand = new Random();
                // TODO: choose even or odd depending on who went first
                selected = goodWordsEven.get(rand.nextInt(goodWordsEven.size()));
            }
        }
        Log.d("GHST", "Binary search end");

        return selected;
    }
}

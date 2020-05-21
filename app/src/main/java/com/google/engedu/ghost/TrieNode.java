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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;


public class TrieNode {
    private HashMap<String, TrieNode> children;
    private boolean isWord;
    private Random rand = new Random();

    public TrieNode() {
        children = new HashMap<>();
        isWord = false;
    }

    public void add(String s) {
        TrieNode currNode = this;
        TrieNode nextNode = null;
        for (int i = 0; i < s.length() - 1; ++i) {
            String substr = s.substring(0, i + 1);
            if (!currNode.children.containsKey(substr)) {
                nextNode = new TrieNode();
                currNode.children.put(substr, nextNode);
            }
            else {
                nextNode = currNode.children.get(substr);
            }
            currNode = nextNode;
        }

        if (!currNode.children.containsKey(s)) {
            nextNode = new TrieNode();
            nextNode.isWord = true;
            currNode.children.put(s, nextNode);
            // Log.d("GHST", "Added " + s);
        }
    }

    public boolean isWord(String s) {
        boolean retval = false;
        TrieNode currNode = this;
        TrieNode nextNode = null;
        for (int i = 0; i < s.length() - 1; ++i) {
            String substr = s.substring(0, i + 1);
            if (!currNode.children.containsKey(substr)) {
                currNode = null;
                break;
            }
            currNode = currNode.children.get(substr);
        }

        if ((currNode != null) && currNode.children.containsKey(s) && currNode.children.get(s).isWord) {
            retval = true;
        }

        return retval;
    }

    public String getAnyWordStartingWith(String s) {
        String anyWord = null;
        boolean foundPrefix = false;
        TrieNode currNode = this;
        TrieNode nextNode = null;
        if (s.length() == 0) {
            foundPrefix = true;
            int randIdx = rand.nextInt(currNode.children.size());
            Iterator hmIter = currNode.children.entrySet().iterator();
            Map.Entry mapElem = null;
            for (int idx = 0; idx < randIdx; ++idx) {
                mapElem = (Map.Entry)hmIter.next();
            }
            currNode = (TrieNode)mapElem.getValue();
        }
        else {
            for (int i = 0; i < s.length(); ++i) {
                String substr = s.substring(0, i + 1);
                if (!currNode.children.containsKey(substr)) {
                    break;
                }
                currNode = currNode.children.get(substr);
            }
            foundPrefix = true;
        }

        if (foundPrefix) {
            boolean foundWord = false;
            while (!foundWord) {
                if (currNode.children.size() == 0) {
                    break;
                }

                int randIdx = rand.nextInt(currNode.children.size());
                Iterator hmIter = currNode.children.entrySet().iterator();
                Map.Entry mapElem = (Map.Entry)hmIter.next();
                for (int idx = 0; idx < randIdx; ++idx) {
                    mapElem = (Map.Entry)hmIter.next();
                }
                currNode = (TrieNode)mapElem.getValue();
                if (currNode.isWord) {
                    foundWord = true;
                    anyWord = (String)mapElem.getKey();
                    Log.d("GHST", "anyWord = " + anyWord);
                }
            }
        }
        return anyWord;
    }

    public String getGoodWordStartingWith(String s) {
        String anyWord = null;
        boolean foundPrefix = false;
        TrieNode currNode = this;
        TrieNode nextNode = null;
        if (s.length() == 0) {
            foundPrefix = true;
            int randIdx = rand.nextInt(currNode.children.size());
            Iterator hmIter = currNode.children.entrySet().iterator();
            Map.Entry mapElem = null;
            for (int idx = 0; idx < randIdx; ++idx) {
                mapElem = (Map.Entry)hmIter.next();
            }
            currNode = (TrieNode)mapElem.getValue();
        }
        else {
            foundPrefix = true;
            for (int i = 0; i < s.length(); ++i) {
                String substr = s.substring(0, i + 1);
                if (!currNode.children.containsKey(substr)) {
                    foundPrefix = false;
                    Log.d("GHST", "Failed to find " + substr);
                    break;
                }
                currNode = currNode.children.get(substr);
            }
        }

        if (foundPrefix) {
            boolean foundWord = false;
            String firstRandWord = null;
            int randIdx = rand.nextInt(currNode.children.size());
            Iterator hmIter = currNode.children.entrySet().iterator();
            Map.Entry mapElem = (Map.Entry)hmIter.next();
            for (int idx = 0; idx < randIdx; ++idx) {
                mapElem = (Map.Entry)hmIter.next();
            }
            nextNode = (TrieNode)mapElem.getValue();
            firstRandWord = (String)mapElem.getKey();
            if (! nextNode.isWord) {
                foundWord = true;
                anyWord = (String)mapElem.getKey();
            }

            if (!foundWord) {
                hmIter = currNode.children.entrySet().iterator();
                while (hmIter.hasNext()) {
                    mapElem = (Map.Entry)hmIter.next();
                    nextNode = (TrieNode)mapElem.getValue();
                    if (!nextNode.isWord) {
                        foundWord = true;
                        anyWord = (String)mapElem.getKey();
                    }
                }
            }

            if (!foundWord) {
                anyWord = firstRandWord;
            }
        }
        Log.d("GHST", "anyWord = " + anyWord);
        return anyWord;
    }
}

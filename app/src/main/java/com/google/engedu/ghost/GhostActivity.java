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

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String COMPUTER_WINS = "Computer wins";
    private static final String WORD_INVALID = "Word invalid";
    private static final String USER_TURN = "Your turn";
    private static final String USER_WINS = "You win";
    private static final String VALID_WORD = "Complete word";
    private static final int MIN_WORD_LENGTH = 4;
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private boolean userTurnFirst = false;
    private Random random = new Random();
    private String wordFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();

        try {
            InputStream inStream = assetManager.open("words.txt");
            dictionary = new SimpleDictionary(inStream);
        }
        catch (Exception e) {
            // TODO
        }
        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        userTurnFirst = userTurn;
        TextView text = (TextView) findViewById(R.id.ghostText);
        wordFragment = new String();
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        //
        // Do computer turn stuff then make it the user's turn again
        //

        //
        // If user formed a complete word, declare victory for the computer
        //
        if (dictionary.isWord(wordFragment) && wordFragment.length() >= MIN_WORD_LENGTH) {
            label.setText(COMPUTER_WINS);
        }
        else {
            //
            // If no word can be formed from the current word fragment, also declare victory
            // for the computer
            //
            // String longerWord = dictionary.getAnyWordStartingWith(wordFragment);
            String longerWord = dictionary.getGoodWordStartingWith(wordFragment, userTurnFirst);
            if (longerWord == null) {
                label.setText(WORD_INVALID + " " + COMPUTER_WINS);
            }
            else {
                //
                // Add a letter to the word fragment and give the user a turn
                //
                wordFragment = longerWord.substring(0, wordFragment.length() + 1);
                TextView text = (TextView) findViewById(R.id.ghostText);
                text.setText(wordFragment);

                userTurn = true;
                label.setText(USER_TURN);
            }
        }
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (! Character.isLetter(event.getUnicodeChar())) {
            return super.onKeyUp(keyCode, event);
        }
        TextView label = (TextView) findViewById(R.id.gameStatus);
        wordFragment += (char) event.getUnicodeChar();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText(wordFragment);
        if (dictionary.isWord(wordFragment)) {
            label.setText(VALID_WORD);
        }

        computerTurn();
        return true;
    }

    public boolean onPressChallenge(View view) {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if ((wordFragment.length() >= MIN_WORD_LENGTH) && (dictionary.isWord(wordFragment))) {
            label.setText(USER_WINS);
        }
        else {
            String possibleWord = dictionary.getAnyWordStartingWith(wordFragment);
            if (possibleWord != null) {
                label.setText(COMPUTER_WINS + " " + possibleWord);
            }
            else {
                label.setText(USER_WINS);
            }
        }
        return true;
    }
}

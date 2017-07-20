package com.xyphoid.kanagrammer

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import kotlin.collections.HashMap;
import kotlin.collections.Map;
import kotlin.collections.Set;

import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : AppCompatActivity() {

    internal var results: Map<String, Int>? = null
    internal var handler: Handler? = null
    internal lateinit var output: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolbar)
        val assetManager = assets
        val mainDictionary: HashMap<String, String>

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        try {
            val input = assetManager.open("enablenew.bin")
            val buttonAll = findViewById<View>(R.id.buttonAll) as Button
            val buttonExact = findViewById<View>(R.id.buttonExact) as Button
            val buttonMultiword = findViewById<View>(R.id.buttonMultiword) as Button
            val textQuery = findViewById<View>(R.id.textQuery) as EditText
            val textMultiword = findViewById<View>(R.id.textMultiword) as EditText
            val anagrams = findViewById<View>(R.id.textResults) as TextView
            val mainActivity = this
            this.output = anagrams

            Log.d("Anagrammer2:", "Trying to deserialize database...")
            try {
                //FileInputStream fileIn = new FileInputStream(location);
                val `in` = ObjectInputStream(input)
                mainDictionary = `in`.readObject() as HashMap<String, String>
                `in`.close()
            } catch (i: IOException) {
                i.printStackTrace()
                return
            } catch (c: ClassNotFoundException) {
                println("Hashmap class not found")
                c.printStackTrace()
                return
            }

            Log.d("Anagrammer2:", "Deserialization successful.")


            buttonAll.setOnClickListener {
                anagrams.text = ""
                val solverArgs = SolverArgs(mainDictionary, mainActivity, textQuery.text.toString(), null)
                AllWordSolverTask().execute(solverArgs)
            }



            buttonExact.setOnClickListener {
                anagrams.text = ""
                val solverArgs = SolverArgs(mainDictionary, mainActivity, textQuery.text.toString(), null)
                ExactWordSolverTask().execute(solverArgs)
            }


            buttonMultiword.setOnClickListener {
                anagrams.text = ""
                var minWordLength = 0

                if(textMultiword.text.isNotEmpty()) {
                    val multiword = textMultiword.text.toString().split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val lengths = IntArray(multiword.size)


                    var iter = 0
                    for (s in multiword) {
                        val i = Integer.parseInt(s)
                        lengths[iter] = i
                        iter++
                    }

                    Arrays.sort(lengths)
                    minWordLength = lengths[0]
                    val anagram: Set<Set<String>>
                    val solverArgs = SolverArgs(mainDictionary, mainActivity, textQuery.text.toString(), lengths)
                    MultiWordSolverTask().execute(solverArgs)
                } else {
                    anagrams.text = "Invalid input for Multiword."
                }
            }

            /*
            buttonMultiword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    anagrams.setText("");
                    int minWordLength = 0;
                    if(textMultiword.getText().length() == 0) {
                        anagrams.setText("Invalid input for Multiword.");
                        return;
                    }
                    String[] multiword = textMultiword.getText().toString().split(",");
                    int[] lengths = new int[multiword.length];


                    int iter = 0;
                    for(String s : multiword){
                        int i = Integer.parseInt(s);
                        lengths[iter] = i;
                        iter++;
                    }

                    Arrays.sort(lengths);
                    minWordLength = lengths[0];
                    Set<Set<String>> anagram;

                    if (minWordLength == 0) {
                        anagram = anagramSolver.findAllAnagrams(textQuery.getText().toString());
                    }
                    else {
                        //anagram = anagramSolver.findAllAnagrams(textQuery.getText().toString(), minWordLength);
                        anagram = anagramSolver.findSpecificAnagrams(textQuery.getText().toString(), lengths);
                    }

                    if(!anagram.isEmpty()) {
                        Boolean match = false;
                        for (Set<String> combo : anagram) {
                            int size = combo.size();

                            if(size == lengths.length) {
                                int[] clengths = new int[size];
                                String tempstring = "";
                                int iter2 = 0;
                                for (String s : combo) {
                                    tempstring += s + " ";
                                    clengths[iter2] = s.length();
                                    iter2++;
                                }
                                Arrays.sort(clengths);

                                if(Arrays.equals(lengths, clengths)) {
                                    match = true;
                                    anagrams.append(tempstring + "\n");
                                }
                            }
                        }
                        if(!match) {
                            anagrams.append("No matches found.\n");
                        }
                    } else {
                        anagrams.append("No matches found.\n");
                    }

                }
            });
            */


        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun PublishProgress(s: String) {
        output.text = s
    }

    fun PublishAppend(s: String) {
        output.append(s)
    }

}

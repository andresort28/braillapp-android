package com.project.rubikon.braillapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.project.rubikon.braillapp.Model.Tweet;

import java.util.ArrayList;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;

public class LoadTweetsActivity extends AppCompatActivity {
    private boolean flag= false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG-RUBIKON", "LoadTweetsActivity created");
        setContentView(R.layout.layout_load_tweets);
        loadingTweets();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_load_tweets, menu);
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

    private static Tweet tweety;
    private static ArrayList[] listaTweets= new ArrayList[20];


    public void loadingTweets(){
        //Aqui va el evento de cuando se cargan los tweets, una vez ya cargados el pasa a la activity
        // de lectura
        // Si en el evento de cargar tweets, si existen tweets para cargar el retorno es true
        // y pasa a la activity de lectura, si no existen tweets para cargar el retorno es false
        // se pasa a la activity de no hay tweets.


        if(true){

            Intent newfront = new Intent(LoadTweetsActivity.this, BrailleScreenActivity.class);
            startActivity(newfront);
        }else{
            Intent newfront = new Intent(LoadTweetsActivity.this, NoTweetsActivity.class);
            startActivity(newfront);
        }

    }
}
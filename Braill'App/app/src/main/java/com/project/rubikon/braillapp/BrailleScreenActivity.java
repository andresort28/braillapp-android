package com.project.rubikon.braillapp;

import android.content.Context;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import com.project.rubikon.braillapp.Model.Tweet;

import java.util.ArrayList;

public class BrailleScreenActivity extends AppCompatActivity  implements GestureDetector.OnGestureListener{

    private RelativeLayout touchview;
    private int count;
    float x1=0, y1=0;
    private boolean lock;

    private final static int[] STATE_PRESSED = {
            android.R.attr.state_pressed,
            android.R.attr.state_focused
                    | android.R.attr.state_enabled };

    private static int defaultStates[];
    private GestureDetector gd;
    private static Tweet tweety;
    private static String[] listTweets;
    private static ArrayList[] listaTweets= new ArrayList[20];
    private String segment="1000101010001010100101101011010100001010100000110001010101";
    int tweetCounter = 0; //Columnas del array de arraylist que es un tweet con su lista de paginas
    int segmentCounter = 0;  // Filas que son las paginas de un tweet

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG-RUBIKON", "BrailleScreenActivity created");

        setContentView(R.layout.layout_braille_screen);

        touchview = (RelativeLayout) findViewById(R.id.braille);
        defaultStates = findViewById(R.id.dot1).getBackground().getState();
        Thread closeActivity = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Thread.sleep(10000);
                    // Do some stuff
                } catch (Exception e) {
                    e.getLocalizedMessage();
                }
            }
        });
        TweetRepository.getInstance().getTimelineAsync(timelineListener); // => timelineListener
        //updateBrailleScreen((String) listaTweets[tweetCounter].get(segmentCounter));
        gd = new GestureDetector(this);

        //set the on Double tap listener
        gd.setOnDoubleTapListener(new OnDoubleTapListener(){
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                lock=!lock;
                Log.d("doubleTap", lock+"");
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }
        });


        final View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton button = (ImageButton)v;
                Vibrator vib= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(100);
            }
        };



        View.OnTouchListener t = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {

                gd.onTouchEvent(event);

                int x = (int) event.getX();
                int y = (int) event.getY();
                count++;


                if(!lock){
                    switch (event.getActionMasked()){

                        case MotionEvent.ACTION_DOWN:{
                            x1 = event.getX();
                            y1 = event.getY();
                            break;
                        }

                        case MotionEvent.ACTION_UP: {
                            float x2 = event.getX();
                            float y2 = event.getY();
                            float diffX = x2 - x1;
                            float diffY = y2 - y1;

                            if(Math.abs(diffX) > Math.abs(diffY))
                            {
                                if(diffX > 0) {
                                    prevTweet();
                                    Log.d("TAG-RUBIKON", "Left");
                                }else {
                                    nextTweet();
                                    Log.d("TAG-RUBIKON", "Right");
                                }
                            }
                            else
                            {
                                if(diffY > 0){
                                    prevPage();
                                    Log.d("TAG-RUBIKON", "Up");
                                } else{
                                    nextPage();
                                    Log.d("TAG-RUBIKON", "Down");
                                }

                            }
                            break;
                        }
                    }
                }
                else
                {
                    for (int i = 0; i < touchview.getChildCount(); i++) {
                        View current = touchview.getChildAt(i);
                        if (current instanceof ImageButton) {
                            ImageButton b = (ImageButton) current;

                            if (!isPointWithin(x, y, b.getLeft(), b.getRight(), b.getTop(),
                                    b.getBottom())) {
                                b.getBackground().setState(defaultStates);
                            }

                            if (isPointWithin(x, y, b.getLeft(), b.getRight(), b.getTop(),
                                    b.getBottom())) {
                                b.getBackground().setState(STATE_PRESSED);
                                if ( (int)b.getTag() == R.drawable.filled13) {
                                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                    v.vibrate(100);
                                }
                            }

                        }
                    }
                }
                return true;
            }
        };


        touchview.setOnTouchListener(t);

    }



    public void updateBrailleScreen ( String segment ) {

        int childCount = touchview.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View current = touchview.getChildAt(i);
            if (current instanceof ImageButton) {
                ImageButton b = (ImageButton) current;
                String resourceName = getResources().getResourceName(b.getId());
                String idName = resourceName.split("/")[1];
                int num = Integer.parseInt(idName.substring(3, idName.length()));

                if ( segment.charAt(num-1) == '1'){
                    b.setBackgroundResource(R.drawable.filled13);
                    b.setTag(R.drawable.filled13);
                }
                else{
                    b.setBackgroundResource(R.drawable.blank36);
                    b.setTag(R.drawable.blank36);
                }
            }
        }
    }

    static boolean isPointWithin(int x, int y, int x1, int x2, int y1, int y2) {
        return (x <= x2 && x >= x1 && y <= y2 && y >= y1);
    }

    public void prevPage(){
        if(segmentCounter>=0){
            ArrayList currentTweet = listaTweets[tweetCounter];
            updateBrailleScreen((String) currentTweet.get(segmentCounter));
        }else {
            segmentCounter--;
            ArrayList currentTweet = listaTweets[tweetCounter];
            updateBrailleScreen((String) currentTweet.get(segmentCounter));
        }
    }

    public void nextPage(){
        if(segmentCounter==listaTweets[tweetCounter].size()-1){
            ArrayList currentTweet = listaTweets[tweetCounter];
            updateBrailleScreen((String) currentTweet.get(segmentCounter));
        }else {
            segmentCounter++;
            ArrayList currentTweet = listaTweets[tweetCounter];
            updateBrailleScreen((String) currentTweet.get(segmentCounter));
        }
    }
    public void prevTweet() {
        if(tweetCounter==0){
            segmentCounter = 0;
            ArrayList currentTweet = listaTweets[tweetCounter];
            updateBrailleScreen((String) currentTweet.get(segmentCounter));
        }else {
            tweetCounter--;
            segmentCounter = 0;
            ArrayList currentTweet = listaTweets[tweetCounter];
            updateBrailleScreen((String) currentTweet.get(segmentCounter));
        }
    }

    public void nextTweet() {
        if(tweetCounter<=20){
            ArrayList currentTweet = listaTweets[tweetCounter];
            updateBrailleScreen((String) currentTweet.get(segmentCounter));
        }else {
            tweetCounter++;
            segmentCounter = 0;
            ArrayList currentTweet = listaTweets[tweetCounter];
            updateBrailleScreen((String) currentTweet.get(segmentCounter));
        }
    }


    public void pasarTweet(){

        // va a haber una variable bandera que va a indicar si esta la pantalla bloqueada
        // para realizar movimientos de desplazamiento o esta desbloqueada
        // True representa que si se pueden realizar movimientos

        if(true){
            // aqui se especifica hacia donde se dirige el movimiento
            // dependiendo de la informacion brindada por la accion realizada por el usuario
            // puede cambiar la informacion si va hacia arriba sigue leyendo el tweet
            // si va hacia la izquierda lee el siguiente tweet, si va a la derecha
            // pasa al anterior tweet y si va hacia abajo vuelve a la parte superior del tweet que
            // esta leyendo
            // Si al hacer un movimiento buscando un tweet nuevo no se encuentran mas tweets por leer
            // Se cambia la bandera de Si hay mas tweets por leer a TRUE y entra al if que
            // Cambia a la activity de cargando tweets.....


            // Este seria el if de la bandera que indica que no hay mas tweets por leer
            //
            if(true){


                Intent newfront = new Intent(BrailleScreenActivity.this, LoadTweetsActivity.class);
                startActivity(newfront);
            }


            Intent newfront = new Intent(BrailleScreenActivity.this, BrailleScreenActivity.class);
            startActivity(newfront);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_braille_screen, menu);
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





    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
    private void showTimeline(ResponseList<Status> statuses)
    {
        // Creamos un array de Strings con el texto de los Status( Tweets )
        String[] tweets = new String[statuses.size()];
        int counter = 0;
        for (Status status : statuses) {
            tweets[counter] = status.getText();
            tweety = new Tweet(tweets[counter].split("http")[0].toString());
            listaTweets[counter]= tweety.getTraslation();
            Log.d("TAG-RUBIKON", "Tweets: " + tweets[counter].split("http")[0].toString());
            counter++;
        }


    }
    // Esta clase interna es la encargada de manejar el callback,  tiene dos m�todos para manejar la posibilidad de �xito y de error.
    TwitterListener timelineListener = new TwitterAdapter() {

        @Override
        public void gotHomeTimeline(ResponseList<Status> statuses) {
            showTimeline(statuses);
        }

        @Override
        public void onException(TwitterException te, TwitterMethod method) {
            //showError();
        }
        @Override
        public void gotUserTimeline(ResponseList<Status> statuses) {
            showTimeline(statuses);
        }
    };
}
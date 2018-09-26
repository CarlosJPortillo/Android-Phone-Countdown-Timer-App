package com.example.carlos.countdowntimer;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carlos.countdowntimerexample.R;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //starting time
    private static final long START_TIME_IN_MILLIS = 600000;
    //view controls
    private TextView mTextViewCountDown;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private EditText mEditText;
    //object to keep track of countdown
    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning;
    //time left in the timer
    private long mTimeLeftInMillis;
    //variable that saves time when our timer is supposed to end, important to face lag
    //lag can happen you rotate screen too much
    private long mEndTime;

    int minutes =0;
    //checks to see if timer isn't paused
    private boolean mTimerPaused = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewCountDown = findViewById(R.id.text_view_countdown);

        mEditText = (EditText) findViewById(R.id.edit_text1);


        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);
        //event listener/handler
        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            //event handler
            public void onClick(View v) {
                //check to see if input in editText can be parsed
               try {
                   minutes = Integer.parseInt(mEditText.getText().toString());
                   if (mTimerRunning) {
                       pauseTimer();
                   } else {
                       if(minutes >0 && minutes <= 60) {
                           //checks to see if timer is paused that way you don't reset timer when pushing start
                           if(mTimerPaused == false) {
                               mTimeLeftInMillis = 60000 * minutes;
                           }
                           startTimer();
                       }
                       else{
                           String negativeResponse = "You can only enter minutes less than 60 and of at least one minute";
                           Toast.makeText(getApplicationContext(), negativeResponse, Toast.LENGTH_SHORT).show();
                       }
                   }

               }
                catch(NumberFormatException ex){
                    //android's version of alert
                    //pass this instance of android application, the response of the message,and how long you
                    //want the toast to show for
                    String negativeResponse = "You can only type in whole minute numbers!";
                    Toast.makeText(getApplicationContext(), negativeResponse, Toast.LENGTH_SHORT).show();
                }

            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        updateCountDownText();
    }

    private void startTimer() {
        //This method returns the difference, measured in milliseconds, between the current time and midnight
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        //assign countdown timer to a new instance of timer
        //countDownInterval is a the increment in the timer, in this instance it is a second
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            //what you want to happen every tick
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                //update the text every second/tick
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                mButtonStartPause.setText("Start");
                mButtonStartPause.setVisibility(View.INVISIBLE);
                mButtonReset.setVisibility(View.VISIBLE);
            }
        }.start();

        mTimerRunning = true;
        mButtonStartPause.setText("pause");
        mButtonReset.setVisibility(View.INVISIBLE);
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        mButtonStartPause.setText("Start");
        mButtonReset.setVisibility(View.VISIBLE);
        mTimerPaused = true;
    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        mTextViewCountDown.setText(timeLeftFormatted);
    }
    //whenever we want to reset our buttons
    private void updateButtons() {
        //we can oly reset when the timer is paused
        if (mTimerRunning) {
            //make reset button invisible if timer is running
            mButtonReset.setVisibility(View.INVISIBLE);
            mButtonStartPause.setText("Pause");
            //if you are not running timer, make sure pause is set to start
        } else {
            mButtonStartPause.setText("Start");
            //if you reach below 1 second on timer
            if (mTimeLeftInMillis < 1000) {
                //make the pause button visible again
                mButtonStartPause.setVisibility(View.INVISIBLE);
            } else {
                mButtonStartPause.setVisibility(View.VISIBLE);
            }
            //if at least 1 tick has passed
            if (mTimeLeftInMillis < START_TIME_IN_MILLIS) {
                mButtonReset.setVisibility(View.VISIBLE);
            } else {
                mButtonReset.setVisibility(View.INVISIBLE);
            }
        }
    }
    @Override
    //Save variables on instance state change, such as rotating screen
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save milliseconds left and boolean variable if we
        //were running
        //uses key and value
        outState.putLong("millisLeft", mTimeLeftInMillis);
        outState.putBoolean("timerRunning", mTimerRunning);
        outState.putLong("endTime", mEndTime);
    }

    @Override
    //called after oncreate method if there is a saved instance state
    //this restore's an instance state
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //get values
        mTimeLeftInMillis = savedInstanceState.getLong("millisLeft");
        mTimerRunning = savedInstanceState.getBoolean("timerRunning");
        //update the text to the saved state variables
        updateCountDownText();
        //and also update buttons
        updateButtons();

        if (mTimerRunning) {
            //get endTime
            mEndTime = savedInstanceState.getLong("endTime");
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            startTimer();
        }
    }
}
package com.dou.dboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.huawei.android.hardware.hwtouchweight.TouchForceManager;
import com.huawei.android.util.NoExtAPIException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };


    EditText editText;
    private TouchForceManager mTouchForceManager = null;
    private boolean isSupport = false;
    private String TAG = "TouchForceManager";
    private boolean isAvailble = false;
    private boolean flag = true;
    boolean darkBG=false;
    RelativeLayout BGFL,BGBG;

    //ImageButton imageButton_ok,imageButton_ok_2,imageButton_cancel;
    ImageButton imageButton_ok_2,imageButton_cancel_2;

    SeekBar seekBar;
    int TXT_SIZE = 27;
    //RelativeLayout controlRL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        //hide UI
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.FeditText);

        //imageButton_cancel = (ImageButton)findViewById(R.id.imageButton_cancel);
        //imageButton_ok = (ImageButton)findViewById(R.id.imageButton_ok);
        imageButton_ok_2 = (ImageButton)findViewById(R.id.imageButton_ok_2);
        imageButton_cancel_2 = (ImageButton)findViewById(R.id.imageButton_cancel_2);

        //controlRL = (RelativeLayout)findViewById(R.id.relativeLayout_control);
        editText = (EditText) findViewById(R.id.FeditText);
        BGFL = (RelativeLayout) findViewById(R.id.FBGFL);
        BGBG = (RelativeLayout) findViewById(R.id.bgbg);

        seekBar = (SeekBar)findViewById(R.id.seekBar);

        seekBar.setProgress(TXT_SIZE);
        editText.setTextSize(TXT_SIZE);

        sharedPreferences  = getSharedPreferences("DBoardTXT",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editText.setText(sharedPreferences.getString("dbtxt","Enter"));
        if (sharedPreferences.getBoolean("dbdarkbg",false)){
            switchBG();
        }

        //imageButton_ok.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        toggle();
        //    }
        //});

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                TXT_SIZE = i;
                editText.setTextSize(TXT_SIZE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        imageButton_ok_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commitTXT();
                toggle();
            }
        });

        BGBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().isEmpty()){
                    cancelTXT();
                }else {
                    commitTXT();
                }
                toggle();
            }
        });

        imageButton_cancel_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelTXT();
                toggle();
            }
        });

        //imageButton_cancel.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        toggle();
        //    }
        //});

        // Set up the user interaction to manually show or hide the system UI.
        //mContentView.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        toggle();
        //    }
        //});

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);



        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
                toggle();
            }
        });

        getFT();
        setFT();
    }

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private void cancelTXT() {
        editText.setText(sharedPreferences.getString("dbtxt","Enter"));
    }

    private void commitTXT() {
        editor.putString("dbtxt",editText.getText().toString());
        editor.commit();
    }

    private boolean getFT() {

        try {
            mTouchForceManager = new TouchForceManager(this);
            isSupport = mTouchForceManager.isSupportForce();
        } catch (NoExtAPIException e) {
            Log.e(TAG, "init: mTouchForceManager" + e.getMessage());
            return false;
        }
        return isSupport;
    }

    public void setFT() {
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if ( isSupport) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_MOVE:
							/*
							*当前环境支持ForceTouch时，MotionEvent.getPressure()获取当前压力值；
							*/
                            float pressure = motionEvent.getPressure();
                            isAvailble = mTouchForceManager.isForceAvailble(pressure);
							/*
							*当前环境支持ForceTouch，压力值在有效范围内，弹出吐司信息；；
							*/
                            if ( isAvailble && flag && (pressure > 0.3f)) {
                                switchBG();
                                Toast.makeText(FullscreenActivity.this, "BG Changed", Toast.LENGTH_SHORT).show();
                                flag = false;
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            flag = true;
                            break;
                        default:
                            break;
                    }
						/*
						*不支持压感，撤离压力物，弹出不支持压感信息；；
						*/
                } else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    Toast.makeText(FullscreenActivity.this, "不支持压力感应",
                            Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        BGBG.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if ( isSupport) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_MOVE:
							/*
							*当前环境支持ForceTouch时，MotionEvent.getPressure()获取当前压力值；
							*/
                            float pressure = motionEvent.getPressure();
                            isAvailble = mTouchForceManager.isForceAvailble(pressure);
							/*
							*当前环境支持ForceTouch，压力值在有效范围内，弹出吐司信息；；
							*/
                            if ( isAvailble && flag && (pressure > 0.25f)) {
                                switchBG();
                                Toast.makeText(FullscreenActivity.this, "BG Changed", Toast.LENGTH_SHORT).show();
                                flag = false;
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (!flag){
                                flag = true;
                                return true;
                            }
                            flag = true;
                            break;
                        default:
                            break;
                    }
						/*
						*不支持压感，撤离压力物，弹出不支持压感信息；；
						*/
                } else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    Toast.makeText(FullscreenActivity.this, "不支持压力感应",
                            Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    private void switchBG() {
        if (darkBG){
            BGFL.setBackgroundResource(R.color.BGLight);
            editText.setTextColor(getResources().getColor(R.color.BGDark));
            darkBG = false;
            editor.putBoolean("dbdarkbg",darkBG);
        }else {
            BGFL.setBackgroundResource(R.color.BGDark);
            editText.setTextColor(getResources().getColor(R.color.BGLight));
            darkBG = true;
            editor.putBoolean("dbdarkbg",darkBG);
        }
        editor.commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        //ActionBar actionBar = getSupportActionBar();
        //if (actionBar != null) {
        //    actionBar.hide();
        //}
        //controlRL.setVisibility(View.GONE);
        imageButton_ok_2.setVisibility(View.GONE);
        imageButton_cancel_2.setVisibility(View.GONE);

        hideKB();
        editText.clearFocus();

        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        //mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void hideKB() {
        InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @SuppressLint("InlinedApi")
    private void show() {

        //controlRL.setVisibility(View.VISIBLE);

        imageButton_ok_2.setVisibility(View.VISIBLE);
        imageButton_cancel_2.setVisibility(View.VISIBLE);


        // Show the system bar
        mContentView.setSystemUiVisibility(
                //View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                );

        mVisible = true;

        mControlsView.setVisibility(View.VISIBLE);
        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        //mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}

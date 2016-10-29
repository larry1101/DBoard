package com.dou.dboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.huawei.android.hardware.hwtouchweight.TouchForceManager;
import com.huawei.android.util.NoExtAPIException;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    boolean darkBG=false;
    RelativeLayout BGRL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        BGRL = (RelativeLayout)findViewById(R.id.BGRL);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().equals(getString(R.string.inii))) {
                    editText.setText("");
                }
            }
        });

        getFT();
        setFT();
    }


    private TouchForceManager mTouchForceManager = null;
    private boolean isSupport = false;
    private String TAG = "TouchForceManager";
    private boolean isAvailble = false;
    private boolean flag = true;

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
                            if ( isAvailble && flag) {
                                switchBG();
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
                    Toast.makeText(MainActivity.this, "不支持压力感应",
                            Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    private void switchBG() {
        if (darkBG){
            BGRL.setBackgroundResource(R.color.BGLight);
            editText.setTextColor(getResources().getColor(R.color.BGDark));
            darkBG = !darkBG;
        }else {
            BGRL.setBackgroundResource(R.color.BGDark);
            editText.setTextColor(getResources().getColor(R.color.BGLight));
            darkBG = !darkBG;
        }
    }
}

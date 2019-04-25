package com.example.downloaddemo;


import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class TimeActivity extends AppCompatActivity {

    public static final int INT = 1001;
    private TextView timetextview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        timetextview=findViewById(R.id.tv_time);

        TimeHandler handler=new TimeHandler(this);
        Message message=Message.obtain();
        message.what= INT;
        message.arg1=10;
        handler.sendMessageDelayed(message,1000);


    }

    public static class TimeHandler extends Handler{
        final WeakReference<TimeActivity> timeActivityWeakReference;


        //构造器
        public TimeHandler(TimeActivity timeActivity) {
            this.timeActivityWeakReference = new WeakReference<>(timeActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TimeActivity activity=timeActivityWeakReference.get();//获取活动

            switch (msg.what){
                case INT:
                    int value=msg.arg1;
                    activity.timetextview.setText(String.valueOf(--value));

                    //重新构造一个mssage：
                    Message message=Message.obtain();
                    message.what=INT;
                    message.arg1=value;

                    if(value>0)
                    {
                        sendMessageDelayed(message,1000);
                    }

                    break;
            }
        }
    }
}

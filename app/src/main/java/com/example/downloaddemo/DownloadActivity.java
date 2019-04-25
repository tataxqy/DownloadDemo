package com.example.downloaddemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;



public class DownloadActivity extends AppCompatActivity {

    static Handler handler;
    private ProgressBar progressBar;
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_download);
            progressBar=(ProgressBar) findViewById(R.id.progressBar);
            //创建一个新的集合list，依次判断这三个权限有没有被授权，如果没有被授权就添加到List集合中
            List<String> permissionList=new ArrayList<>();
            if(ContextCompat.checkSelfPermission(DownloadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if(ContextCompat.checkSelfPermission(DownloadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            {
                permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if(!permissionList.isEmpty())
            {
                //将list转换成数组，调用requestPermission一次性申请

                String[] permissions=permissionList.toArray(new String[permissionList.size()]);
                ActivityCompat.requestPermissions(DownloadActivity.this,permissions,1);
            }else{
                findViewById(R.id.downloadbutton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                download("http://issuecdn.baidupcs.com/issue/netdisk/yunguanjia/BaiduNetdisk_6.7.3.exe");
                            }
                        }).start();

                    }
                });



            }

            handler =new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what)
                    {
                        case 1001:
                            progressBar.setProgress((Integer)msg.obj);
                            break;
                        case 1002:
                            Toast.makeText(getApplicationContext(),"downloadFail!",Toast.LENGTH_LONG).show();
                            break;

                    }
                }

            };
        }
        private void download(String url)
        {
            try {
                URL downloadurl=new URL(url);
                URLConnection urlConnection=downloadurl.openConnection();
                InputStream inputStream=urlConnection.getInputStream();

                int contentlength=urlConnection.getContentLength();


                String downloadFileName= Environment.getExternalStorageDirectory()+ File.separator+"xqy"+File.separator;

                File file=new File(downloadFileName);
                if(!file.exists())
                {
                    file.mkdir();
                }
                String fileName=downloadFileName+"xqy.apk";

                File apkFile=new File(fileName);
                if(apkFile.exists())
                {
                    apkFile.delete();
                }


                int downloadSize=0;
                byte[] bytes=new byte[1024];

                int length=0;

                OutputStream outputStream =new FileOutputStream(fileName);
                while((length=inputStream.read(bytes))!=-1){
                    outputStream.write(bytes,0,length);
                    downloadSize+=length;
                    //更新UI
                    Message message=Message.obtain();
                    message.obj=downloadSize*100/contentlength;
                    message.what=1001;
                    handler.sendMessage(message);

                }
                inputStream.close();
                outputStream.close();

            } catch (MalformedURLException e) {
                Message message=Message.obtain();

                message.what=1002;
                handler.sendMessage(message);
                e.printStackTrace();
            } catch (IOException e) {
                Message message=Message.obtain();

                message.what=1002;
                handler.sendMessage(message);
                e.printStackTrace();
            }

        }

    }


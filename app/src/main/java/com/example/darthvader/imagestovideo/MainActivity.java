package com.example.darthvader.imagestovideo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorSpace;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.jcodec.api.android.AndroidSequenceEncoder;
import org.jcodec.common.Format;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Rational;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    List<Bitmap> list=new ArrayList<>();
    String filePath;
    ImageView view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list.clear();
        view=(ImageView)findViewById(R.id.imageView);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},2);
        }
        else
        {
            getFiles();
        }
        org.jcodec.common.io.SeekableByteChannel out=null;
        File file=new File(Environment.getExternalStorageDirectory()+"/tmp/");
        file.mkdir();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                out= NIOUtils.writableFileChannel(Environment.getExternalStorageDirectory()+"/tmp/output.mp4");
                AndroidSequenceEncoder sequenceEncoder=new AndroidSequenceEncoder(out,new Rational(1,1));
                for(Bitmap bitmap:list)
                {
                    sequenceEncoder.encodeImage(bitmap);
                }
                sequenceEncoder.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                NIOUtils.closeQuietly(out);
            }
        }
    }
     private void getFiles()
     {
         Bitmap bitmap=null;
         Bitmap bitmap2=null;
         list.clear();
         filePath=Environment.getExternalStorageDirectory()+"/DCIM/New Folder/";
         File fileDirectory = new File(filePath);
         Log.i("Check",fileDirectory.getAbsolutePath());
         File[] dirFiles = fileDirectory.listFiles();
         int x;
         if (dirFiles.length != 0) {
             for (int ii = 0; ii < dirFiles.length; ii++) {
                 String fileOutput = dirFiles[ii].toString();
                 bitmap=BitmapFactory.decodeFile(fileOutput);
                 ByteArrayOutputStream stream= new ByteArrayOutputStream();
                 bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                 bitmap2=BitmapFactory.decodeByteArray(stream.toByteArray(),0,stream.toByteArray().length);
                 list.add(Bitmap.createScaledBitmap(bitmap2,200,200,false));
             }
             view.setImageBitmap(bitmap2);
         }
     }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 2:if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                getFiles();
            }
            else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

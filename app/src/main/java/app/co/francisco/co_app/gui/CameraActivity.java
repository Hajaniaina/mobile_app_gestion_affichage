package app.co.francisco.co_app.gui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

import app.co.francisco.co_app.R;

public class CameraActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    ImageView imageView;
    Button btn_video_cam;
    Button photoButton;
    public String TAG="Camera";
    private static final int VIDEO_CAPTURE = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        init();


    }
    public  void init(){
        imageView = (ImageView) this.findViewById(R.id.image_icone_photo);
        photoButton = (Button) this.findViewById(R.id.btn_take_photo);
        btn_video_cam = (Button)this.findViewById(R.id.btn_video_cam) ;
        btn_video_cam.setVisibility(View.INVISIBLE);

        btn_video_cam.setEnabled(false);

        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        final Bitmap bm = BitmapFactory.decodeResource(getResources(), R.id.image_icone_photo);


        btn_video_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent captureVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(captureVideoIntent,VIDEO_CAPTURE );
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null){
            if (requestCode == CAMERA_REQUEST) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(photo);
                btn_video_cam.setEnabled(true);
                savePicture(photo,generate_name());
                Toast.makeText(getApplicationContext(), "Image saved...", Toast.LENGTH_SHORT).show();
            }if(requestCode==VIDEO_CAPTURE){
                Toast.makeText(getApplicationContext(), "Video...", Toast.LENGTH_SHORT).show();
            }
        }else{
            return;
        }

    }

    private String generate_name() {
        String name=null;
        Date d =new Date();
        name = d.getDay()+"-"+d.getMonth()+"-"+d.getYear()+" "+d.getHours()+"-"+d.getMinutes()+"-"+d.getSeconds()+"-msb.jpg";
        return name;
    }

    private void savePicture(Bitmap bm, String imgName)
    {
        OutputStream fOut = null;
        String strDirectory = Environment.getExternalStorageDirectory().toString()+"/MSB";
        if(!new File(strDirectory).mkdir()){
            Log.w(TAG,"Impossible de creer le repertoir de stockage");
        }

        File f = new File(strDirectory, imgName);

        try {
            fOut = new FileOutputStream(f);
            /**Compress image**/
            bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();

            /**Update image to gallery**/

            MediaStore.Images.Media.insertImage(getContentResolver(),
                    f.getAbsolutePath(), f.getName(), f.getName());

        } catch (Exception e) {
            Log.e(TAG,e.getMessage().toString());
        }
    }
}

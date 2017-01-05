package com.muchmore.www.chasquido;

import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.ImageView;
import android.content.pm.PackageInfo;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.view.View;

// new imports
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;


public class Home extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView imageView;

    ImageButton btnShowLocation;
    ImageButton upload;

    GPSTracker gps;
    String Global_User_Name;

    static double lat;
    static double lon;

    boolean location_taken = false;
    boolean upload_allow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        upload = (ImageButton)findViewById(R.id.upload);
        upload.setEnabled(upload_allow);

        //          Media Player
        Runnable backgroundSound = new Runnable() {
            @Override
            public void run() {
                MediaPlayer mediaPlayer1= MediaPlayer.create(Home.this, R.raw.welcome_message);
                mediaPlayer1.start();
                //MediaPlayer mediaPlayer2= MediaPlayer.create(Home.this, R.raw.welcome_tone);
                //mediaPlayer2.start();
            }
        };

        Thread media = new Thread(backgroundSound);
        media.start();
        //          Media Player

        //          Font
        final TextView userMessage = (TextView) findViewById(R.id.welcome_tag);

        Typeface customWelcomeMessage = (Typeface)Typeface.createFromAsset(getAssets(), "PoiretOne-Regular.ttf");
        userMessage.setTypeface(customWelcomeMessage);
        //          Font

        //          Intent Import and show top Text
        Bundle achieve = getIntent().getExtras();
        Boolean anonymousUser = false;

        if(achieve == null){
            anonymousUser = true;
            return;
        }
        String userName= achieve.getString("Username");
        if(anonymousUser == false) {
            userMessage.setText("Welcome " + userName + ", upload button will enable once you take a picture and click on location button.");
            Global_User_Name = userName;
        }
        if(anonymousUser == true){
            userMessage.setText("Welcome Annoymous User, You can't do anything here. Buzz off!!");
        }
        //          Intent Import and show top Text

        // GPS

        Runnable r = new Runnable() {
            @Override
            public void run() {
                btnShowLocation = (ImageButton)findViewById(R.id.show_location);

                btnShowLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final MediaPlayer media = MediaPlayer.create(Home.this, R.raw.login_button_click);
                    media.start();
                gps = new GPSTracker(Home.this);

                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    lat = latitude;
                    lon = longitude;

                    Toast.makeText(
                             getApplicationContext(),
                             "Your Location is -\nLat: " + latitude + "\nLong: "
                               + longitude, Toast.LENGTH_LONG).show();

                    upload = (ImageButton) findViewById(R.id.upload);
                    upload.setEnabled(!upload_allow);

                } else {
                    gps.showSettingsAlert();
                }
            }

            });
            } };



        Thread th = new Thread(r);
        th.start();
        // GPS

        //          CAMERA BUTTON IMPLEMENTATION
        ImageButton cameraButton = (ImageButton)findViewById(R.id.imageButton);
        imageView = (ImageView)findViewById(R.id.imageView);

                    /* Disable the button if the user doesn't have camera */

        if(!hasCamera())
            cameraButton.setEnabled(false);
        //          CAMERA BUTTON IMPLEMENTATION


    }


    // Upload View Load
    public void loadUpload(View view){
        final MediaPlayer media = MediaPlayer.create(Home.this, R.raw.login_button_click);
        media.start();
        Log.d("MyMessage", "On Upload Button Click");
        Intent intent = new Intent(Home.this, Upload.class);
        intent.putExtra("longitude", Double.toString(lon)) ;
        intent.putExtra("latitude", Double.toString(lat));
        intent.putExtra("UserNameDir", Global_User_Name);

        startActivity(intent);
        Log.d("MyMessage", "Starting Intent");
    }
    //



    // Check if the user has a camera

    private boolean hasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    // Launching the camera
    public void launchCamera(View view){
        final MediaPlayer media = MediaPlayer.create(Home.this, R.raw.login_button_click);
        media.start();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Take a picture and pass results along to onActivityResult
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }


    // If you want to return the image taken
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            //Get the photo
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");
            imageView.setImageBitmap(photo);
        }
    }


}








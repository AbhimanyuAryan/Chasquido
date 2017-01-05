package com.muchmore.www.chasquido;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.*;

public class Upload extends Activity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private static final String[]paths = {"Class Rooms", "Labs", "Grounds", "Buildings", "Libraries"};
    static String drop_down_choice = "";

    static String imageLoc = "";

    private static final int MY_INTENT_CLICK=302;
    private TextView txta;
    private TextView txtb;
    private TextView txtc;
    private ImageButton btn_selectImage;
    String myGlobalImagePath = "";
    Runnable r;
    static String fileName;
    static String UserName;

    static String longitude_db;
    static String latitude_db;

    private TextView stat;


    // Database connectivity

    Connection connect;
    PreparedStatement preparedStatement;

    @SuppressLint("NewApi")
    private Connection ConnectionHelper(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            Log.e("ERRO", "Class Loaded");
            ConnectionURL = "jdbc:jtds:sqlserver://14.139.235.213/mdu_college_portal;encrypt=fasle;user=cp_test;password=cp@123;instance=MSSQLSERVER;";
            connection = DriverManager.getConnection(ConnectionURL);
            Log.e("ERRO", "Connection Established");

        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return connection;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Bundle data = getIntent().getExtras();

        UserName = data.getString("UserNameDir");

        if(data == null){
            fileName = "file.png";
        }else{
            String fileName1 = data.getString("longitude");
            longitude_db = fileName1;
            String fileName2 = data.getString("latitude");
            latitude_db = fileName2;

            fileName = fileName1 + "%" + fileName2;
            imageLoc = "\\vikas\\" + fileName;
        }

        stat = (TextView) findViewById(R.id.status);
        txta = (TextView) findViewById(R.id.textView1);
        txtb = (TextView) findViewById(R.id.textView);
        txtc = (TextView) findViewById(R.id.dropText);

        //Change font

        Typeface customWelcomeMessage1 = (Typeface)Typeface.createFromAsset(getAssets(), "CoveredByYourGrace.ttf");
        txta.setTypeface(customWelcomeMessage1);

        Typeface customWelcomeMessage2 = (Typeface)Typeface.createFromAsset(getAssets(), "ShadowsIntoLight.ttf");
        txtb.setTypeface(customWelcomeMessage2);

        Typeface customWelcomeMessage3 = (Typeface)Typeface.createFromAsset(getAssets(), "ShadowsIntoLight.ttf");
        stat.setTypeface(customWelcomeMessage3);

        Typeface customWelcomeMessage4 = (Typeface)Typeface.createFromAsset(getAssets(), "ShadowsIntoLight.ttf");
        txtc.setTypeface(customWelcomeMessage4);


        //Change font


        //                      DROP DOWN

        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(Upload.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);




        //                     DROP DOWN


        btn_selectImage = (ImageButton) findViewById(R.id.btn_selectImage);

        btn_selectImage.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final MediaPlayer media = MediaPlayer.create(Upload.this, R.raw.login_button_click);
                media.start();
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"),MY_INTENT_CLICK);
            }
        });

        r = new Runnable() {
            @Override
            public void run() {
                Ftp obj = new Ftp();
                obj.ftpConnect("cp.mdurtk.in", "cp.mdurtk.in|cp", "cp@123", 21);
                //obj.ftpUpload(myGlobalImagePath,"file.jpg", "/vikas");

               // obj.ftpChangeDirectory("vikas/", UserName);
                obj.ftpMyUpload(myGlobalImagePath, fileName);
            }
        };
    }


    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                drop_down_choice = "Class Rooms";
                break;
            case 1:
                drop_down_choice = "Labs";
                break;
            case 2:
                drop_down_choice = "Grounds";
                break;
            case 3:
                drop_down_choice = "Buildings";
                break;
            case 4:
                drop_down_choice = "Libraries";
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        ImageButton btn = (ImageButton)findViewById(R.id.btn_selectImage);
        btn.setEnabled(false);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == MY_INTENT_CLICK)
            {
                if (null == data) return;

                String selectedImagePath;
                Uri selectedImageUri = data.getData();

                //MEDIA GALLERY
                selectedImagePath = ImageFilePath.getPath(getApplicationContext(), selectedImageUri);
                myGlobalImagePath = selectedImagePath;
                Log.i("Image File Path", ""+selectedImagePath);
                txta.setText("File Path : \n"+selectedImagePath);           }
        }

        stat = (TextView)findViewById(R.id.status);
        stat.setText("Uploading....Please Wait!!!");
        Thread t = new Thread(r);
        t.start();

        try{
            Log.d("Database filling things", UserName + "  " + myGlobalImagePath + "  " + longitude_db + "  " + latitude_db + "  " + drop_down_choice );

            connect = ConnectionHelper();
            Statement stmt = connect.createStatement();

            String res = "insert into ImageData values('" + UserName + "','" + imageLoc + "','" + longitude_db + "','" + latitude_db + "','" + drop_down_choice + "')";


            stmt.executeUpdate(res);
//                    string res = "insert into batch values('" + nm + "'," +" ag" + ")";

           // psmt.setString(1,UserName);
           // psmt.setString(2,"\\vikas\\" +myGlobalImagePath);
           // psmt.setString(3, longitude_db);
           // psmt.setString(4, latitude_db);
            // YET to set  ***********************************************************************************************************
           // psmt.setString(5, drop_down_choice);
            // YET to set

        }catch (SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        stat.setText("Uploaded, Continue Uploading More Photos. Have Fun ;)");

    }

}

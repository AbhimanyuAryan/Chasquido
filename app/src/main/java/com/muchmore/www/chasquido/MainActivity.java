package com.muchmore.www.chasquido;

import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.sql.*;

public class MainActivity extends AppCompatActivity {
    Button loginbtn;
    TextView errorlbl;
    EditText edname, edpassword;
    Connection connect;
    PreparedStatement preparedStatement;
    Statement st;
    String ipaddress, db, username, password;
    TextView t;
    TextView dev;
    TextView m;

    @SuppressLint("NewApi")
    private Connection ConnectionHelper(String user, String password,
                                        String database, String server) {
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

            //192.168.2.103:1433

            //connection = DriverManager.getConnection(ConnectionURL);
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
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dev = (TextView)findViewById(R.id.developers);
        m = (TextView)findViewById(R.id.moto);
        Typeface d = (Typeface)Typeface.createFromAsset(getAssets(), "CoveredByYourGrace.ttf");
        dev.setTypeface(d);

        Typeface d2 = (Typeface)Typeface.createFromAsset(getAssets(), "Bangers.ttf");
        m.setTypeface(d2);


        //ReplaceFont.replaceDefaultFont(this,"Default", "Pacifico.ttf");
        //////Font Styling///////////
        t = (TextView)findViewById(R.id.title);
        errorlbl = (TextView)findViewById(R.id.lblerror);
        Log.e("ERRO","found title");
        Typeface myCustomFont1 = (Typeface)Typeface.createFromAsset(getAssets(), "Bangers.ttf");
        Typeface myCustomFont2 = (Typeface)Typeface.createFromAsset(getAssets(), "CoveredByYourGrace.ttf");
        Log.e("ERRO", "Loaded myCustomFont");
        t.setTypeface(myCustomFont1);
        errorlbl.setTypeface(myCustomFont2);
        Log.e("ERRO","setted CustomFont");
        //////Font Styling///////////


        loginbtn = (Button) findViewById(R.id.btnlogin);

        errorlbl = (TextView) findViewById(R.id.lblerror);

        edname = (EditText) findViewById(R.id.txtname);
        edpassword = (EditText) findViewById(R.id.txtpassword);

        ipaddress = "192.168.2.103";
        db = "MyDatabase";
        username = "sa";
        password = "121abhi";
        connect = ConnectionHelper(username, password, db, ipaddress);
        loginbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Sound on Button Click

                final MediaPlayer media = MediaPlayer.create(MainActivity.this, R.raw.login_button_click);
                media.start();

                // Sound on Button Click
                try {
                    connect = ConnectionHelper(username, password, db, ipaddress);

                    st = connect.createStatement();
                    ResultSet rs = st.executeQuery("select * from College_info where college_code='" + edname.getText().toString() + "' and password='" + edpassword.getText().toString() + "'");
                    if (rs != null && rs.next()) {
                        //errorlbl.setText("Gottyaa!!!Logging In....WAIT. Life is beautiful yet slow. ISN'T IT?");
                        //Thread.sleep(5000);
                        errorlbl.setText("Sorry taking longer than usual. Wait while we verify your credentials with database");
                        Intent intent = new Intent(MainActivity.this, Home.class);
                        intent.putExtra("Username", edname.getText().toString() );
                        startActivity(intent);
                    } else {
                        errorlbl.setText("Sorry, wrong credentials!!! Re-Enter UserName & Password. Don't Mess around stupid -_-");
                    }

                } catch (SQLException e) {
                    errorlbl.setText(e.getMessage().toString());
                } /*catch (InterruptedException e){
                    Log.d("ERRO","InterruptedException");
                    e.printStackTrace();
                }*/

            }
        });

    }

}

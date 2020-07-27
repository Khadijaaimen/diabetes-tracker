package com.example.diabetestracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Tabs extends AppCompatActivity {

    AlertDialog.Builder builder;
    DatabaseHelper db;
    SQLiteDatabase database;
    Toolbar tb;
    FloatingActionButton fab1, fab2, fab3;
    Preferences utils;
    DatabaseHelper dbHelper;
    User user;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        tb = findViewById(R.id.title);
        setSupportActionBar(tb);

        Intent i=getIntent();
        utils=new Preferences();
        db=new DatabaseHelper(this);
        tb = findViewById(R.id.title);
        dbHelper=new DatabaseHelper(this);
        user=new User();
        getUserDetails();

        ViewPager viewPager = findViewById(R.id.view_pager);
        setupViewPage(viewPager);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        fab1 = findViewById(R.id.item1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Tabs.this, SugarEntry.class);
                startActivity(i);
            }
        });

        fab2 = findViewById(R.id.item2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Tabs.this, WeightEntry.class);
                startActivity(i);
            }
        });

        fab3 = findViewById(R.id.item3);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Tabs.this, MedEntry.class);
                startActivity(i);
            }
        });

    }
    //take details for profile page
    private void getUserDetails()
    {
        email=utils.getEmail(this);
        user=dbHelper.getUser(email);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reminder:
                Intent i = new Intent(this, Reminder.class);
                startActivity(i);
                break;

            case R.id.dlt:
                builder=new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to delete all records?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            database = db.getReadableDatabase();
                            database.execSQL("delete from medication");
                            database.execSQL("delete from sugar");
                            database.execSQL("delete from weight");
                            Toast.makeText(getApplicationContext(), "All Records Deleted ", Toast.LENGTH_SHORT).show();
                          }
                        catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Deletion unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
                return true;

            case R.id.logout:
                utils.saveEmail("",this);
                if(utils.getEmail(this)==null||utils.getEmail(this)=="")
                {
                    Intent i2=new Intent(this,Welcome.class);
                    startActivity(i2);
                }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPage(ViewPager viewPager){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new HomeFragment(), "HOME");
        viewPagerAdapter.addFragment(new ProfileFragment(user.getName(),user.getEmail(),user.getDOB(),user.getGender(),user.getPassword()), "MY PROFILE");
        viewPager.setAdapter(viewPagerAdapter);
    }

}
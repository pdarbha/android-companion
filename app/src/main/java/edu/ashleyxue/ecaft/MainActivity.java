package edu.ashleyxue.ecaft;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ashleyxue.ecaft.DatabaseSchema.CompanyTable;

public class MainActivity extends AppCompatActivity  implements SearchView
        .OnQueryTextListener, NavigationView.OnNavigationItemSelectedListener   {

    private static final String TAG = "ECaFT";
    private TabLayout mTabLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mMenuOptions;
    private DrawerLayout mDrawerLayout;
    private FrameLayout mFrameLayout;
    private ListView mDrawerList;
    private RelativeLayout mDrawerListLayout;
    private boolean searching;
    private DrawerLayout drawer;
    public static NavigationView navigationView;

    public static List<String> allCompanies = new ArrayList<String>();
    public static List<String> allCompanyIds = new ArrayList<String>();

    /**
     * Database variables
     */
    public static Context mContext;
    public static SQLiteDatabase mDatabase;
    public static int currentUserList = 0;
    public static List<String> userListNames = new ArrayList<String>(){{
        add("companies");
    }};
    /**
     * Fragments
     */
    private HomeFragment homeFragment;
    private MapFragment mapFragment;
    private InfoFragment infoFragment;
    private ChecklistFragment checklistFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Log.d("testingtesting", FirebaseDatabase.getInstance().getApp().getName());
        mContext = getApplicationContext();
        mDatabase = new DatabaseHelper(mContext).getWritableDatabase();

        homeFragment = new HomeFragment();
        mapFragment = new MapFragment();
        infoFragment = new InfoFragment();
        checklistFragment = new ChecklistFragment();

       // View drawer = findViewById(R.id.activity_main);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        getSupportFragmentManager().beginTransaction().replace(R.id
                .content_frame, new HomeFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            fragment = homeFragment;
        } else if (id == R.id.nav_map) {
            fragment = mapFragment;
        } else if (id == R.id.nav_companies){
            fragment = infoFragment;
        } else if (id == R.id.nav_checklist) {
            fragment = checklistFragment;
        } else {
            Log.d("ECaFT", "Cannot select existing menu option.");
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame,
                fragment).addToBackStack(null)
                .commit();

        navigationView.setCheckedItem(id);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
       // mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = null;
        switch (position) {
            case 0: //Home fragment
                fragment = homeFragment;
                break;
            case 1:
                fragment = mapFragment;
                break;
            case 2:
                fragment = infoFragment;
                searching= true;
                break;
            case 3:
                fragment = checklistFragment;
                break;
            default:
                break;
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.content_frame, fragment)
        .commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerListLayout);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
     //   mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, SearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }


    /**
     * Database Methods
     */
    public static void deleteRow(String id) {
        mDatabase.delete(CompanyTable.NAME, CompanyTable.Cols.ID + " = ?", new String[]{id});
    }


    public static boolean isInDatabase(String name) {
        Cursor c = mDatabase.query(CompanyTable.NAME, null, null, null, null, null, null);
        boolean inside = false;
        try {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                if (name.equals(c.getString(c.getColumnIndex(CompanyTable.Cols.COMPANY_NAME)))) {
                    inside = true;
                    break;
                }
                c.moveToNext();
            }

        } finally {
            c.close();
        }
        return inside;
    }

    public static void addRow(String currentCompanyUUID, String
            currentCompanyName) {
        ContentValues values = new ContentValues();
        values.put(CompanyTable.Cols.ID, currentCompanyUUID);
        values.put(CompanyTable.Cols.COMPANY_NAME, currentCompanyName);
        values.put(CompanyTable.Cols.VISITED, 0);
        values.put(CompanyTable.Cols.NOTE, "");
        mDatabase.insert(CompanyTable.NAME, null, values);
    }


    public static List<Integer> makeIsVisited() {
        List<Integer> compiledList = new ArrayList<>();
        Cursor c = mDatabase.query(CompanyTable.NAME, null, null, null, null,
                null, CompanyTable.Cols.COMPANY_NAME + " ASC");
        try {
            c.moveToFirst();

            while (!c.isAfterLast()) {

                compiledList.add(c.getInt(c.getColumnIndex(CompanyTable.Cols
                        .VISITED)));
                c.moveToNext();
            }
        } finally {
            c.close();
        }

        return compiledList;
    }


    public static List<String> makeSavedList() {
        List<String> compiledList = new ArrayList<>();
        Cursor c = mDatabase.query(CompanyTable.NAME,
                null, null, null, null, null, CompanyTable.Cols.ID + " ASC");
        try {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                String com = c.getString(c.getColumnIndex(CompanyTable.Cols
                        .ID));
                compiledList.add(com);
                c.moveToNext();
            }
        } finally {
            c.close();
        }
        return compiledList;
    }


    public static void setVisitStatus(FirebaseCompany po, int visited){
        String str = "update " + CompanyTable.NAME + " set " + CompanyTable
                .Cols.VISITED + " = " + visited + " where " + CompanyTable
                .Cols.ID + " = \"" + po.getId() + "\"";
        mDatabase.execSQL(str);
    }

    public static void saveNote(String id, String note) {
        String str = "update " + CompanyTable.NAME + " set " + CompanyTable
                .Cols.NOTE + " = \"" + note + "\" where " + CompanyTable
                .Cols.ID + " = \"" + id + "\"";
        mDatabase.execSQL(str);
    }

    public static String getNote(String id) {
        Cursor c = mDatabase.query(CompanyTable.NAME,
                null, null, null, null, null, CompanyTable.Cols.ID + " ASC");

        try {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                String com = c.getString(c.getColumnIndex(CompanyTable.Cols
                        .ID));
                if (com.equals(id))
                    return c.getString(c.getColumnIndex(CompanyTable.Cols
                            .NOTE));
                c.moveToNext();
            }
        } finally {
            c.close();
        }
        return "";
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // User pressed the search button
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // User changed the text
        return false;
    }


    public static void addUserListRow(String currentCompanyUUID, String
            currentCompanyName) {
        ContentValues values = new ContentValues();
        values.put(CompanyTable.Cols.ID, currentCompanyUUID);
        values.put(CompanyTable.Cols.COMPANY_NAME, currentCompanyName);
        values.put(CompanyTable.Cols.VISITED, 0);
        values.put(CompanyTable.Cols.NOTE, "");
        mDatabase.insert(MainActivity.userListNames.get(
                MainActivity.currentUserList), null, values);
    }


    public static List<Integer> makeUserListIsVisited() {
        List<Integer> compiledList = new ArrayList<>();
        Cursor c = mDatabase.query(MainActivity.userListNames.get(
                MainActivity.currentUserList), null, null, null, null,
                null, CompanyTable.Cols.COMPANY_NAME + " ASC");
        try {
            c.moveToFirst();

            while (!c.isAfterLast()) {

                compiledList.add(c.getInt(c.getColumnIndex(CompanyTable.Cols
                        .VISITED)));
                c.moveToNext();
            }
        } finally {
            c.close();
        }

        return compiledList;
    }


    public static List<String> makeUserListSavedList() {
        List<String> compiledList = new ArrayList<>();
        Cursor c = mDatabase.query(MainActivity.userListNames.get(
                MainActivity.currentUserList), null, null, null,
                null, null, CompanyTable.Cols.ID + " ASC");
        try {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                String com = c.getString(c.getColumnIndex(CompanyTable.Cols
                        .ID));
                compiledList.add(com);
                c.moveToNext();
            }
        } finally {
            c.close();
        }
        return compiledList;
    }


    public static void setUserListVisitStatus(FirebaseCompany po, int visited){
        String str = "update " + MainActivity.userListNames.get(
                MainActivity.currentUserList) + " set " + CompanyTable
                .Cols.VISITED + " = " + visited + " where " + CompanyTable
                .Cols.ID + " = \"" + po.getId() + "\"";
        mDatabase.execSQL(str);
    }

    public static List<String> getTables(){
        List<String> tables = new ArrayList<String>();
         Cursor c = mDatabase.rawQuery("SELECT name FROM sqlite_master WHERE type='table' " +
                 "AND name!='android_metadata' ",null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                tables.add(c.getString(0));
                c.moveToNext();
            }
        }
        return tables;
    }

}

package com.televiranga.spagreen;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.televiranga.spagreen.adapters.NavigationAdapter;
import com.televiranga.spagreen.fragments.LiveTvFragment;
import com.televiranga.spagreen.fragments.MoviesFragment;
import com.televiranga.spagreen.fragments.TvSeriesFragment;
import com.televiranga.spagreen.models.NavigationModel;
import com.televiranga.spagreen.nav_fragments.CountryFragment;
import com.televiranga.spagreen.nav_fragments.FavoriteFragment;
import com.televiranga.spagreen.nav_fragments.GenreFragment;
import com.televiranga.spagreen.nav_fragments.MainHomeFragment;
import com.televiranga.spagreen.utils.Constants;
import com.televiranga.spagreen.utils.SpacingItemDecoration;
import com.televiranga.spagreen.utils.Tools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Serializable {


    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private LinearLayout navHeaderLayout;

    private RecyclerView recyclerView;
    private NavigationAdapter mAdapter;
    private List<NavigationModel> list =new ArrayList<>();
    private NavigationView navigationView;
    private String[] navItemImage;

    private String[] navItemName2;
    private String[] navItemImage2;
    private boolean status=false;

    private FirebaseAnalytics mFirebaseAnalytics;
    boolean isDark;
    private String navMenuStyle;

    private Switch themeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        isDark = sharedPreferences.getBoolean("dark", false);
        
        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navMenuStyle = Constants.NAV_MENU_STYLE;

        //---analytics-----------
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "main_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        //----dark mode----------

        if (sharedPreferences.getBoolean("firstTime", true) == true) {
            showTermServicesDialog();
        }




        //----init---------------------------
        navigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navHeaderLayout = findViewById(R.id.nav_head_layout);
        themeSwitch = findViewById(R.id.theme_switch);

        if (isDark) {
            themeSwitch.setChecked(true);
        }else {
            themeSwitch.setChecked(false);
        }


        //----navDrawer------------------------
        toolbar = findViewById(R.id.toolbar);
        if (!isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            navHeaderLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            navHeaderLayout.setBackgroundColor(getResources().getColor(R.color.nav_head_bg));
        }
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        navigationView.setNavigationItemSelectedListener(this);


        //----fetch array------------
        String[] navItemName = getResources().getStringArray(R.array.nav_item_name);
        navItemImage=getResources().getStringArray(R.array.nav_item_image);
        navItemImage2=getResources().getStringArray(R.array.nav_item_image_2);


        navItemName2=getResources().getStringArray(R.array.nav_item_name_2);




        //----navigation view items---------------------
        recyclerView = findViewById(R.id.recyclerView);
        if (navMenuStyle.equals("grid")) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 15), true));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        }
        recyclerView.setHasFixedSize(true);


        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        status = prefs.getBoolean("status",false);

        if (status){
            for (int i = 0; i< navItemName.length; i++){
                NavigationModel models =new NavigationModel(navItemImage[i], navItemName[i]);
                list.add(models);
            }
        }else {
            for (int i=0;i<navItemName2.length;i++){
                NavigationModel models =new NavigationModel(navItemImage2[i],navItemName2[i]);
                list.add(models);
            }
        }


        //set data and list adapter
        mAdapter = new NavigationAdapter(this, list, navMenuStyle);
        recyclerView.setAdapter(mAdapter);


        final NavigationAdapter.OriginalViewHolder[] viewHolder = {null};

        mAdapter.setOnItemClickListener(new NavigationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, NavigationModel obj, int position, NavigationAdapter.OriginalViewHolder holder) {

                Log.e("POSITION OF NAV:::", String.valueOf(position));

                //----action for click items nav---------------------

                if (position==0){
                    loadFragment(new MainHomeFragment());
                }
                else if (position==1){
                    loadFragment(new MoviesFragment());
                }
                else if (position==2){
                    loadFragment(new LiveTvFragment());
                }
                else if (position==3){
                    loadFragment(new TvSeriesFragment());
                }
                else if (position==4){
                    loadFragment(new GenreFragment());
                }
                else if (position==5){
                    loadFragment(new CountryFragment());
                }
                else {
                    if (status){

                        if (position==6){
                            Intent intent=new Intent(MainActivity.this,ProfileActivity.class);
                            startActivity(intent);
                        }
                        else if (position==7){
                            loadFragment(new FavoriteFragment());
                        }
                        else if (position==8){
                            new AlertDialog.Builder(MainActivity.this).setMessage("¿ Estás seguro de cerrar sesión ?")
                                    .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                                            editor.putBoolean("status",false);
                                            editor.apply();

                                            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).create().show();
                        }
                        else if (position==9){
                            Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
                            startActivity(intent);
                            MainActivity.this.finish();
                        }
                    }else {
                        if (position==6){
                            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                            startActivity(intent);
                        }
                        else if (position==7){
                            Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
                            startActivity(intent);
                            MainActivity.this.finish();
                        }
                    }

                }




                //----behaviour of bg nav items-----------------
                if (!obj.getTitle().equals("Settings") && !obj.getTitle().equals("Login") && !obj.getTitle().equals("Sign Out")){

                    if (isDark){
                        mAdapter.chanColor(viewHolder[0],position,R.color.nav_bg);
                    }else {
                        mAdapter.chanColor(viewHolder[0],position,R.color.white);
                    }


                    if (navMenuStyle.equals("grid")) {
                        holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        holder.name.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        holder.selectedLayout.setBackground(getResources().getDrawable(R.drawable.round_grey_transparent));
                        holder.name.setTextColor(getResources().getColor(R.color.colorPrimary));
                    }

                    viewHolder[0] =holder;
                }

                mDrawerLayout.closeDrawers();
            }
        });

        //----external method call--------------
        loadFragment(new MainHomeFragment());

        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SharedPreferences.Editor editor = getSharedPreferences("push", MODE_PRIVATE).edit();
                    editor.putBoolean("dark",true);
                    editor.apply();

                }else {
                    SharedPreferences.Editor editor = getSharedPreferences("push", MODE_PRIVATE).edit();
                    editor.putBoolean("dark",false);
                    editor.apply();
                }

                mDrawerLayout.closeDrawers();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        return true;
    }


    private boolean loadFragment(Fragment fragment){

        if (fragment!=null){

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .commit();

            return true;
        }
        return false;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_search:

                final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {

                        Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                        intent.putExtra("q",s);
                        startActivity(intent);

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        return false;
                    }
                });

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawers();
        }else {

            new AlertDialog.Builder(MainActivity.this).setMessage("Quieres salir ?")
                    .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                            System.exit(0);
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create().show();

        }
    }

    //----nav menu item click---------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // set item as selected to persist highlight
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();
        return true;
    }

    private void showTermServicesDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_term_of_services);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        Button declineBt = dialog.findViewById(R.id.bt_decline);
        Button acceptBt = dialog.findViewById(R.id.bt_accept);

        if (isDark) {
            declineBt.setBackground(getResources().getDrawable(R.drawable.btn_rounded_grey_outline));
            acceptBt.setBackground(getResources().getDrawable(R.drawable.btn_rounded_dark));
        }

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        acceptBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("push", MODE_PRIVATE).edit();
                editor.putBoolean("firstTime",false);
                editor.apply();
                dialog.dismiss();
            }
        });

        declineBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}

package com.hankarun.gevrek;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hankarun.gevrek.models.NewsGroup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends ThemedBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<LinkedHashMap<String,Vector<NewsGroup>>> {


    FoodAdapter mAdapter;

    @BindView(R.id.newsGroupRecycle)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.newsgroupSwipe)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        1);
            } else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else
        {
            getSupportLoaderManager().initLoader(1,null,this);
        }

        mAdapter = new FoodAdapter(null, R.layout.newsgroupitem, this);


        mRecyclerView = (RecyclerView) findViewById(R.id.newsGroupRecycle);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.newsgroupSwipe);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Bundle bundle = new Bundle();
                bundle.putBoolean("clear",true);
                getSupportLoaderManager().restartLoader(1,null,MainActivity.this);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults){
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getSupportLoaderManager().initLoader(1,null,this);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, PreferencesActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        NewsGroupLoader loader = new NewsGroupLoader(getApplicationContext());
        if(args != null && args.getBoolean("clean"))
            loader.clearCache();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<LinkedHashMap<String, Vector<NewsGroup>>> loader, LinkedHashMap<String, Vector<NewsGroup>> data)
    {
        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
        class head
        {
            int type;
            String name;
            String url;
        }
        private List<head> meals;
        private int rowLayout;
        private Context mContext;
        View v;

        public FoodAdapter(LinkedHashMap<String, Vector<NewsGroup>> data, int rowLayout, Context context) {
            if(data == null)
                this.meals = new ArrayList<>();
            else
                this.meals = getList(data);
            this.rowLayout = rowLayout;
            this.mContext = context;
        }

        public void setData(LinkedHashMap<String, Vector<NewsGroup>> data)
        {
            this.meals = getList(data);
            notifyDataSetChanged();
        }

        List<head> getList(LinkedHashMap<String, Vector<NewsGroup>> data)
        {
            List<head> temp = new ArrayList<>();
            for (Map.Entry<String, Vector<NewsGroup>> entry : data.entrySet()) {
                head thead = new head();
                thead.name = entry.getKey();
                thead.type = 0;
                temp.add(thead);
                for (NewsGroup group : entry.getValue()) {
                    head shead = new head();
                    shead.name = group.mName;
                    shead.name += " " + group.mCount;
                    shead.url = group.mUrl;
                    shead.type = 1;
                    temp.add(shead);
                }
            }

            return temp;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            final head meal = meals.get(i);
            if(meal.type == 0) {
                viewHolder.countryName.setBackgroundResource(R.color.);
                viewHolder.countryName.setBackgroundColor(Color.BLUE);
                viewHolder.countryName.setTextColor(Color.WHITE);
                viewHolder.countryName.setGravity(Gravity.CENTER_HORIZONTAL);
            }
            viewHolder.countryName.setText(Html.fromHtml(meal.name));

            viewHolder.countryName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(meal.type == 1) {
                        Intent intent = new Intent(mContext, Main2Activity.class);
                        intent.putExtra("link",meal.url);
                        intent.putExtra("group", meal.name);
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return meals.size();
        }

        @Override
        public int getItemViewType(int position) {
            return meals.get(position).type;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public TextView countryName;

            public ViewHolder(View itemView) {
                super(itemView);
                countryName = (TextView) itemView.findViewById(R.id.recyclerItemText);
            }
        }
    }

}

package app.news.allinone.craftystudio.allinonenewsapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import java.sql.Time;
import java.util.ArrayList;

import utils.ClickListener;
import utils.DatabaseHandlerFirebase;
import utils.NewsInfo;
import utils.NewsListRecyclerAdapter;
import utils.NewsMetaInfo;
import utils.RecyclerTouchListener;

public class NewsListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<NewsMetaInfo> newsMetaInfoArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    NewsListRecyclerAdapter newsListRecyclerAdapter;
    boolean isLoadingMoreArticle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Build GoogleApiClient with AppInvite API for receiving deep links
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(AppInvite.API)
                .build();

        // Check if this app was launched from a deep link. Setting autoLaunchDeepLink to true
        // would automatically launch the deep link if one is found.
        boolean autoLaunchDeepLink = false;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(@NonNull AppInviteInvitationResult result) {
                                if (result.getStatus().isSuccess()) {
                                    // Extract deep link from Intent
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);

                                    Log.d("NewsList", "onResult: " + deepLink);

                                    int endIndex = deepLink.indexOf("?");
                                    String pushKeyId = deepLink.substring(25, endIndex);
                                    Log.d("NewsList", "onResult: " + pushKeyId);
                                    openNewsFeedActivity(pushKeyId);

                                    // Handle the deep link. For example, open the linked
                                    // content, or apply promotional credit to the user's
                                    // account.

                                    // ...
                                } else {
                                    Log.d("NewsList", "getInvitation: no deep link found.");
                                    initiaizeNewsInfoArrayList();
                                    initializeRecyclerView();


                                }
                            }
                        });

        Log.d("NewsList", "onResult: ");

    }


    private void initializeRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.content_news_list_recyclerView);
        newsListRecyclerAdapter = new NewsListRecyclerAdapter(newsMetaInfoArrayList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(newsListRecyclerAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Toast.makeText(NewsListActivity.this, "Item clicked "+position, Toast.LENGTH_SHORT).show();
                openNewsFeedActivity(newsMetaInfoArrayList.get(position));
            }

            @Override
            public void onLongClick(View view, int position) {


            }
        }));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    onScrolledToBottom();
                    Toast.makeText(NewsListActivity.this, "Refreshing", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onScrolledToBottom() {
        //Toast.makeText(this, "Scrolled to bootom", Toast.LENGTH_SHORT).show();
        if (isLoadingMoreArticle) {

            loadMoreArticle();
        } else {
            isLoadingMoreArticle = true;
        }
    }

    private void loadMoreArticle() {
        DatabaseHandlerFirebase databaseHandlerFirebase = new DatabaseHandlerFirebase();
        databaseHandlerFirebase.addNewsListListner(new DatabaseHandlerFirebase.DataBaseHandlerNewsListListner() {
            @Override
            public void onNewsList(ArrayList<NewsMetaInfo> newsMetaInfoArrayList) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onNoticePost(boolean isSuccessful) {

            }

            @Override
            public void onNewsImageFetched(boolean isFetchedImage) {

            }

            @Override
            public void onNewsInfo(NewsInfo newsInfo) {

            }

            @Override
            public void ongetNewsListMore(ArrayList<NewsMetaInfo> newsMetaInfoArrayListMore) {
                newsMetaInfoArrayListMore.remove(newsMetaInfoArrayListMore.size() - 1);
                for (int i = newsMetaInfoArrayListMore.size() - 1; i >= 0; i--) {
                    NewsListActivity.this.newsMetaInfoArrayList.add(newsMetaInfoArrayListMore.get(i));

                }

                isLoadingMoreArticle = false;
                newsListRecyclerAdapter.notifyDataSetChanged();
                Toast.makeText(NewsListActivity.this, "Done loading", Toast.LENGTH_SHORT).show();

            }
        });
        databaseHandlerFirebase.getNewsListMore(newsMetaInfoArrayList.get(newsMetaInfoArrayList.size() - 1).getNewsPushKeyId(), 10);

    }

    private void openNewsFeedActivity(NewsMetaInfo newsMetaInfo) {
        Intent intent = new Intent(this, NewsFeedActivity.class);
        intent.putExtra("ByLink", false);
        intent.putExtra("PushKeyId", newsMetaInfo.getNewsPushKeyId());
        intent.putExtra("NewsHeading", newsMetaInfo.getNewsHeading());
        intent.putExtra("NewsImageLocalPath", newsMetaInfo.getNewsImageLocalPath());
        startActivity(intent);
    }

    private void openNewsFeedActivity(String pushKeyId) {
        Intent intent = new Intent(this, NewsFeedActivity.class);
        intent.putExtra("ByLink", true);
        intent.putExtra("PushKeyId", pushKeyId);

        startActivity(intent);
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
        getMenuInflater().inflate(R.menu.news_list, menu);
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

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initiaizeNewsInfoArrayList() {
       /* for (int i = 0; i < 10; i++) {
            NewsMetaInfo newsMetaInfo = new NewsMetaInfo();
            newsMetaInfo.setNewsDate("20/04/2017");
            newsMetaInfo.setNewsSource("Technology");
            newsMetaInfo.setNewsHeading("Heading text nothing to add so just dummy line here and there to test "+i);
            newsMetaInfoArrayList.add(newsMetaInfo);
        }
*/

        DatabaseHandlerFirebase databaseHandlerFirebase = new DatabaseHandlerFirebase();
        databaseHandlerFirebase.getNewsList(10);
        databaseHandlerFirebase.addNewsListListner(new DatabaseHandlerFirebase.DataBaseHandlerNewsListListner() {
            @Override
            public void onNewsList(ArrayList<NewsMetaInfo> newsMetaInfoArrayList) {


                for (NewsMetaInfo newsMetaInfo : newsMetaInfoArrayList) {

                    //newsMetaInfo.resolveNewsTimeString();
                    NewsListActivity.this.newsMetaInfoArrayList.add(newsMetaInfo);
                }

                newsListRecyclerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onNoticePost(boolean isSuccessful) {

            }

            @Override
            public void onNewsImageFetched(boolean isFetchedImage) {
                newsListRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNewsInfo(NewsInfo newsInfo) {

            }

            @Override
            public void ongetNewsListMore(ArrayList<NewsMetaInfo> newsMetaInfoArrayListMore) {

            }
        });


    }


}

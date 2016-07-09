package com.hankarun.gevrek;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.util.LruCache;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.test.suitebuilder.TestMethod;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.hankarun.gevrek.models.CowMessage;
import com.hankarun.gevrek.models.MessageHeader;
import com.hankarun.gevrek.models.NewsGroup;
import com.wunderlist.slidinglayer.SlidingLayer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Main2Activity extends ThemedBaseActivity implements LoaderManager.LoaderCallbacks{

    enum FABSTATE{
        REPLY,
        NEWMESSAGE,
        POSTMESSAGE
    }

    FABSTATE fabstate = FABSTATE.NEWMESSAGE;

    @BindView(R.id.groupMesagesRecycle)
    RecyclerView mRecyclerView;


    @BindView(R.id.writeBodyEdit)
    EditText mWriteBody;

    @BindView(R.id.writeFromEdit)
    EditText mWriteFrom;

    @BindView(R.id.writeSubjectEdit)
    EditText mWriteSubject;

    FoodAdapter1 mAdapter;
    SlidingLayer mSlidingLayer;
    SlidingLayer mSlidingLayer2;
    FloatingActionButton fab;

    ProgressBar mReadMessageProgressBar;

    TextView author;
    TextView body;
    TextView date;

    ScrollView mScrollView;

    NetworkImageView avatar;

    RequestQueue mRequestQueue;
    ImageLoader mImageLoader;

    LinearLayout mReadMessage;

    SwipeRefreshLayout mSwipeRefreshLayout;

    String url;

    boolean messageReading = false;
    boolean messageWriting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ButterKnife.bind(this);

        Bundle data = getIntent().getExtras();
        url = data.getString("link");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml(data.getString("group")));

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabClickAction();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = new FoodAdapter1(null, R.layout.message_row, this);
        mSlidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer1);


        mSlidingLayer.setOnInteractListener(new SlidingLayer.OnInteractListener() {
            @Override
            public void onOpen() {}

            @Override
            public void onShowPreview() {

            }

            @Override
            public void onClose() {
                messageReading = false;
                fab.setImageResource(android.R.drawable.ic_dialog_email);
            }

            @Override
            public void onOpened() {}

            @Override
            public void onPreviewShowed() {

            }

            @Override
            public void onClosed() {}
        });

        mSlidingLayer2 = (SlidingLayer) findViewById(R.id.slidingLayer2);

        mScrollView = (ScrollView) findViewById(R.id.scrollView);

        mRecyclerView = (RecyclerView) findViewById(R.id.groupMesagesRecycle);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        author = (TextView) findViewById(R.id.author);
        body = (TextView) findViewById(R.id.body);
        date = (TextView) findViewById(R.id.dateText);
        mReadMessage = (LinearLayout) findViewById(R.id.toHide);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.newsgroupSwipe1);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSupportLoaderManager().restartLoader(1,null,Main2Activity.this);
            }
        });

        avatar = (NetworkImageView)findViewById(R.id.imageView2);

        mReadMessageProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });

        getSupportLoaderManager().initLoader(1,null,this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Loader loader = null;
        switch (id)
        {
            case 1:
                loader = new MessageLoader(getApplicationContext(),url);
                break;
            case 2:
                mReadMessage.setVisibility(View.GONE);
                mReadMessageProgressBar.setVisibility(View.VISIBLE);
                mScrollView.setVisibility(View.GONE);
                loader = new MessageDetailLoader(getApplicationContext(), args.getString("url"));
                break;
            case 3:
                loader = new NewMessageLoader(getApplicationContext(), args.getString("url"));
                break;
        }
        return loader;
    }

    private void fabClickAction()
    {
        switch (fabstate) {
            case REPLY:
                mSlidingLayer2.openLayer(true);
                fabstate = FABSTATE.POSTMESSAGE;
                break;
            case NEWMESSAGE:
                Bundle args = new Bundle();
                args.putString("url",url);
                getSupportLoaderManager().restartLoader(3,args,Main2Activity.this);
                mSlidingLayer2.openLayer(true);
                fabstate = FABSTATE.POSTMESSAGE;
                break;
            case POSTMESSAGE:
                mSlidingLayer2.closeLayer(true);
                mSlidingLayer.closeLayer(true);

                fabstate = FABSTATE.NEWMESSAGE;
                Snackbar.make(findViewById(R.id.coordinator), "Message send.", Snackbar.LENGTH_SHORT)
                        .show();
                getSupportLoaderManager().restartLoader(1,null,Main2Activity.this);
                mSwipeRefreshLayout.setRefreshing(true);
                break;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId())
        {
            case 1:
                mAdapter.setData((ArrayList<MessageHeader>)data);
                mSwipeRefreshLayout.setRefreshing(false);
                break;
            case 2:
                setMessageRead((CowMessage)data);
                break;
            case 3:
                setNewMessage((CowMessage)data);
                break;
        };
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mSlidingLayer2.isOpened()) {
                    mSlidingLayer2.closeLayer(true);
                    fab.setImageResource(android.R.drawable.ic_dialog_email);
                    fabstate = FABSTATE.REPLY;
                    return true;
                } else if(mSlidingLayer.isOpened()) {
                    mSlidingLayer.closeLayer(true);
                    fab.setImageResource(android.R.drawable.ic_dialog_email);
                    fabstate = FABSTATE.NEWMESSAGE;
                    return true;
                }
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    private void setNewMessage(CowMessage message)
    {
        mWriteSubject.setText(message.mSubject);
        mWriteBody.setText(message.mBody);
        mWriteFrom.setText(message.mFrom);
    }

    public void setMessageRead(CowMessage message)
    {

        author.setText(message.mFrom);
        body.setText(Html.fromHtml(message.mBody));
        date.setText(message.mDate.toString());
        avatar.setImageUrl(message.mImage,mImageLoader);
        mReadMessage.setVisibility(View.VISIBLE);
        mReadMessageProgressBar.setVisibility(View.GONE);
        mScrollView.setVisibility(View.VISIBLE);
    }

    public class FoodAdapter1 extends RecyclerView.Adapter<FoodAdapter1.ViewHolder> {
        ArrayList<MessageHeader> list;

        private int rowLayout;
        private Context mContext;

        public FoodAdapter1(ArrayList<MessageHeader> data, int rowLayout, Context context) {
            list = data;
            this.rowLayout = rowLayout;
            this.mContext = context;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
            return new ViewHolder(v);
        }

        void setData(ArrayList<MessageHeader> data)
        {
            list = data;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final MessageHeader meal = list.get(position);
            holder.bind(meal);

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fabstate = FABSTATE.REPLY;
                    fab.setImageResource(android.R.drawable.ic_media_pause);
                    Bundle args = new Bundle();
                    args.putString("url",meal.mMessage[1]);
                    args.putInt("position",position);
                    getSupportLoaderManager().restartLoader(2,args,Main2Activity.this);
                    mSlidingLayer.openLayer(true);
                    messageReading = true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public TextView date;
            public TextView message;
            public TextView author;

            public LinearLayout layout;

            public void bind(MessageHeader meal)
            {
                message.setText(Html.fromHtml(meal.mMessage[0]));
                author.setText(meal.mAuthor);
                date.setText(meal.mMessageDate);
            }

            public ViewHolder(View itemView) {
                super(itemView);
                layout = (LinearLayout) itemView.findViewById(R.id.messageHeaderLayout);
                date = (TextView) itemView.findViewById(R.id.dateTextView);
                message = (TextView) itemView.findViewById(R.id.messageHeaderText);
                author = (TextView) itemView.findViewById(R.id.authorTestView);
            }
        }
    }
    }

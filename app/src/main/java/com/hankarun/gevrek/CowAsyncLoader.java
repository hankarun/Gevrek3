package com.hankarun.gevrek;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class CowAsyncLoader<N> extends AsyncTaskLoader<N> {
    static Map<String,String> cookieMap = new HashMap<>();
    public String url;
    static Calendar cookieTime;

    public CowAsyncLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    private boolean checkTime()
    {
        if(cookieTime == null)
            return false;
        Calendar previous = Calendar.getInstance();
        previous.setTime(cookieTime.getTime());
        Calendar now = Calendar.getInstance();
        long diff = now.getTimeInMillis() - previous.getTimeInMillis();
        if(diff >= 10 * 60 * 1000)
        {
            cookieMap.clear();
        }
        return false;
    }

    public void clearCache()
    {
        cookieMap.clear();
    }

    Document get()
    {
        checkTime();
        if(cookieMap.size() > 0) {
            try {
                return Jsoup.connect(url)
                        .cookies(cookieMap)
                        .timeout(4000)
                        .get();
            } catch (IOException e) {
                return null;
            }
        }
        try {
            Connection.Response res = Jsoup.connect(url)
                    .data("username", "e175903", "password", "For9876um")
                    .method(Connection.Method.POST)
                    .timeout(4000)
                    .execute();

            cookieMap = res.cookies();
            cookieTime =  Calendar.getInstance();
            return res.parse();
        } catch (IOException e) {
            return null;
        }
    }

    public N getData(Document doc)
    {
        return null;
    }

    @Override
    public N loadInBackground() {
        Document doc = get();
        if(doc != null)
            return getData(doc);
        else
        {
            cookieMap.clear();
            doc = get();
            return getData(doc);
        }
    }
}

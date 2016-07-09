package com.hankarun.gevrek;


import android.content.Context;

import com.hankarun.gevrek.models.CowMessage;
import com.hankarun.gevrek.models.MessageHeader;
import com.hankarun.gevrek.models.NewsGroup;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

public class CowApi extends CowAsyncLoader {
    static Map<String,String> cookieMap = new HashMap<>();

    public CowApi(Context context) {
        super(context);
    }

    public Document get(String url)
    {
        if(cookieMap.size() > 0) {
            try {
                return Jsoup.connect(url)
                        .cookies(cookieMap)
                        .timeout(4000)
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Connection.Response res = Jsoup.connect(url)
                    .data("username", "e175903", "password", "For9876um")
                    .method(Connection.Method.POST)
                    .timeout(4000)
                    .execute();

            cookieMap = res.cookies();

            return res.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CowMessage parseMessage(String html)
    {
        CowMessage tempMessage = new CowMessage();
        Document doc = get(html);
        Elements buttons = doc.select("a.np_button");
        Elements articleHeader = doc.select("div.np_article_header");
        String header = articleHeader.first().text();

        int firstb = header.indexOf("Subject:");
        int secondb = header.indexOf("From:",firstb + 1);
        int thirdb = header.indexOf("Date:",secondb+1);

        tempMessage.mReplyTo = buttons.first().attr("href");
        tempMessage.mSubject = header.substring(9,secondb);
        tempMessage.mFrom = header.substring(secondb+6,thirdb);
        tempMessage.setDate(header.substring(thirdb+6));
        tempMessage.mBody = doc.select("div.np_article_body").first().text();

        return tempMessage;
    }

    public ArrayList<MessageHeader> parseGroupMessages(String html)
    {
        Document doc = get(html);
        Elements mainTable = doc.select("tr");
        ArrayList<MessageHeader> tempArray = new ArrayList<>();
        for(int x = 1; x < mainTable.size(); ++x)
        {
            MessageHeader tempHeader = new MessageHeader();
            Elements trEl = mainTable.get(x).select("td");
            if(trEl.size() > 3)
            {
                tempHeader.mMessageDate = trEl.get(0).select("span.np_thread_line_text").text();
                tempHeader.mMessage[0] = trEl.get(1).select("span.np_thread_line_text").select("a[href]").text();
                tempHeader.mMessage[1] = trEl.get(1).select("span.np_thread_line_text").select("a[href]").attr("href");
                tempHeader.mAuthor = trEl.get(3).select("span.np_thread_line_text").text();
                tempArray.add(tempHeader);
            }
        }
        return tempArray;
    }

    public LinkedHashMap<String,Vector<NewsGroup>> parseNewsGroups(String html)
    {
        Document doc = get(html);

        Elements ele = doc.select("div.np_index_groups");
        Element groups = ele.first().child(0);

        String groupName = "";
        LinkedHashMap<String,Vector<NewsGroup>> map = new LinkedHashMap<>();
        for(int x = 0; x < groups.childNodeSize(); x++)
        {
            if(groups.child(x).className().equals("np_index_grouphead")) {
                groupName = groups.child(x).text();
                map.put(groupName,new Vector<NewsGroup>());
            }

            if(groups.child(x).className().equals("np_index_groupblock"))
            {
                Elements names = groups.child(x).select("a[href]");
                Elements ss = groups.child(x).select("small");
                for(int d = 0; d < names.size(); ++d) {
                    NewsGroup temp = new NewsGroup();
                    temp.mName = names.get(d).text();
                    temp.mCount = ss.get(d).html();
                    temp.mUrl = names.get(d).attr("abs:href");
                    temp.mGroupName = groupName;
                    map.get(groupName).add(temp);
                }
            }
        }
        return map;
    }
}

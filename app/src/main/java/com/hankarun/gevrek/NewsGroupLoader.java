package com.hankarun.gevrek;

import android.content.Context;

import com.hankarun.gevrek.models.NewsGroup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedHashMap;
import java.util.Vector;

public class NewsGroupLoader extends CowAsyncLoader<LinkedHashMap<String,Vector<NewsGroup>>> {
    public NewsGroupLoader(Context context) {
        super(context);
        url = BaseUrls.groupsMainPage;
    }

    @Override
    public LinkedHashMap<String,Vector<NewsGroup>> getData(Document doc) {
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

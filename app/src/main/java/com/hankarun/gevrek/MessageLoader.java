package com.hankarun.gevrek;

import android.content.Context;

import com.hankarun.gevrek.models.MessageHeader;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class MessageLoader extends CowAsyncLoader<ArrayList<MessageHeader>> {
    public MessageLoader(Context context, String _url) {
        super(context);
        url = _url;
    }

    @Override
    public ArrayList<MessageHeader> getData(Document doc) {
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
}

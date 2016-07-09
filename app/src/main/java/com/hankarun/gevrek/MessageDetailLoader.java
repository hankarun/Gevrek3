package com.hankarun.gevrek;

import android.content.Context;

import com.hankarun.gevrek.models.CowMessage;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class MessageDetailLoader extends CowAsyncLoader<CowMessage>{
    public MessageDetailLoader(Context context, String _url) {
        super(context);
        url = "https://cow.ceng.metu.edu.tr/News/"+_url;
    }

    @Override
    public CowMessage getData(Document doc) {
        Elements buttons = doc.select("a.np_button");
        Elements articleHeader = doc.select("div.np_article_header");
        String header = articleHeader.first().text();

        int firstb = header.indexOf("Subject:");
        int secondb = header.indexOf("From:",firstb + 1);
        int thirdb = header.indexOf("Date:",secondb+1);

        CowMessage tempMessage = new CowMessage();
        tempMessage.mReplyTo = buttons.first().attr("href");
        tempMessage.mSubject = header.substring(9,secondb);
        tempMessage.mFrom = header.substring(secondb+6,thirdb);
        tempMessage.setDate(header.substring(thirdb+6));
        tempMessage.mBody = doc.select("div.np_article_body").first().text();
        tempMessage.mImage = doc.select("img").first().attr("src");

        return tempMessage;
    }
}

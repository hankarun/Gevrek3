package com.hankarun.gevrek.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CowMessage {
    public String      mSubject;
    public String      mFrom;
    public Date        mDate;
    public String      mBody;
    public String      mReplyTo;
    public String      mImage;

    public HashMap<String,String> replyParameters;

    public void        setDate(String _date)
    {
        DateFormat format = new SimpleDateFormat("MMMM d yyyy HH:mm:ss", Locale.ENGLISH);
        mDate = null;
        try {
            mDate = format.parse(_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String toString()
    {
        return "From:" + mFrom + " - Date: " + mDate.toString() + " Body: " + mBody;
    }
}

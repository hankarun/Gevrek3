package com.hankarun.gevrek.models;

public class MessageHeader {
    public MessageHeader()
    {
        mMessage = new String[2];
    }
    public int    mGroupId;
    public String[] mMessage;
    public String mMessageDate;
    public String mMessageHeader;
    public String mAuthor;
}
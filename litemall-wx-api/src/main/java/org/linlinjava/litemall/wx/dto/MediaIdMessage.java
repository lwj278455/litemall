package org.linlinjava.litemall.wx.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.linlinjava.litemall.wx.annotation.XStreamCDATA;

public class MediaIdMessage {
    @XStreamAlias("MediaId")
    @XStreamCDATA
    private String MediaId;

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }

}

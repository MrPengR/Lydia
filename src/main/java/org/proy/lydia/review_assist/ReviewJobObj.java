package org.proy.lydia.review_assist;

public class ReviewJobObj {
    private String msgID;
    private String filePath;

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public ReviewJobObj(String msgID, String filePath) {
        this.msgID = msgID;
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getMsgID() {
        return msgID;
    }
}

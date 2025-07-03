package pt.estg.sd.alertops.components;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class Message {
    private int id;
    private int senderId;
    private String senderName;
    private int recipientId;
    private int channelId;
    private String content;
    private Timestamp timestamp;

    public Message() {
    }

    public Message(int senderId, String senderName, int recipientId, int channelId, String content, Timestamp timestamp) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.recipientId = recipientId;
        this.channelId = channelId;
        this.content = content;
        this.timestamp = timestamp;
    }
}

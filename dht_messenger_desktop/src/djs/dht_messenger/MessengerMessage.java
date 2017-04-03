package djs.dht_messenger;

import java.io.Serializable;

/**
 * Created by djsteffey on 3/24/2017.
 */
public class MessengerMessage implements Serializable{

    // variables
    private String m_source_id;
    private String m_destination_id;
    private byte[] m_message;

    // functions
    public MessengerMessage(String source_id, String destination_id, byte[] message){
        this.m_source_id = source_id;
        this.m_destination_id = destination_id;
        this.m_message = message;
    }

    public String get_source_id(){
        return this.m_source_id;
    }

    public String get_destination_id(){
        return this.m_destination_id;
    }

    public byte[] get_message(){
        return this.m_message;
    }
}

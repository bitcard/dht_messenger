package djs.dht_messenger;

import java.io.Serializable;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by djsteffey on 3/24/2017.
 */
public class DHTValue implements Serializable{

    // variables
    private String m_ip;
    private int m_port;
    byte[] m_public_key;

    // functions
    public DHTValue(String ip, int port, PublicKey public_key){
        this.m_ip = ip;
        this.m_port = port;
        this.m_public_key = public_key.getEncoded();
    }

    public String get_ip(){
        return this.m_ip;
    }

    public int get_port(){
        return this.m_port;
    }

    public PublicKey get_public_key() throws Exception{
        X509EncodedKeySpec spec = new X509EncodedKeySpec(this.m_public_key);
        KeyFactory f = KeyFactory.getInstance("RSA");
        return f.generatePublic(spec);
    }
}

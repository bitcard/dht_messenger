package djs.simpledht;

import java.io.Serializable;

/**
 * Created by djsteffey on 3/20/2017.
 */
public abstract class DHT {
    public interface DHTListener{
        void on_dht_create_async_complete(boolean success, String message);
        void on_dht_join_async_complete(boolean success, String message);
        void on_dht_put_async_complete(String key, boolean success, String message);
        void on_dht_get_async_complete(String key, Serializable object, String message);
    }

    // variables
    protected DHTListener m_listener;
    protected String m_string_id;


    // functions
    public DHT(DHTListener listener){
        this.m_listener = listener;
    }

    public abstract void shutdown();

    public abstract boolean create(String id, int local_port);
    public abstract void create_async(String id, int local_port);

    public abstract boolean join(String id, int local_port, String bootstrap_host, int bootstrap_port);
    public abstract void join_async(String id, int local_port, String bootstrap_host, int bootstrap_port);

    public abstract boolean put(String key, Serializable o);
    public abstract void put_async(final String key, final Serializable o);

    public abstract Serializable get(String key);
    public abstract void get_async(final String key, final Serializable object);

    public String get_string_id(){
        return this.m_string_id;
    }

    public abstract String get_my_ip();
    public abstract int get_my_port();
}

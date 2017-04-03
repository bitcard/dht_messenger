package djs.simpledht;

import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.FuturePut;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.BaseFuture;
import net.tomp2p.futures.BaseFutureListener;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.Number640;
import net.tomp2p.storage.Data;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Map;
import java.util.NavigableMap;

/**
 * Created by djsteffey on 3/21/2017.
 */
public class DHTTomP2P extends DHT{

    // variables
    private PeerDHT m_tomp2p_dht;

    // functions
    public DHTTomP2P(DHTListener listener){
        super(listener);
        this.m_tomp2p_dht = null;
    }

    @Override
    public void shutdown(){
        // remove our entry in the DHT
        try {
            this.m_tomp2p_dht.remove(this.m_tomp2p_dht.peerID()).start().awaitUninterruptibly();
        } catch (Exception e){
            e.printStackTrace();
        }
        // shut down our dht
        this.m_tomp2p_dht.shutdown().awaitUninterruptibly();
    }

    @Override
    public boolean create(String id, int local_port){
        try {
            // save the id
            this.m_string_id = id;
            // create a new dht
            this.m_tomp2p_dht = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(id)).ports(local_port).start()).start();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void create_async(String id, int local_port){
        try {
            // save the id
            this.m_string_id = id;
            // create new dht
            this.m_tomp2p_dht = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(id)).ports(local_port).start()).start();
        } catch (Exception e){
            e.printStackTrace();
            this.m_listener.on_dht_create_async_complete(false, e.toString());
        }
        this.m_listener.on_dht_create_async_complete(true, "");
    }

    @Override
    public boolean join(String id, int local_port, String bootstrap_host, int bootstrap_port){
        try {
            // save the id
            this.m_string_id = id;

            // create a dht node
            this.m_tomp2p_dht = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(id)).ports(local_port).start()).start();

            // bootstrap to an existing one
            FutureBootstrap fb = this.m_tomp2p_dht.peer().bootstrap().inetAddress(InetAddress.getByName(bootstrap_host)).ports(bootstrap_port).start();

            // wait for the bootstrap to complete
            fb.awaitUninterruptibly();

            // test if successful
            if (fb.isSuccess()){
                // not sure, but tom p2p example uses it
                this.m_tomp2p_dht.peer().discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
                return true;
            }

            return false;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void join_async(String id, int local_port, String bootstrap_host, int bootstrap_port){
        try {
            // save the id
            this.m_string_id = id;

            // create a dht node
            this.m_tomp2p_dht = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(id)).ports(local_port).start()).start();

            // bootstrap to an existing one
            final FutureBootstrap fb = this.m_tomp2p_dht.peer().bootstrap().inetAddress(InetAddress.getByName(bootstrap_host)).ports(bootstrap_port).start();

            // attach a listener to wait for it to complete
            fb.addListener(new BaseFutureListener<BaseFuture>() {
                @Override
                public void operationComplete(BaseFuture future) throws Exception {
                    if (future.isSuccess()){
                        // success
                        DHTTomP2P.this.m_listener.on_dht_join_async_complete(true, "");
                    }else{
                        // failed
                        DHTTomP2P.this.m_listener.on_dht_join_async_complete(false, fb.failedReason());
                    }
                }

                @Override
                public void exceptionCaught(Throwable throwable) throws Exception {
                    // exception
                    DHTTomP2P.this.m_listener.on_dht_join_async_complete(false, throwable.toString());
                }
            });
        } catch (Exception e){
            e.printStackTrace();
            DHTTomP2P.this.m_listener.on_dht_join_async_complete(false, e.toString());
        }
    }

    @Override
    public boolean put(String key, Serializable o){
        try {
            this.m_tomp2p_dht.put(Number160.createHash(key)).data(new Data(o)).start().awaitUninterruptibly();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void put_async(final String key, final Serializable o){
        // create the put request
        FuturePut future_put = null;
        try {
            future_put = this.m_tomp2p_dht.put(Number160.createHash(key)).data(new Data(o)).start();
        }catch (Exception e){
            DHTTomP2P.this.m_listener.on_dht_put_async_complete(key, false, e.toString());
        }

        // attach a listener to wait for it to complete
        future_put.addListener(new BaseFutureListener<BaseFuture>() {
            @Override
            public void operationComplete(BaseFuture future) throws Exception {
                if (future.isSuccess()){
                    // success
                    DHTTomP2P.this.m_listener.on_dht_put_async_complete(key, true, "");
                }else{
                    // failed
                    DHTTomP2P.this.m_listener.on_dht_put_async_complete(key, false, "Unable to store object");
                }
            }

            @Override
            public void exceptionCaught(Throwable throwable) throws Exception {
                // exception
                DHTTomP2P.this.m_listener.on_dht_put_async_complete(key, false, throwable.toString());
            }
        });
    }

    @Override
    public Serializable get(String key){
        FutureGet futureGet = this.m_tomp2p_dht.get(Number160.createHash(key)).start();
        futureGet.awaitUninterruptibly();
        try {
            if (futureGet.isSuccess()) {
                return (Serializable)(futureGet.dataMap().values().iterator().next().object());
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public void get_async(final String key, final Serializable object){
        // create the get request
        final FutureGet futureGet = this.m_tomp2p_dht.get(Number160.createHash(key)).start();

        // attach a listener to wait for the answer
        futureGet.addListener(new BaseFutureListener<BaseFuture>() {
            @Override
            public void operationComplete(BaseFuture future) throws Exception {
                if (future.isSuccess() && futureGet.data() != null){
                    // found it
                    DHTTomP2P.this.m_listener.on_dht_get_async_complete(key, (Serializable)(futureGet.data().object()), "");
                }else{
                    // couldnt find it
                    DHTTomP2P.this.m_listener.on_dht_get_async_complete(key, null, "Object not found");
                }
            }

            @Override
            public void exceptionCaught(Throwable throwable) throws Exception {
                // exception
                DHTTomP2P.this.m_listener.on_dht_get_async_complete(key, null, throwable.toString());
            }
        });
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append(this.m_string_id);
        sb.append(": ");
        sb.append(this.m_tomp2p_dht.peerAddress().toString());


        NavigableMap<Number640, Data> map = this.m_tomp2p_dht.storageLayer().get();
        for(Map.Entry<Number640, Data> entry: map.entrySet()){
            sb.append("\n\t");
            try {
                sb.append(entry.getKey().toString());
                sb.append("=" + entry.getValue().object().toString());
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    @Override
    public String get_my_ip(){
        return this.m_tomp2p_dht.peerAddress().inetAddress().getHostAddress();
    }

    @Override
    public int get_my_port(){
        return this.m_tomp2p_dht.peerAddress().tcpPort();
    }
}

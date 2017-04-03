package djs.dht_messenger;

import android.util.Log;

import djs.simpledht.DHT;
import djs.simpledht.DHTTomP2P;
import javax.crypto.Cipher;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;

/**
 * Created by djsteffey on 3/24/2017.
 */
public class Messenger implements DHT.DHTListener{

    public interface MessengerListener{
        void on_create_async_complete(boolean success, String reason);
        void on_join_async_complete(boolean success, String reason);
        void on_send_message_async_complete(boolean success, String reason, String id, String message);
        void on_received_message(String source_id, String message);
    }

    // constants
    private static final int SERVER_LISTEN_SOCKET_BACKLOG = 10;
    private static final int SEND_CONNECT_TIMEOUT_IN_MILLISECONDS = 2000;
    private static final int SEND_CONNECT_TIMEOUT_ATTEMPTS = 3;
    private static final int DHT_GET_ATTEMPTS = 5;
    private static final int DHT_GET_COOLDOWN_IN_MILLISECONDS = 1000;

    // variables
    private String m_id;
    private ServerSocket m_listen_socket;
    private DHT m_dht;
    private MessengerListener m_listener;
    private PublicKey m_public_key;
    private PrivateKey m_private_key;

    // properties
    public Messenger(){
        this.m_id = null;
        this.m_listen_socket = null;
        this.m_dht = null;
        this.m_listener = null;
        this.m_public_key = null;
        this.m_private_key = null;
    }

    public void shutdown(){
        try {
            this.m_listen_socket.close();
        } catch(Exception e){
            e.printStackTrace();
        }
        this.m_dht.shutdown();
    }

    public void set_listener(MessengerListener listener){
        this.m_listener = listener;
    }

    public boolean create(String id, int messenger_port, int dht_port, MessengerListener listener) throws Exception{
        // save my id
        this.m_id = id;

        // save the listener
        this.m_listener = listener;

        // create our public and private keys
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair key_pair = kpg.generateKeyPair();
        this.m_public_key = key_pair.getPublic();
        this.m_private_key = key_pair.getPrivate();


        // create a server listener socket to listen for incoming messages
        this.m_listen_socket = new ServerSocket(messenger_port, SERVER_LISTEN_SOCKET_BACKLOG);

        // run the listen socket in a thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                // run forever, until the listen socket is closed
                Socket socket = null;
                while (true){
                    try {
                        // accept a pending connection, this will block until one is accepted
                        socket = Messenger.this.m_listen_socket.accept();
                    } catch (Exception e){
                        // exception occurs when listen socket is closed, so return from the thread
                        e.printStackTrace();
                        return;
                    }

                    // handle the new connection
                    Messenger.this.handle_async_new_socket_connection(socket);
                }
            }
        }).start();

        // create the dht
        this.m_dht = new DHTTomP2P(this);
        if (this.m_dht.create(this.m_id, dht_port) == false){
            // couldnt create the dht
            return false;
        }

        // insert our own messenger connection info into the dht
        String my_ip = this.m_dht.get_my_ip();
        DHTValue value = new DHTValue(my_ip, messenger_port, this.m_public_key);
        if (this.m_dht.put(this.m_id, value) == false){
            // couldnt insert our own key
            return false;
        }

        // everything worked
        return true;
    }

    public void create_async(final String id, final int messenger_port, final int dht_port, final MessengerListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if (Messenger.this.create(id, messenger_port, dht_port, listener)){
                        Messenger.this.m_listener.on_create_async_complete(true, "New messenger group created");
                    } else{
                        Messenger.this.m_listener.on_create_async_complete(false, "Unable to create new messenger group");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Messenger.this.m_listener.on_create_async_complete(false, e.toString());
                }
            }
        }).start();
    }

    public boolean join(String id, int messenger_port, int dht_port, String bootstrap_host, int bootstrap_port,
                        MessengerListener listener) throws Exception{
        // save my id
        this.m_id = id;

        // save the listener
        this.m_listener = listener;

        // create our public and private keys
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        KeyPair key_pair = kpg.generateKeyPair();
        this.m_public_key = key_pair.getPublic();
        this.m_private_key = key_pair.getPrivate();

        // create a server listener socket to listen for incoming messages
        this.m_listen_socket = new ServerSocket(messenger_port, SERVER_LISTEN_SOCKET_BACKLOG);

        // run the listen socket in a thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                // run forever, until the listen socket is closed
                Socket socket = null;
                while (true){
                    try {
                        // accept a pending connection, this will block until one is accepted
                        socket = Messenger.this.m_listen_socket.accept();
                    } catch (Exception e){
                        // exception occurs when listen socket is closed, so return from the thread
                        e.printStackTrace();
                        return;
                    }

                    // handle the new connection
                    Messenger.this.handle_async_new_socket_connection(socket);
                }
            }
        }).start();

        // create the dht
        this.m_dht = new DHTTomP2P(this);
        if (this.m_dht.join(this.m_id, dht_port, bootstrap_host, bootstrap_port) == false){
            // couldnt join the dht
            return false;
        }

        // insert our own messenger connection info into the dht
        String my_ip = this.m_dht.get_my_ip();
        DHTValue value = new DHTValue(my_ip, messenger_port, this.m_public_key);
        if (this.m_dht.put(this.m_id, value) == false){
            // couldnt insert our own key
            return false;
        }

        // everything worked
        return true;
    }

    public void join_async(final String id, final int messenger_port, final int dht_port, final String bootstrap_host, final int bootstrap_port, final MessengerListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if (Messenger.this.join(id, messenger_port, dht_port, bootstrap_host, bootstrap_port, listener)){
                        Messenger.this.m_listener.on_join_async_complete(true, "Joined messenger group success");
                    } else{
                        Messenger.this.m_listener.on_join_async_complete(false, "Unable to join messenger group");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Messenger.this.m_listener.on_join_async_complete(false, e.toString());
                }
            }
        }).start();
    }

    public boolean send_message(String id, String message){
        // try x times to get the connection info from the dht
        DHTValue value = null;
        for (int i = 0; i < DHT_GET_ATTEMPTS; ++i) {
            value = (DHTValue) this.m_dht.get(id);
            if (value != null) {
                // found it
                break;
            }
            // didnt find it yet
            try{
                Thread.sleep(DHT_GET_COOLDOWN_IN_MILLISECONDS);
            } catch (Exception e){
                // dont care if sleep is interrupted
            }
        }

        if (value == null){
            // never found it
            Log.v("Messenger", "Unable to locate connection info for " + id);
            return false;
        }

        // now open a socket connection and send the message
        boolean send_success = false;
        for (int i = 0; i < SEND_CONNECT_TIMEOUT_ATTEMPTS; ++i) {
            Socket socket = null;
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(value.get_ip(), value.get_port()), SEND_CONNECT_TIMEOUT_IN_MILLISECONDS);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch(Exception e1){
                    e1.printStackTrace();
                }
                // try again
                continue;
            }

            // get an output stream to write the message to
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(socket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch(Exception e1){
                    e1.printStackTrace();
                }
                // try again
                continue;
            }

            // write to the stream a message
            MessengerMessage messenger_message = null;

            try {
                messenger_message = new MessengerMessage(this.m_id, id,
                        encrypt(message.getBytes(), value.get_public_key()));
                oos.writeObject(messenger_message);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch(Exception e1){
                    e1.printStackTrace();
                }
                // try again
                continue;
            }

            // if we get here then is passed all of the above
            send_success = true;
            break;
        }
        return send_success;
    }

    public void send_message_async(final String id, final String message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if (Messenger.this.send_message(id, message)){
                        Messenger.this.m_listener.on_send_message_async_complete(true, "Message sent to " + id, id, message);
                    } else{
                        Messenger.this.m_listener.on_send_message_async_complete(false, "Unable to send message to " + id, id, message);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Messenger.this.m_listener.on_send_message_async_complete(false, e.toString(), id, message);
                }
            }
        }).start();
    }

    private void handle_async_new_socket_connection(final Socket socket){
        // new connection just received from the listen socket
        new Thread(new Runnable() {
            @Override
            public void run() {
                // receive the messenger message
                ObjectInputStream ois = null;
                try {
                    ois = new ObjectInputStream(socket.getInputStream());
                } catch (Exception e){
                    e.printStackTrace();
                    return;
                }
                MessengerMessage message = null;
                try {
                    message = (MessengerMessage) ois.readObject();
                    Messenger.this.m_listener.on_received_message(message.get_source_id(),
                            new String(decrypt(message.get_message(), Messenger.this.m_private_key)));
                } catch(Exception e){
                    e.printStackTrace();
                    return;
                }

            }
        }).start();
    }

    public String get_string_id(){
        return this.m_id;
    }

    public String get_ip_string(){
        return this.m_dht.get_my_ip();
    }

    public int get_dht_port(){
        return this.m_dht.get_my_port();
    }

    private static byte[] encrypt(byte[] input, PublicKey public_key) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, public_key);
        return cipher.doFinal(input);
    }

    private static byte[] decrypt(byte[] input, PrivateKey private_key) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, private_key);
        return cipher.doFinal(input);
    }

    @Override
    public void on_dht_create_async_complete(boolean success, String message) {
        assert false;
    }

    @Override
    public void on_dht_join_async_complete(boolean success, String message) {
        assert false;
    }

    @Override
    public void on_dht_put_async_complete(String key, boolean success, String message) {
        assert false;
    }

    @Override
    public void on_dht_get_async_complete(String key, Serializable object, String message) {
        assert false;
    }
}

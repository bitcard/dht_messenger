package djs.dht_messenger.android;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import djs.dht_messenger.Messenger;

/**
 * Created by djsteffey on 3/29/2017.
 */

public class DhtMessengerService extends Service{//} implements Messenger.MessengerListener{

    // listener
    protected interface Listener {
        void on_create_complete(boolean success);
        void on_join_complete(boolean success);
        void on_send_complete(boolean success, String id, String message);
        void on_receive_complete(boolean success, String id, String message);
        void on_disconnect_complete();
    }

    // constants
    private static final int MAX_STORED_MESSAGES = 50;
    private static final String TAG = DhtMessengerService.class.toString();

    // variables
    private static boolean s_running = false;
    private static boolean s_connected = false;
    private static Messenger s_messenger = null;
    private static Listener s_listener = null;
    private static List<String[]> s_stored_messages = null;


    // functions
    public DhtMessengerService(){
        Log.v(TAG, "DhtMessengerService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.v(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startID) {
        Log.v(TAG, "onStartCommand");
        Log.v(TAG, "running = " + Boolean.toString(s_running));
        // first determine if we are already started
        if (!s_running) {
            // mark as running
            s_running = true;

            // still not connected
            s_connected = false;

            // create the messenger
            s_messenger = new Messenger();

            // store messages
            s_stored_messages = new ArrayList<>();
        }

        // let android know we want to keep this service started until explicitly stopped
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }

    // callbacks from the messenger
  /*
    @Override
    public void on_create_async_complete(boolean success, String reason) {
        // unused
    }

    @Override
    public void on_join_async_complete(boolean success, String reason) {
        // unused
    }

    @Override
    public void on_send_message_async_complete(boolean success, String reason, String id, String message) {
        // unused
    }

    @Override
    public void on_received_message(String source_id, String message) {
        // add it to the stored messages
        s_stored_messages.add(new String[]{source_id, message, "r"});
        // make sure not too many stored messages
        if (s_stored_messages.size() > DhtMessengerService.MAX_STORED_MESSAGES) {
            // remove the first one
            s_stored_messages.remove(0);
        }

        // if we have a listener then let them know of the new message
        if (s_listener != null) {
            s_listener.on_receive_complete(true, source_id, message);
        }
    }
*/
    // static methods
    public static void create_connection(final String id) {
        if (!s_running) {
            // not yet running
            return;
        }
        if (s_connected) {
            // already connected
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Random random = new Random();
                    int messenger_port = random.nextInt(63001) + 1024;
                    int dht_port = random.nextInt(63001) + 1024;
                    if (s_messenger.create(id, messenger_port, dht_port, new Messenger.MessengerListener() {
                        @Override
                        public void on_create_async_complete(boolean success, String reason) {
                            // not used
                        }

                        @Override
                        public void on_join_async_complete(boolean success, String reason) {
                            // not used
                        }

                        @Override
                        public void on_send_message_async_complete(boolean success, String reason, String id, String message) {
                            // not used
                        }

                        @Override
                        public void on_received_message(String source_id, String message) {
                            // add it to the list of messages
                            s_stored_messages.add(new String[]{id, message, "r"});
                            // check to see if we have too many messages
                            while (s_stored_messages.size() > MAX_STORED_MESSAGES){
                                s_stored_messages.remove(0);
                            }
                            // if we have a listener then inform them
                            if (s_listener != null){
                                s_listener.on_receive_complete(true, id, message);
                            }
                        }
                    })) {
                        // create was successful
                        s_connected = true;
                        if (s_listener != null){
                            s_listener.on_create_complete(true);
                        }
                    } else {
                        // create failed
                        s_connected = false;
                        if (s_listener != null){
                            s_listener.on_create_complete(false);
                        }
                    }
                } catch (Exception e) {
                    // create failed
                    s_connected = false;
                    if (s_listener != null){
                        s_listener.on_create_complete(false);
                    }
                }
            }
        }).start();
    }

    public static void join_connection(final String id, final String bootstrap_host, final int bootstrap_port) {
        if (!s_running) {
            // not yet running
            return;
        }
        if (s_connected) {
            // already connected
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Random random = new Random();
                    int messenger_port = random.nextInt(63001) + 1024;
                    int dht_port = random.nextInt(63001) + 1024;
                    if (s_messenger.join(id, messenger_port, dht_port, bootstrap_host, bootstrap_port, new Messenger.MessengerListener() {
                        @Override
                        public void on_create_async_complete(boolean success, String reason) {
                            // not used
                        }

                        @Override
                        public void on_join_async_complete(boolean success, String reason) {
                            // not used
                        }

                        @Override
                        public void on_send_message_async_complete(boolean success, String reason, String id, String message) {
                            // not used
                        }

                        @Override
                        public void on_received_message(String source_id, String message) {
                            // add it to the list of messages
                            s_stored_messages.add(new String[]{id, message, "r"});
                            // check to see if we have too many messages
                            while (s_stored_messages.size() > MAX_STORED_MESSAGES){
                                s_stored_messages.remove(0);
                            }
                            // if we have a listener then inform them
                            if (s_listener != null){
                                s_listener.on_receive_complete(true, id, message);
                            }
                        }
                    })) {
                        // join was successful
                        s_connected = true;
                        if (s_listener != null){
                            s_listener.on_join_complete(true);
                        }
                    } else {
                        // join failed
                        s_connected = false;
                        if (s_listener != null){
                            s_listener.on_join_complete(false);
                        }
                    }
                } catch (Exception e) {
                    // join failed
                    s_connected = false;
                    if (s_listener != null){
                        s_listener.on_join_complete(false);
                    }
                }
            }
        }).start();
    }

    public static void disconnect() {
        if (!s_running) {
            // not yet running
            return;
        }
        if (!s_connected) {
            // not connected
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                s_connected = false;
                s_messenger.shutdown();
                s_messenger = null;
                s_running = false;
                s_stored_messages = null;

                // send back a success if we have a listener
                if (s_listener != null) {
                    s_listener.on_disconnect_complete();
                }
            }
        }).start();
    }

    public static void send(final String id, final String message) {
        if (!s_running) {
            // not yet running
            return;
        }
        if (!s_connected) {
            // not connected
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (s_messenger.send_message (id, message)){
                        // send successfully
                        // add it to the stored messages
                        s_stored_messages.add(new String[]{id, message, "s"});
                        // make sure not too many stored messages
                        while (s_stored_messages.size() > MAX_STORED_MESSAGES) {
                            // remove the first one
                            s_stored_messages.remove(0);
                        }

                        // send back a success if we have a listener
                        if (s_listener != null) {
                            s_listener.on_send_complete(true, id, message);
                        }
                    } else{
                        // send back a failure if we have a listener
                        if (s_listener != null) {
                            s_listener.on_send_complete(false, id, message);
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    // send back a failure if we have a listener
                    if (s_listener != null) {
                        s_listener.on_send_complete(false, id, message);
                    }
                }
            }
        }).start();
    }

    public static void set_listener(Listener listener) {
        s_listener = listener;
    }

    public static boolean get_is_running(){
        return s_running;
    }

    public static boolean get_is_connected(){
        return s_connected;
    }

    public static Messenger get_messenger(){
        return s_messenger;
    }

    public static List<String[]> get_stored_messages(){
        return s_stored_messages;
    }
}

package djs.dht_messenger.desktop;

import djs.dht_messenger.Messenger;
import djs.dht_messenger.simulator.DHTSimulator;
import javafx.application.Application;

import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws Exception{

        run_desktop_application();
    }

    private static void sleep(int ms){
        try{
            Thread.sleep(ms);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void run_dht_simulator(){
        DHTSimulator simulator = new DHTSimulator();
        simulator.run();
    }

    private static void run_messenger_simulator() throws Exception{
        // list of all messengers
        ArrayList<Messenger> messengers = new ArrayList<>();

        // the initial messenger that creates the community
        Messenger m = new Messenger();
        if (m.create("m0", 12345, 23456, new Messenger.MessengerListener() {
            @Override
            public void on_create_async_complete(boolean success, String reason) {

            }

            @Override
            public void on_join_async_complete(boolean success, String reason) {

            }

            @Override
            public void on_send_message_async_complete(boolean success, String reason, String id, String message) {

            }

            @Override
            public void on_received_message(String source_id, String message) {
                Log.v(m.get_string_id(), message);
            }
        })){
            Log.v("CREATED", "Created m0");
        };
        messengers.add(m);

        sleep(1000);

        // add a bunch more messengers
        for (int i = 1; i < 100; ++i){
            Messenger m1 = new Messenger();
            if (m1.join("m" + i, 12345 + i, 23456 + i, "127.0.0.1", 23456, new Messenger.MessengerListener() {
                @Override
                public void on_create_async_complete(boolean success, String reason) {

                }

                @Override
                public void on_join_async_complete(boolean success, String reason) {

                }

                @Override
                public void on_send_message_async_complete(boolean success, String reason, String id, String message) {

                }

                @Override
                public void on_received_message(String source_id, String message) {
                    Log.v(m1.get_string_id(), message);
                }
            })){
                Log.v("JOIN", "join m" + i);
            }
            messengers.add(m1);
        }


        sleep(5000);

        // send a bunch of random messages
        Random r = new Random();
        int unsent = 0;
        for (int i = 0; i < 5000; ++i){
            int m_a = r.nextInt(messengers.size());
            int m_b = r.nextInt(messengers.size());
            if (messengers.get(m_a).send_message(messengers.get(m_b).get_string_id(), "message from " + messengers.get(m_a).get_string_id() +
                    " to " + messengers.get(m_b).get_string_id()) == false) {
                Log.v("ERROR", "Unable to send: " + messengers.get(m_a).get_string_id() + " to " +
                        messengers.get(m_b).get_string_id());
                unsent += 1;
            }
            Log.v("SENT", Integer.toString(i));
            sleep(100);
        }

        Log.v("UNSENT: ", Integer.toString(unsent));

        while (true){
            sleep(100);
        }
    }

    private static void run_desktop_application(){
        Application.launch(DesktopApplication.class);
    }
}

package djs.dht_messenger.simulator;

import djs.dht_messenger.desktop.Log;
import djs.simpledht.DHT;
import djs.simpledht.DHTTomP2P;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by djsteffey on 3/23/2017.
 */
public class DHTSimulatorCommandJoinRange implements DHTSimulatorCommand{
    @Override
    public DHT execute(DHTSimulator simulator, String[] tokens, Map<String, DHT> nodes) {
        if (tokens.length < 7){
            Log.v("JOINRANGE", "Not enough arguments:  joinrange <node_id_base> <start_id> <quantity> <local_port_base> <bootstrap_host> <bootstrap_port>");
            return null;
        }

        // extract parameters
        String base_id = tokens[1];
        int start_id = Integer.parseInt(tokens[2]);
        int quantity = Integer.parseInt(tokens[3]);
        int local_port_base = Integer.parseInt(tokens[4]);
        String bootstrap_host = tokens[5];
        int bootstrap_port = Integer.parseInt(tokens[6]);


        for (int i = 0; i < quantity; ++i) {
            // create a new node
            DHT dht = new DHTTomP2P(new DHT.DHTListener() {
                @Override
                public void on_dht_create_async_complete(boolean success, String message) {

                }

                @Override
                public void on_dht_join_async_complete(boolean success, String message) {

                }

                @Override
                public void on_dht_put_async_complete(String key, boolean success, String message) {

                }

                @Override
                public void on_dht_get_async_complete(String key, Serializable object, String message) {

                }
            });
            if (dht.join(base_id + Integer.toString(start_id + i), local_port_base + i, bootstrap_host, bootstrap_port)) {
                Log.v("JOIN", "Join successful: " + dht.toString());
                nodes.put(dht.get_string_id(), dht);
            } else {
                Log.v("JOIN", "Join failed: " + dht.get_string_id());
            }
        }

        return null;
    }

    @Override
    public String get_command() {
        return "joinrange";
    }
}

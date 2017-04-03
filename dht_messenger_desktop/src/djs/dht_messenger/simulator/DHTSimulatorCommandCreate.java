package djs.dht_messenger.simulator;

import djs.dht_messenger.desktop.Log;
import djs.simpledht.DHT;
import djs.simpledht.DHTTomP2P;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by djsteffey on 3/23/2017.
 */
public class DHTSimulatorCommandCreate implements DHTSimulatorCommand {
    @Override
    public DHT execute(DHTSimulator simulator, String[] tokens, Map<String, DHT> nodes) {
        if (tokens.length < 3){
            Log.v("CREATE", "Not enough arguments:  create <node_id> <local_port>");
            return null;
        }

        // extract parameters
        String id = tokens[1];
        int local_port = Integer.parseInt(tokens[2]);

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
        if (dht.create(id, local_port)){
            Log.v("CREATE", "Create successful: " + dht.toString());
            nodes.put(id, dht);
            return dht;
        }else{
            Log.v("CREATE", "Create failed");
            return null;
        }
    }

    @Override
    public String get_command() {
        return "create";
    }
}

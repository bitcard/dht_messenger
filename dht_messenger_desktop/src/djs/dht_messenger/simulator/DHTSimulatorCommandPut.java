package djs.dht_messenger.simulator;

import djs.dht_messenger.desktop.Log;
import djs.simpledht.DHT;

import java.util.Map;

/**
 * Created by djsteffey on 3/23/2017.
 */
public class DHTSimulatorCommandPut implements DHTSimulatorCommand {
    @Override
    public DHT execute(DHTSimulator simulator, String[] tokens, Map<String, DHT> nodes) {
        if (tokens.length < 4){
            Log.v("PUT", "Not enough arguments:  put <node_id> <string_id> <string_data>");
            return null;
        }

        String id = tokens[1];
        String data_id = tokens[2];
        String data = tokens[3];

        if (nodes.containsKey(id) == false){
            Log.v("PUT", "Unknown node id");
            return null;
        }

        DHT node = nodes.get(id);
        if (node.put(data_id, data)){
            Log.v("PUT", "Put successful");
            return null;
        } else{
            Log.v("PUT", "Put failed");
            return null;
        }
    }

    @Override
    public String get_command() {
        return "put";
    }
}

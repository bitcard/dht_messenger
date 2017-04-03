package djs.dht_messenger.simulator;

import djs.dht_messenger.desktop.Log;
import djs.simpledht.DHT;

import java.util.Map;

/**
 * Created by djsteffey on 3/23/2017.
 */
public class DHTSimulatorCommandGet implements DHTSimulatorCommand {
    @Override
    public DHT execute(DHTSimulator simulator, String[] tokens, Map<String, DHT> nodes) {
        if (tokens.length < 3){
            Log.v("GET", "Not enough arguments:  get <node_id> <string_id>");
            return null;
        }

        String id = tokens[1];
        String data_id = tokens[2];

        if (nodes.containsKey(id) == false){
            Log.v("GET", "Unknown node id");
            return null;
        }

        DHT node = nodes.get(id);
        String data = (String)node.get(data_id);
        if (data != null){
            Log.v("GET", "Get successful: " + data);
            return null;
        } else{
            Log.v("GET", "Get failed");
            return null;
        }
    }

    @Override
    public String get_command() {
        return "get";
    }
}

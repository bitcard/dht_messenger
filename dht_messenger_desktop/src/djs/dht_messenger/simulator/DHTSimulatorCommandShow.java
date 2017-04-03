package djs.dht_messenger.simulator;

import djs.dht_messenger.desktop.Log;
import djs.simpledht.DHT;

import java.util.Map;

/**
 * Created by djsteffey on 3/23/2017.
 */
public class DHTSimulatorCommandShow implements DHTSimulatorCommand{
    @Override
    public DHT execute(DHTSimulator simulator, String[] tokens, Map<String, DHT> nodes) {
        if (tokens.length < 2){
            Log.v("SHOW", "Not enough arguments:  show <node_id>");
            return null;
        }

        // the id
        String id = tokens[1];
        // get the specific node
        if (nodes.containsKey(id) == false){
            Log.v("SHOW", "Unknown node id");
            return null;
        }

        DHT node = nodes.get(id);

        return null;
    }

    @Override
    public String get_command() {
        return "show";
    }
}

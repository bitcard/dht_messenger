package djs.dht_messenger.simulator;

import djs.dht_messenger.desktop.Log;
import djs.simpledht.DHT;

import java.util.Map;

/**
 * Created by djsteffey on 3/23/2017.
 */
public class DHTSimulatorCommandList implements DHTSimulatorCommand{
    @Override
    public DHT execute(DHTSimulator simulator, String[] tokens, Map<String, DHT> nodes) {
        if (tokens.length < 1){
            Log.v("LIST", "Not enough arguments:  list");
            return null;
        }

        for (Map.Entry<String, DHT> node : nodes.entrySet()){
            Log.v("LIST", node.toString());
        }

        return null;
    }

    @Override
    public String get_command() {
        return "list";
    }
}

package djs.dht_messenger.simulator;

import djs.simpledht.DHT;
import java.util.Map;

/**
 * Created by djsteffey on 3/23/2017.
 */
public interface DHTSimulatorCommand {
    DHT execute(DHTSimulator simulator, String[] tokens, Map<String, DHT> nodes);
    String get_command();
}

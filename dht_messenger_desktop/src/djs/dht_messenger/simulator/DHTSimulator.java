package djs.dht_messenger.simulator;

import djs.dht_messenger.desktop.Log;
import djs.simpledht.DHT;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by djsteffey on 3/23/2017.
 */
public class DHTSimulator {

    // variables
    private Map<String, DHT> m_nodes;
    private Map<String, DHTSimulatorCommand> m_commands;

    // functions
    public DHTSimulator(){
        this.m_nodes = null;
        this.m_commands = null;
    }

    public void run(){
        // create map of nodes
        this.m_nodes = new HashMap<>();

        // create our supported commands
        this.m_commands = new HashMap<>();
        DHTSimulatorCommand cmd = new DHTSimulatorCommandCreate();
        this.m_commands.put(cmd.get_command(), cmd);
        cmd = new DHTSimulatorCommandJoin();
        this.m_commands.put(cmd.get_command(), cmd);
        cmd = new DHTSimulatorCommandList();
        this.m_commands.put(cmd.get_command(), cmd);
        cmd = new DHTSimulatorCommandPut();
        this.m_commands.put(cmd.get_command(), cmd);
        cmd = new DHTSimulatorCommandGet();
        this.m_commands.put(cmd.get_command(), cmd);
        cmd = new DHTSimulatorCommandJoinRange();
        this.m_commands.put(cmd.get_command(), cmd);

        // read from keyboard
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // loop forever...until we return
        while (true){

            // get a command
            Log.v("Enter Command", "");

            String line = null;
            try {
                line = reader.readLine();
            }catch (Exception e){
                // print the stack trace and then go back to continue the while loop
                e.printStackTrace();
                continue;
            }

            // execute the command
            this.execute_command(line);
        }
    }

    private void execute_command(String line){
        // break line up into space separated values
        String[] tokens = line.split(" ");

        if (this.m_commands.containsKey(tokens[0])){
            this.m_commands.get(tokens[0]).execute(this, tokens, this.m_nodes);
        }else {
            Log.v("CMD", "Unknown command");
        }
    }
}

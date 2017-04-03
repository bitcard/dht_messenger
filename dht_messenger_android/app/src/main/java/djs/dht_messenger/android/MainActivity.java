package djs.dht_messenger.android;


import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import djs.dht_messenger_android.R;

public class MainActivity extends Activity implements DhtMessengerService.Listener{

    // constants
    private static final int MAX_DISPLAYED_MESSAGE_LINES = 50;

    // variables
    private TextView m_textview_myid;
    private TextView m_textview_messages;
    private EditText m_edittext_id;
    private EditText m_edittext_message;
    private Button m_button_send;
    private Button m_button_disconnect;

    // functions
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // prevent keyboard from open on start
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // get handles to the views and buttons
        this.m_textview_myid = (TextView)(this.findViewById(R.id.main_textview_myid));
        this.m_textview_messages = (TextView)(this.findViewById(R.id.main_textview_messages));
        ViewGroup.LayoutParams p = this.m_textview_messages.getLayoutParams();
        p.height = this.getWindowManager().getDefaultDisplay().getHeight() - 600;
        this.m_textview_messages.setLayoutParams(p);
        this.m_edittext_id = (EditText)(this.findViewById(R.id.main_edittext_id));
        this.m_edittext_message = (EditText)(this.findViewById(R.id.main_edittext_message));
        this.m_button_send = (Button)(this.findViewById(R.id.main_button_send));
        this.m_button_disconnect = (Button)(this.findViewById(R.id.main_button_disconnect));

        // set the my id and connection info
        this.m_textview_myid.setText("My ID: " + DhtMessengerService.get_messenger().get_string_id());
        this.m_textview_myid.append("\t\t" + DhtMessengerService.get_messenger().get_ip_string() + ":" + DhtMessengerService.get_messenger().get_dht_port());

        // setup scrollbars
        this.m_textview_messages.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void onResume(){
        super.onResume();

        // clear out the messages
        this.m_textview_messages.setText("");

        // get all the stored messages and put in the textview
        for (String[] s : DhtMessengerService.get_stored_messages()){
            if (s[2].equals("s")){
                this.add_message("To " + s[0] + ": " + s[1]);
            } else{
                this.add_message("From " + s[0] + ": " + s[1]);
            }
        }

        // set us up as a listener
        DhtMessengerService.set_listener(this);
    }

    @Override
    public void onPause(){
        super.onPause();

        // clear out the listener
        DhtMessengerService.set_listener(null);
    }

    public void main_button_send_on_click(View v){
        // disable the id, message, and send
        this.m_edittext_id.setEnabled(false);
        this.m_edittext_message.setEnabled(false);
        this.m_button_send.setEnabled(false);
        this.m_button_disconnect.setEnabled(false);

        // extract the id and message
        String id = this.m_edittext_id.getText().toString();
        String message = this.m_edittext_message.getText().toString();

        // ensure valid
        if (id.equals("")){
            Toast.makeText(this.getApplicationContext(), "Must set a valid ID", Toast.LENGTH_LONG).show();
            this.m_edittext_id.setEnabled(true);
            this.m_edittext_message.setEnabled(true);
            this.m_button_send.setEnabled(true);
            this.m_button_disconnect.setEnabled(true);
            return;
        }
        if (message.equals("")){
            Toast.makeText(this.getApplicationContext(), "Must set a valid message", Toast.LENGTH_LONG).show();
            this.m_edittext_id.setEnabled(true);
            this.m_edittext_message.setEnabled(true);
            this.m_button_send.setEnabled(true);
            this.m_button_disconnect.setEnabled(true);
            return;
        }

        // send away
        DhtMessengerService.send(id, message);
    }

    public void main_button_disconnect_on_click(View v){
        // disable ui elements
        this.m_edittext_id.setEnabled(false);
        this.m_edittext_message.setEnabled(false);
        this.m_button_send.setEnabled(false);
        this.m_button_disconnect.setEnabled(false);

        // tell the service to disconnect
        DhtMessengerService.disconnect();
    }

    public void add_message(String message){
        String s = this.m_textview_messages.getText().toString();
        if (this.m_textview_messages.getLineCount() > MAX_DISPLAYED_MESSAGE_LINES){
            // getting too long
            int i = this.m_textview_messages.getLayout().getLineEnd(0);
            s = s.substring(i);
            this.m_textview_messages.setText(s);
        }
        this.m_textview_messages.append("\n");
        this.m_textview_messages.append(message);
    }

    @Override
    public void on_create_complete(boolean success) {
        // not used here
    }

    @Override
    public void on_join_complete(boolean success) {
        // not used here
    }

    @Override
    public void on_send_complete(final boolean success, final String id, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (success){
                    MainActivity.this.add_message("To " + id + ": " + message);
                } else{
                    MainActivity.this.add_message("***FAILED*** To " + id + ": " + message);
                }
                MainActivity.this.m_edittext_id.setEnabled(true);
                MainActivity.this.m_edittext_message.setEnabled(true);
                MainActivity.this.m_button_send.setEnabled(true);
                MainActivity.this.m_edittext_message.setText("");
                MainActivity.this.m_button_disconnect.setEnabled(true);
            }
        });
    }

    @Override
    public void on_receive_complete(boolean success, final String id, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.add_message("From " + id + ": " + message);
            }
        });
    }

    @Override
    public void on_disconnect_complete() {
        this.finish();
    }
}

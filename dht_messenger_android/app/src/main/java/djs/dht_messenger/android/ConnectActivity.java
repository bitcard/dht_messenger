package djs.dht_messenger.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import djs.dht_messenger_android.R;

public class ConnectActivity extends Activity {

    // variables
    private EditText m_edittext_id;
    private RadioButton m_radiobutton_create;
    private RadioButton m_radiobutton_join;
    private EditText m_edittext_bootstrap_host;
    private EditText m_edittext_bootstrap_port;
    private Button m_button_connect;

    // functions
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        // prefer IPv4
        System.setProperty("java.net.preferIPv6Addresses", "false");

        // first just check if already connected
        if (DhtMessengerService.get_is_connected()){
            // already connected so proceed to main activity and close this one
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
            this.finish();
            return;
        }

        // now see if already running
        if (!DhtMessengerService.get_is_running()){
            // not yet running so start it
            Intent intent = new Intent(this, DhtMessengerService.class);
            this.startService(intent);
        }

        // prevent keyboard from open on start
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // get all the views and buttons
        this.m_edittext_id = (EditText)(this.findViewById(R.id.connect_edittext_id));
        RadioGroup radio_group = (RadioGroup)(this.findViewById(R.id.connect_radiogroup_createjoin));
        this.m_radiobutton_create = (RadioButton)(this.findViewById(R.id.connect_radiobutton_create));
        this.m_radiobutton_join = (RadioButton)(this.findViewById(R.id.connect_radiobutton_join));
        this.m_edittext_bootstrap_host = (EditText)(this.findViewById(R.id.connect_edittext_bootstrap_host));
        this.m_edittext_bootstrap_port = (EditText)(this.findViewById(R.id.connect_exittext_bootstrap_port));
        this.m_button_connect = (Button)(this.findViewById(R.id.connect_button_connect));

        // default disable connect button, bootstrap host, bootstrap port
        this.m_edittext_bootstrap_host.setEnabled(false);
        this.m_edittext_bootstrap_port.setEnabled(false);
        this.m_button_connect.setEnabled(false);

        // set some callbacks
        // input id
        this.m_edittext_id.setOnEditorActionListener( new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    // finished typing
                    ConnectActivity.this.calculate_ui_enable();
                    return false;
                }
                return false;
            }
        });
        this.m_edittext_id.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(!hasFocus)
                {
                    ConnectActivity.this.calculate_ui_enable();
                }
            }
        });
        // change radio button
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                ConnectActivity.this.calculate_ui_enable();
            }
        });
        // input on host
        this.m_edittext_bootstrap_host.setOnEditorActionListener( new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    // finished typing
                    ConnectActivity.this.calculate_ui_enable();
                    return false;
                }
                return false;
            }
        });
        this.m_edittext_bootstrap_host.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(!hasFocus)
                {
                    ConnectActivity.this.calculate_ui_enable();
                }
            }
        });
        // input on port
        this.m_edittext_bootstrap_port.setOnEditorActionListener( new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE){
                        // finished typing
                        ConnectActivity.this.calculate_ui_enable();
                        return false;
                    }
                    return false;
            }
        });
        this.m_edittext_bootstrap_port.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(!hasFocus)
                {
                    ConnectActivity.this.calculate_ui_enable();
                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public void connect_button_connect_on_click(View v){
        // disable ui
        this.m_edittext_id.setEnabled(false);
        this.m_radiobutton_create.setEnabled(false);
        this.m_radiobutton_join.setEnabled(false);
        this.m_edittext_bootstrap_host.setEnabled(false);
        this.m_edittext_bootstrap_port.setEnabled(false);
        this.m_button_connect.setEnabled(false);

        // extract the id
        String id = this.m_edittext_id.getText().toString();

        // determine if create or join
        if (this.m_radiobutton_create.isChecked()){
            // we are creating
            // we have some work to do so set a listener
            DhtMessengerService.set_listener(new DhtMessengerService.Listener() {
                @Override
                public void on_create_complete(boolean success) {
                    if (success){
                        // start main activity and close this one
                        Intent intent = new Intent(ConnectActivity.this, MainActivity.class);
                        ConnectActivity.this.startActivity(intent);
                        ConnectActivity.this.finish();
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ConnectActivity.this.getApplicationContext(), "Unable to create messenger", Toast.LENGTH_LONG).show();
                                ConnectActivity.this.calculate_ui_enable();
                            }
                        });
                    }
                }

                @Override
                public void on_join_complete(boolean success) {
                    // unused here
                }

                @Override
                public void on_send_complete(boolean success, String id, String message) {
                    // unused here
                }

                @Override
                public void on_receive_complete(boolean success, String id, String message) {
                    // unused here
                }

                @Override
                public void on_disconnect_complete() {
                    // unused here
                }
            });
            DhtMessengerService.create_connection(id);
        } else {
            // we are joining
            // get host and port for bootstrap
            String bootstrap_host = this.m_edittext_bootstrap_host.getText().toString();
            int bootstrap_port = Integer.parseInt(this.m_edittext_bootstrap_port.getText().toString());
            if ((bootstrap_port < 1024) || (bootstrap_port > 64024)){
                // invalid port range
                Toast.makeText(ConnectActivity.this.getApplicationContext(), "Bootstrap port must be from 1024 to 64024", Toast.LENGTH_LONG).show();
                ConnectActivity.this.calculate_ui_enable();
                return;
            }
            // set a listener
            DhtMessengerService.set_listener(new DhtMessengerService.Listener() {
                @Override
                public void on_create_complete(boolean success) {
                    // unused here
                }

                @Override
                public void on_join_complete(boolean success) {
                    if (success){
                        // start main activity and close this one
                        Intent intent = new Intent(ConnectActivity.this, MainActivity.class);
                        ConnectActivity.this.startActivity(intent);
                        ConnectActivity.this.finish();
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ConnectActivity.this.getApplicationContext(), "Unable to join messenger", Toast.LENGTH_LONG).show();
                                ConnectActivity.this.calculate_ui_enable();
                            }
                        });
                    }
                }

                @Override
                public void on_send_complete(boolean success, String id, String message) {
                    // unused here
                }

                @Override
                public void on_receive_complete(boolean success, String id, String message) {
                    // unused here
                }

                @Override
                public void on_disconnect_complete() {
                    // unused here
                }
            });
            DhtMessengerService.join_connection(id, bootstrap_host, bootstrap_port);
        }
    }

    private void calculate_ui_enable(){
        // radio buttons always on
        this.m_radiobutton_create.setEnabled(true);
        this.m_radiobutton_join.setEnabled(true);
        // id always available
        this.m_edittext_id.setEnabled(true);

        if (this.m_radiobutton_create.isChecked()){
            // create a messenger
            // check id for connect button enable
            if (this.m_edittext_id.getText().toString().equals("")){
                this.m_button_connect.setEnabled(false);
            }
            else{
                this.m_button_connect.setEnabled(true);
            }

            // ensure host/port disabled
            this.m_edittext_bootstrap_host.setEnabled(false);
            this.m_edittext_bootstrap_port.setEnabled(false);
        }
        else {
            // join a messenger
            // ensure host/port enabled
            this.m_edittext_bootstrap_host.setEnabled(true);
            this.m_edittext_bootstrap_port.setEnabled(true);

            // check for connect button
            if ((this.m_edittext_id.getText().toString().equals("")) ||
                (this.m_edittext_bootstrap_host.getText().toString().equals("")) ||
                (this.m_edittext_bootstrap_port.getText().toString().equals(""))){
                // one of the fields is empty to disable connect
                this.m_button_connect.setEnabled(false);
            } else{
                this.m_button_connect.setEnabled(true);
            }
        }
    }
}

package djs.dht_messenger.desktop;

import djs.dht_messenger.Messenger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.util.Random;

/**
 * Created by djsteffey on 3/28/2017.
 */
public class DesktopApplication extends Application implements Messenger.MessengerListener {

    // variables
    private Messenger m_messenger;
    // setup stage
    private Stage m_setup_stage;
    private TextField m_setup_textfield_id;
    private RadioButton m_setup_radiobutton_create;
    private RadioButton m_setup_radiobutton_join;
    private TextField m_setup_textfield_bootstrap_host;
    private TextField m_setup_textfield_bootstrap_port;
    private Button m_setup_button_connect;

    // main stage
    private Stage m_main_stage;
    private TextArea m_main_textarea_messages;
    private TextField m_main_textfield_id;
    private TextField m_main_textfield_message;
    private Button m_main_button_send;
    private Label m_main_label_connection_info;


    @Override
    public void start(Stage primaryStage) throws Exception {
        // start the main stage
        this.construct_main_stage();
        this.m_main_stage.show();

        // but have the setup stage modal on startup
        this.construct_setup_stage();
        this.m_setup_stage.initOwner(this.m_main_stage);
        this.m_setup_stage.initModality(Modality.APPLICATION_MODAL);
        this.m_setup_stage.showAndWait();
    }

    private void construct_main_stage(){
        // stage
        this.m_main_stage = new Stage();
        this.m_main_stage.setTitle("ID: ");

        // group
        Group group = new Group();

        // scene
        Scene scene = new Scene(group);
        this.m_main_stage.setScene(scene);

        // layout boxes
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.setSpacing(10);
        HBox hbox1 = new HBox();
        HBox hbox2 = new HBox();
        HBox hbox3 = new HBox();
        hbox1.setSpacing(10);
        hbox1.setAlignment(Pos.CENTER_LEFT);
        hbox2.setSpacing(10);
        hbox2.setAlignment(Pos.CENTER_LEFT);
        hbox3.setSpacing(10);
        hbox3.setAlignment(Pos.CENTER_LEFT);
        vbox.getChildren().addAll(hbox1, hbox2, hbox3);
        group.getChildren().add(vbox);

        // message display area
        this.m_main_textarea_messages = new TextArea();
        this.m_main_textarea_messages.setId("main_textarea_messages");
        this.m_main_textarea_messages.setMinSize(768, 500);
        this.m_main_textarea_messages.setEditable(false);
        hbox1.getChildren().add(this.m_main_textarea_messages);

        // id input
        Label label = new Label("ID");
        hbox2.getChildren().add(label);
        this.m_main_textfield_id = new TextField();
        this.m_main_textfield_id.setId("main_textfield_id");
        this.m_main_textfield_id.setPrefWidth(120);
        hbox2.getChildren().add(this.m_main_textfield_id);

        // message input
        label = new Label("Message");
        hbox2.getChildren().add(label);
        this.m_main_textfield_message = new TextField();
        this.m_main_textfield_message.setId("main_textfield_message_input");
        this.m_main_textfield_message.setPrefWidth(500);
        this.m_main_textfield_message.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ENTER){
                this.main_on_button_click_send(null);
            }
        });
        hbox2.getChildren().add(this.m_main_textfield_message);

        // send button
        this.m_main_button_send = new Button("Send");
        this.m_main_button_send.setId("main_button_send");
        hbox2.getChildren().add(this.m_main_button_send);
        this.m_main_button_send.setOnAction(this::main_on_button_click_send);

        // status like label...showing my ip and port...if others need to join me
        this.m_main_label_connection_info = new Label("Connection Info: ");
        this.m_main_label_connection_info.setId("main_label_connection_info");
        hbox3.getChildren().add(this.m_main_label_connection_info);

        // create the stage
        this.m_main_stage.sizeToScene();
        this.m_main_stage.setResizable(false);
        this.m_main_stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                // the 'X' was clicked to close the setup window
                // just exit the whole application
                if (DesktopApplication.this.m_messenger != null){
                    DesktopApplication.this.m_messenger.shutdown();
                }
                Platform.exit();
            }
        });
    }

    private void construct_setup_stage(){
        // stage
        this.m_setup_stage = new Stage();
        this.m_setup_stage.setTitle("Setup");

        // group
        Group group = new Group();

        // scene
        Scene scene = new Scene(group);
        this.m_setup_stage.setScene(scene);

        // layout boxes
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.setSpacing(10);
        HBox hbox1 = new HBox();
        HBox hbox2 = new HBox();
        HBox hbox3 = new HBox();
        HBox hbox4 = new HBox();
        HBox hbox5 = new HBox();
        hbox1.setSpacing(10);
        hbox1.setAlignment(Pos.CENTER_LEFT);
        hbox2.setSpacing(10);
        hbox2.setAlignment(Pos.CENTER_LEFT);
        hbox3.setSpacing(10);
        hbox3.setAlignment(Pos.CENTER_LEFT);
        hbox4.setSpacing(10);
        hbox4.setAlignment(Pos.CENTER_LEFT);
        hbox5.setSpacing(10);
        hbox5.setAlignment(Pos.CENTER_LEFT);
        vbox.getChildren().addAll(hbox1, hbox2, hbox3, hbox4, hbox5);
        group.getChildren().add(vbox);

        // id
        Label label_id = new Label("ID");
        this.m_setup_textfield_id = new TextField();
        this.m_setup_textfield_id.setId("setup_textfield_id");
        hbox1.getChildren().addAll(label_id, this.m_setup_textfield_id);

        // radio button for create
        this.m_setup_radiobutton_create = new RadioButton("Create");
        this.m_setup_radiobutton_create.setId("setup_radiobutton_create");
        hbox2.getChildren().add(this.m_setup_radiobutton_create);

        // radio button for join
        this.m_setup_radiobutton_join = new RadioButton("Join");
        this.m_setup_radiobutton_join.setId("setup_radiobutton_join");
        hbox2.getChildren().add(this.m_setup_radiobutton_join);

        // combine them into a toggle group
        ToggleGroup togglegroup_radiobuttons = new ToggleGroup();
        this.m_setup_radiobutton_create.setToggleGroup(togglegroup_radiobuttons);
        this.m_setup_radiobutton_join.setToggleGroup(togglegroup_radiobuttons);

        // bootstrap host
        Label label_bootstrap_host = new Label("Bootstrap Host");
        this.m_setup_textfield_bootstrap_host = new TextField();
        this.m_setup_textfield_bootstrap_host.setId("setup_textfield_bootstrap_host");
        hbox3.getChildren().addAll(label_bootstrap_host, this.m_setup_textfield_bootstrap_host);

        // bootstrap port
        Label label_bootstrap_port = new Label("Bootstrap Port");
        this.m_setup_textfield_bootstrap_port = new TextField();
        this.m_setup_textfield_bootstrap_port.setId("setup_textfield_bootstrap_port");
        hbox4.getChildren().addAll(label_bootstrap_port, this.m_setup_textfield_bootstrap_port);

        // Go button
        this.m_setup_button_connect = new Button("Connect");
        this.m_setup_button_connect.setId("setup_button_connect");
        this.m_setup_button_connect.setOnAction(this::setup_on_button_click_connect);
        hbox5.getChildren().add(this.m_setup_button_connect);

        // radio button toggling
        togglegroup_radiobuttons.selectedToggleProperty().addListener(
                (ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> {
                    if (togglegroup_radiobuttons.getSelectedToggle() == this.m_setup_radiobutton_create){
                        // create is selected so disable the bootstrap stuff
                        this.m_setup_textfield_bootstrap_host.setDisable(true);
                        this.m_setup_textfield_bootstrap_port.setDisable(true);
                    } else{
                        // join is selected so enable the bootstrap stuff
                        m_setup_textfield_bootstrap_host.setDisable(false);
                        m_setup_textfield_bootstrap_port.setDisable(false);
                    }
                }
        );
        this.m_setup_radiobutton_create.setSelected(true);

        // finish setting up the stage
        this.m_setup_stage.sizeToScene();
        this.m_setup_stage.setResizable(false);
        this.m_setup_stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                // the 'X' was clicked to close the setup window
                // just exit the whole application
                if (DesktopApplication.this.m_messenger != null){
                    DesktopApplication.this.m_messenger.shutdown();
                }
                Platform.exit();
            }
        });
    }

    // ui event callbacks
    private void setup_on_button_click_connect(ActionEvent event){
        // must be from the connect button of the setup stage

        // disable ui
        this.m_setup_textfield_id.setDisable(true);
        this.m_setup_radiobutton_create.setDisable(true);
        this.m_setup_radiobutton_join.setDisable(true);
        this.m_setup_textfield_bootstrap_host.setDisable(true);
        this.m_setup_textfield_bootstrap_port.setDisable(true);
        this.m_setup_button_connect.setDisable(true);

        // extract the variables from the ui
        String id = this.m_setup_textfield_id.getText();
        boolean create_dht = this.m_setup_radiobutton_create.isSelected();
        String bootstrap_host = this.m_setup_textfield_bootstrap_host.getText();
        int bootstrap_port = 0;
        if (create_dht == false) {
            try {
                bootstrap_port = Integer.parseInt(this.m_setup_textfield_bootstrap_port.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // sanity check values
        if (id.equals("")){
            this.display_error("Must enter a valid ID");
            this.m_setup_textfield_id.setDisable(false);
            this.m_setup_radiobutton_create.setDisable(false);
            this.m_setup_radiobutton_join.setDisable(false);
            this.m_setup_textfield_bootstrap_host.setDisable(false);
            this.m_setup_textfield_bootstrap_port.setDisable(false);
            this.m_setup_button_connect.setDisable(false);
            return;
        }
        if (create_dht == false){
            if (bootstrap_host.equals("")){
                this.display_error("Must enter a bootstrap host");
                this.m_setup_textfield_id.setDisable(false);
                this.m_setup_radiobutton_create.setDisable(false);
                this.m_setup_radiobutton_join.setDisable(false);
                this.m_setup_textfield_bootstrap_host.setDisable(false);
                this.m_setup_textfield_bootstrap_port.setDisable(false);
                this.m_setup_button_connect.setDisable(false);
                return;
            }
            if (bootstrap_port < 1024 || bootstrap_port > 64024){
                this.display_error("Must enter a bootstrap port (1024 - 64024)");
                this.m_setup_textfield_id.setDisable(false);
                this.m_setup_radiobutton_create.setDisable(false);
                this.m_setup_radiobutton_join.setDisable(false);
                this.m_setup_textfield_bootstrap_host.setDisable(false);
                this.m_setup_textfield_bootstrap_port.setDisable(false);
                this.m_setup_button_connect.setDisable(false);
                return;
            }
        }

        // random local ports for the messenger and dht
        Random random = new Random();
        int messenger_port = random.nextInt(63001) + 1024;
        int dht_port = random.nextInt(63001) + 1024;

        // create the messenger
        this.m_messenger = new Messenger();
        try {
            if (create_dht) {
                if (this.m_messenger.create(id, messenger_port, dht_port, this)) {
                    // success creating
                    // close the setup stage
                    this.m_setup_stage.close();
                    this.m_main_label_connection_info.setText("Connection Info: " + this.m_messenger.get_ip_string() + ":" + this.m_messenger.get_dht_port());
                    this.m_main_stage.setTitle("ID: " + this.m_messenger.get_string_id());
                } else {
                    // failed creating
                    this.display_error("Unable to create messenger");

                    // enable ui
                    this.m_setup_textfield_id.setDisable(false);
                    this.m_setup_radiobutton_create.setDisable(false);
                    this.m_setup_radiobutton_join.setDisable(false);
                    this.m_setup_textfield_bootstrap_host.setDisable(false);
                    this.m_setup_textfield_bootstrap_port.setDisable(false);
                    this.m_setup_button_connect.setDisable(false);
                }
            } else {
                if (this.m_messenger.join(id, messenger_port, dht_port, bootstrap_host, bootstrap_port, this)) {
                    // success joining
                    // close the setup stage
                    this.m_setup_stage.close();
                    this.m_main_label_connection_info.setText("Connection Info: " + this.m_messenger.get_ip_string() + ":" + this.m_messenger.get_dht_port());
                    this.m_main_stage.setTitle("ID: " + this.m_messenger.get_string_id());
                } else {
                    // failed joining
                    this.display_error("Unable to join messenger");

                    // enable ui
                    this.m_setup_textfield_id.setDisable(false);
                    this.m_setup_radiobutton_create.setDisable(false);
                    this.m_setup_radiobutton_join.setDisable(false);
                    this.m_setup_textfield_bootstrap_host.setDisable(false);
                    this.m_setup_textfield_bootstrap_port.setDisable(false);
                    this.m_setup_button_connect.setDisable(false);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
            this.display_error(e.toString());

            // enable ui
            this.m_setup_textfield_id.setDisable(false);
            this.m_setup_radiobutton_create.setDisable(false);
            this.m_setup_radiobutton_join.setDisable(false);
            this.m_setup_textfield_bootstrap_host.setDisable(false);
            this.m_setup_textfield_bootstrap_port.setDisable(false);
            this.m_setup_button_connect.setDisable(false);
        }
    }

    private void main_on_button_click_send(ActionEvent event){
        // try to send the message
        // disable ui stuff
        DesktopApplication.this.m_main_textfield_id.setDisable(true);
        DesktopApplication.this.m_main_textfield_message.setDisable(true);
        DesktopApplication.this.m_main_button_send.setDisable(true);

        // get the id
        String id = DesktopApplication.this.m_main_textfield_id.getText();
        String message = DesktopApplication.this.m_main_textfield_message.getText();
        if (id.equals("")){
            DesktopApplication.this.display_error("No ID is entered");
            DesktopApplication.this.m_main_textfield_id.setDisable(false);
            DesktopApplication.this.m_main_textfield_message.setDisable(false);
            DesktopApplication.this.m_main_button_send.setDisable(false);
            return;
        }
        if (message.equals("")){
            DesktopApplication.this.display_error("No message is entered");
            DesktopApplication.this.m_main_textfield_id.setDisable(false);
            DesktopApplication.this.m_main_textfield_message.setDisable(false);
            DesktopApplication.this.m_main_button_send.setDisable(false);
            return;
        }

        // send async
        DesktopApplication.this.m_messenger.send_message_async(id, message);
    }

    private void display_error(String message){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error");
                alert.setContentText(message);
                alert.showAndWait();
            }
        });
    }

    private void add_message_main_textarea(String message){
        // append the text
        this.m_main_textarea_messages.appendText("\n" + message);
        // scroll down to bottom
        this.m_main_textarea_messages.setScrollTop(Double.MAX_VALUE);
    }

    // messenger callbacks
    @Override
    public void on_create_async_complete(boolean success, String reason) {

    }

    @Override
    public void on_join_async_complete(boolean success, String reason) {

    }

    @Override
    public void on_send_message_async_complete(boolean success, String reason, String id, String message) {
        if (success) {
            DesktopApplication.this.m_main_textfield_message.setText("");
            DesktopApplication.this.add_message_main_textarea("To " + id + ": " + message);
            DesktopApplication.this.m_main_textfield_id.setDisable(false);
            DesktopApplication.this.m_main_textfield_message.setDisable(false);
            DesktopApplication.this.m_main_button_send.setDisable(false);

        } else{
            DesktopApplication.this.display_error("Unable to send message to " + id + ". " + reason);
            DesktopApplication.this.m_main_textfield_id.setDisable(false);
            DesktopApplication.this.m_main_textfield_message.setDisable(false);
            DesktopApplication.this.m_main_button_send.setDisable(false);
        }

        // either way give focus back to the message input
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                DesktopApplication.this.m_main_textfield_message.requestFocus();
            }
        });

    }

    @Override
    public void on_received_message(String source_id, String message) {
        this.add_message_main_textarea("From " + source_id + ": " + message);
    }
}

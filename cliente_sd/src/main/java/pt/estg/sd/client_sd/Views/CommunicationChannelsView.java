package pt.estg.sd.client_sd.Views;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pt.estg.sd.client_sd.client.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents the view for displaying and handling communication channels.
 */
public class CommunicationChannelsView {

    private ListView<String> channelList;
    private Client client;
    private int userId;
    private List<Map<String, Object>> channels;
    private List<Map<String, Object>> users;

    /**
     * Constructs a CommunicationChannelsView with the specified user ID, channels, client, and users.
     *
     * @param userId the user ID
     * @param channels the list of channels
     * @param client the client instance
     * @param users the list of users
     */
    public CommunicationChannelsView(int userId, List<Map<String, Object>> channels, Client client, List<Map<String, Object>> users) {
        this.channels = channels;
        this.userId = userId;
        this.client = client;
        this.users = users;
    }

    /**
     * Returns the view node for displaying the communication channels.
     *
     * @return the view node
     */
    public VBox getView() {
        channelList = new ListView<>();
        for (Map<String, Object> channel : channels) {
            channelList.getItems().add((String) channel.get("name"));
        }

        Button enterChatButton = new Button("Enter Chat");
        enterChatButton.setOnAction(e -> {
            String selectedChannel = channelList.getSelectionModel().getSelectedItem();
            if (selectedChannel != null) {
                client.sendMessage("/ENTER_CHAT;" + selectedChannel + ";" + userId);
            }
        });

        Button createGroupButton = new Button("Create Group");
        createGroupButton.setOnAction(e -> showCreateGroupForm());

        Button deleteChannelButton = new Button("Delete Channel");
        deleteChannelButton.setOnAction(e -> {
            String selectedChannel = channelList.getSelectionModel().getSelectedItem();
            if (selectedChannel != null) {
                client.sendMessage("/DELETE_CHANNEL;" + selectedChannel + ";" + userId);
            }
        });

        VBox channelBox = new VBox(10);
        channelBox.setAlignment(Pos.CENTER);
        channelBox.getChildren().addAll(new Label("Canais de Comunicação"), channelList, enterChatButton, createGroupButton, deleteChannelButton);

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().add(channelBox);

        return layout;
    }

    /**
     * Shows the form for creating a new group.
     */
    private void showCreateGroupForm() {
        TextField groupNameField = new TextField();
        groupNameField.setPromptText("Group Name");

        TextField groupDescriptionField = new TextField();
        groupDescriptionField.setPromptText("Group Description");

        ListView<CheckBox> userListView = new ListView<>();
        for (Map<String, Object> user : users) {
            if ((int) user.get("id") != userId) {
                CheckBox checkBox = new CheckBox((String) user.get("username"));
                checkBox.setUserData(user);
                userListView.getItems().add(checkBox);
            }
        }

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            List<Integer> selectedUserIds = new ArrayList<>();
            selectedUserIds.add(userId);
            for (CheckBox checkBox : userListView.getItems()) {
                if (checkBox.isSelected()) {
                    selectedUserIds.add((Integer) ((Map<String, Object>) checkBox.getUserData()).get("id"));
                }
            }
            String groupName = groupNameField.getText();
            String groupDescription = groupDescriptionField.getText();
            client.sendMessage(String.format("/CREATE_GROUP;%s;%s;%s;%d", groupName, groupDescription, selectedUserIds.toString(), userId));
        });

        VBox createGroupBox = new VBox(10);
        createGroupBox.setAlignment(Pos.CENTER);
        createGroupBox.getChildren().addAll(new Label("Create New Group"), groupNameField, groupDescriptionField, userListView, createButton);

        VBox layout = (VBox) channelList.getParent().getParent();
        layout.getChildren().setAll(createGroupBox);
    }
}
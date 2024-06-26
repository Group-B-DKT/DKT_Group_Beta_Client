package com.example.dkt_group_beta.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.dkt_group_beta.activities.interfaces.GameSearchAction;
import com.example.dkt_group_beta.communication.controller.ActionController;
import com.example.dkt_group_beta.communication.controller.ConnectController;
import com.example.dkt_group_beta.communication.controller.InfoController;
import com.example.dkt_group_beta.communication.controller.WebsocketClientController;
import com.example.dkt_group_beta.communication.enums.Action;
import com.example.dkt_group_beta.communication.enums.ConnectType;
import com.example.dkt_group_beta.communication.enums.Info;
import com.example.dkt_group_beta.model.Field;
import com.example.dkt_group_beta.model.GameInfo;
import com.example.dkt_group_beta.model.Player;

import java.util.List;

public class GameSearchViewModel extends ViewModel {
    private String username;
    private ConnectController connectController;
    private InfoController infoController;
    private ActionController actionController;
    private final GameSearchAction gameSearchAction;

    private List<Player> reconnectPlayerBuffer;

    private boolean destroyed;


    public GameSearchViewModel(String uri, String username, String id, GameSearchAction gameSearchAction){
        WebsocketClientController.connectToServer(uri, id, username);
        connectController = new ConnectController(this::handleConnect);
        infoController = new InfoController(this::handleInfo);
        actionController = new ActionController(this::handleAction);
        this.gameSearchAction = gameSearchAction;
        this.username = username;
        this.destroyed = false;
    }

    public void receiveGames (){
        infoController.getGameListFromServer();
    }

    public void connectToGame(int gameId){
        Log.d("DEBUG", "GameSearchViewModel::connectToGame/ " + gameId);
        if (gameId == -1)
            return;
        actionController.joinGame(gameId);
    }

    public void createGame(String inputText) {
        actionController.createGame(inputText);
    }


    void handleInfo(Info info, List<GameInfo> gameInfos){
        Log.d("DEBUG", "GameSearchViewModel::handleInfo/ " + gameInfos);
        if (gameInfos == null) return;

        gameSearchAction.refreshGameListItems();
        gameInfos.forEach(gameInfo -> gameSearchAction.addGameToScrollView(gameInfo.getId(),
                                                                             gameInfo.getName(),
                                                                             gameInfo.getConnectedPlayers() == null ? 0 : gameInfo.getConnectedPlayers().size(),
                                                                             gameInfo.isStarted()));

    }

    void handleConnect(ConnectType connectType, GameInfo gameInfo){
        if (connectType == ConnectType.CONNECTION_ESTABLISHED)
            gameSearchAction.onConnectionEstablished();

        if (connectType == ConnectType.RECONNECT_TO_GAME){
            gameSearchAction.reconnectToGame(gameInfo);
            this.reconnectPlayerBuffer = gameInfo.getConnectedPlayers();
        }
    }

    void handleAction(Action action, String param, Player fromPlayer, List<Field> fields){
        if (action != Action.RECONNECT_DISCARD && action != Action.RECONNECT_OK && (fromPlayer == null || !fromPlayer.getUsername().equals(username))) {
            if (!destroyed)
                this.receiveGames();
            return;
        }

        if (action == Action.GAME_CREATED_SUCCESSFULLY) {
            WebsocketClientController.getPlayer().setHost(true);
            WebsocketClientController.getPlayer().setColor(fromPlayer.getColor());
            gameSearchAction.switchToGameLobby(username);
        }
        if (action == Action.GAME_JOINED_SUCCESSFULLY){
            WebsocketClientController.getPlayer().setColor(fromPlayer.getColor());
            this.actionController.removeMessageHandler();
            this.connectController.removeMessageHandler();
            this.infoController.removeMessageHandler();
            gameSearchAction.switchToGameLobby(username);
        }
        if (param != null && param.equals(WebsocketClientController.getPlayer().getId()) && action == Action.RECONNECT_OK){
            handleReconnectOk(fromPlayer, fields);
        }
        if (action == Action.RECONNECT_DISCARD && fromPlayer.getId().equals(WebsocketClientController.getPlayer().getId())){
            gameSearchAction.removeReconnectPopUp();

        }
    }

    private void handleReconnectOk(Player fromPlayer, List<Field> fields) {
        Player playerMe = WebsocketClientController.getPlayer();

        this.reconnectPlayerBuffer.forEach(p -> {
            p.setOnTurn(false);
            if (p.getId().equals(fromPlayer.getId()))
                p.setOnTurn(true);
            if (p.getId().equals(playerMe.getId())){
                playerMe.copyFrom(p);
            }
        });
        this.actionController.removeMessageHandler();
        this.connectController.removeMessageHandler();
        this.infoController.removeMessageHandler();
        gameSearchAction.switchToGameBoard(reconnectPlayerBuffer, fields);
    }

    public void reconnectToGame(int gameId) {
        WebsocketClientController.getPlayer().setGameId(gameId);
        actionController.reconnectToGame();
    }

    public void discardReconnect(int gameId) {
        Player myPlayer = WebsocketClientController.getPlayer();
        myPlayer.setDefaulValues();
        actionController.discardReconnect(gameId);
    }

    public void setDestroyed(boolean destroyed){
        this.destroyed = destroyed;
    }
}

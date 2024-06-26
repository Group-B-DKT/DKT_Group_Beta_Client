package com.example.dkt_group_beta.communication.controller;

import android.util.Log;

import com.example.dkt_group_beta.communication.ActionJsonObject;
import com.example.dkt_group_beta.communication.enums.Action;
import com.example.dkt_group_beta.communication.enums.Request;
import com.example.dkt_group_beta.communication.utilities.WrapperHelper;
import com.example.dkt_group_beta.model.Card;
import com.example.dkt_group_beta.model.Field;
import com.example.dkt_group_beta.model.Player;
import com.example.dkt_group_beta.viewmodel.interfaces.InputHandleAction;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActionController {
    private InputHandleAction handleAction;

    public ActionController(InputHandleAction handleAction) {
        this.handleAction = handleAction;
        WebsocketClientController.addMessageHandler(this::onMessageReceived);
    }

    public void removeMessageHandler() {
        WebsocketClientController.removeMessageHandler(this::onMessageReceived);
    }

    public void createGame(String gameName){
        ActionJsonObject actionJsonObject = new ActionJsonObject(Action.CREATE_GAME, gameName);
        String msg = WrapperHelper.toJsonFromObject(Request.ACTION, actionJsonObject);
        WebsocketClientController.sendToServer(msg);
    }

    public void joinGame(int gameId){
        ActionJsonObject actionJsonObject = new ActionJsonObject(Action.JOIN_GAME);
        String msg = WrapperHelper.toJsonFromObject(gameId, Request.ACTION, actionJsonObject);

        WebsocketClientController.sendToServer(msg);
    }
    public void leaveGame() {
        int gameId = WebsocketClientController.getConnectedGameId();
        ActionJsonObject actionJsonObject = new ActionJsonObject(Action.LEAVE_GAME);
        String msg = WrapperHelper.toJsonFromObject(gameId, Request.ACTION, actionJsonObject);
        WebsocketClientController.sendToServer(msg);
    }
    public void buyField(Field field) {
        int gameId = WebsocketClientController.getConnectedGameId();
        ActionJsonObject actionJsonObject = new ActionJsonObject(Action.BUY_FIELD, null, WebsocketClientController.getPlayer(), Collections.singletonList(field));
        String msg = WrapperHelper.toJsonFromObject(gameId, Request.ACTION, actionJsonObject);
        WebsocketClientController.sendToServer(msg);

    }

    public void buyBuilding(Field field){
        int gameId = WebsocketClientController.getConnectedGameId();
        ActionJsonObject actionJsonObject = new ActionJsonObject(Action.BUY_BUILDING, null, WebsocketClientController.getPlayer(), Collections.singletonList(field));
        String msg = WrapperHelper.toJsonFromObject(gameId, Request.ACTION, actionJsonObject);
        WebsocketClientController.sendToServer(msg);
    }


    public void isReady(boolean isReady){
        ActionJsonObject actionJsonObject;
        if (isReady)
            actionJsonObject = new ActionJsonObject(Action.READY, null, WebsocketClientController.getPlayer());
        else actionJsonObject = new ActionJsonObject(Action.NOT_READY, null, WebsocketClientController.getPlayer());

        int connectedGameId = WebsocketClientController.getConnectedGameId();
        if (connectedGameId == -1)
            return;

        String msg = WrapperHelper.toJsonFromObject(connectedGameId, Request.ACTION, actionJsonObject);
        Log.d("DEBUG", msg);
        WebsocketClientController.sendToServer(msg);
    }

    public void initFields(List<Field> fields) {
        Gson gson = new Gson();
        int gameId = WebsocketClientController.getConnectedGameId();
        ActionJsonObject actionJsonObject = new ActionJsonObject(Action.INIT_FIELDS, gson.toJson(fields, ArrayList.class));
        String msg = WrapperHelper.toJsonFromObject(gameId, Request.ACTION, actionJsonObject);
        WebsocketClientController.sendToServer(msg);
    }

    public void diceRolled(int[] diceResults){
        Gson gson =new Gson(); // convert String, int, .. into Json-Object
        int gameId = WebsocketClientController.getConnectedGameId();
        String arr = gson.toJson(diceResults);
        ActionJsonObject actionJsonObject = new ActionJsonObject(Action.ROLL_DICE, arr, WebsocketClientController.getPlayer()); // Action, dice results, and player send to server
        String msg = WrapperHelper.toJsonFromObject(gameId, Request.ACTION, actionJsonObject);
        WebsocketClientController.sendToServer(msg); // sends message to server
    }
    public void gameStarted(List<Field> fields) {
        ActionJsonObject actionJsonObject = new ActionJsonObject(Action.GAME_STARTED, null, fields);
        String msg = WrapperHelper.toJsonFromObject(WebsocketClientController.getConnectedGameId(), Request.ACTION, actionJsonObject);
        WebsocketClientController.sendToServer(msg);
    }

    public void movePlayer(int dice){

        ActionJsonObject actionJsonObject = new ActionJsonObject(Action.MOVE_PLAYER, Integer.toString(dice), null, null);
        String msg = WrapperHelper.toJsonFromObject(WebsocketClientController.getConnectedGameId(), Request.ACTION, actionJsonObject);
        WebsocketClientController.sendToServer(msg);

    }

    public void moneyUpdate(){
        Player player = WebsocketClientController.getPlayer();
        ActionJsonObject actionJsonObject = new ActionJsonObject(Action.UPDATE_MONEY, null, player, null);
        String msg = WrapperHelper.toJsonFromObject(WebsocketClientController.getConnectedGameId(), Request.ACTION, actionJsonObject);
        WebsocketClientController.sendToServer(msg);
    }
    public void payTaxes(Player player){

        ActionJsonObject actionJsonObject = new ActionJsonObject(Action.PAY_TAXES, null, player, null);
        String msg = WrapperHelper.toJsonFromObject(WebsocketClientController.getConnectedGameId(), Request.ACTION, actionJsonObject);
        WebsocketClientController.sendToServer(msg);
    }

    public void updatePlayer(){
      moneyUpdate();
    }

    public void endTurn(){
        ActionJsonObject actionJsonObject = new ActionJsonObject(Action.END_TURN, null);
        String msg = WrapperHelper.toJsonFromObject(WebsocketClientController.getConnectedGameId(), Request.ACTION, actionJsonObject);
        WebsocketClientController.sendToServer(msg);
    }

    public void submitCheat(int money) {
        int gameId = WebsocketClientController.getConnectedGameId();
        ActionJsonObject actionJsonObject = new ActionJsonObject(Action.SUBMIT_CHEAT, String.valueOf(money), WebsocketClientController.getPlayer());
        String msg = WrapperHelper.toJsonFromObject(gameId, Request.ACTION, actionJsonObject);
        WebsocketClientController.sendToServer(msg);
    }
  
    public void reconnectToGame() {
        ActionJsonObject actionJsonObject = new ActionJsonObject(Action.RECONNECT_OK, null);
        String msg = WrapperHelper.toJsonFromObject(WebsocketClientController.getConnectedGameId(), Request.ACTION, actionJsonObject);

        WebsocketClientController.sendToServer(msg);
    }

    public void discardReconnect(int gameId) {
        ActionJsonObject actionJsonObject = new ActionJsonObject(Action.RECONNECT_DISCARD, Integer.toString(gameId), WebsocketClientController.getPlayer(), null);
        String msg = WrapperHelper.toJsonFromObject(gameId, Request.ACTION, actionJsonObject);
        WebsocketClientController.sendToServer(msg);
    }

    public void removePlayer(int gameId, Player player) {
        ActionJsonObject actionJsonObject = new ActionJsonObject(Action.RECONNECT_DISCARD, Integer.toString(gameId), player, null);
        String msg = WrapperHelper.toJsonFromObject(gameId, Request.ACTION, actionJsonObject);
        WebsocketClientController.sendToServer(msg);
    }
    public void reportCheat(Player player, Player fromPlayer) {
        int gameId = WebsocketClientController.getConnectedGameId();
        ActionJsonObject actionJsonObject = new ActionJsonObject(Action.REPORT_CHEAT, player.getId(), fromPlayer, null);
        String msg = WrapperHelper.toJsonFromObject(gameId, Request.ACTION, actionJsonObject);
        WebsocketClientController.sendToServer(msg);
    }

    public void showRisikoCard(int cardIndex){
        int gameId = WebsocketClientController.getConnectedGameId();
        ActionJsonObject actionJsonObject = new ActionJsonObject(Action.RISIKO_CARD_SHOW, Integer.toString(cardIndex), WebsocketClientController.getPlayer()); // Action, card, and player send to server
        String msg = WrapperHelper.toJsonFromObject(gameId, Request.ACTION, actionJsonObject);
        WebsocketClientController.sendToServer(msg); // sends message to server
    }
    public void showBankCard(int cardIndex){
        int gameId = WebsocketClientController.getConnectedGameId();
        ActionJsonObject actionJsonObject = new ActionJsonObject(Action.BANK_CARD_SHOW, Integer.toString(cardIndex), WebsocketClientController.getPlayer()); // Action, card, and player send to server
        String msg = WrapperHelper.toJsonFromObject(gameId, Request.ACTION, actionJsonObject);
        WebsocketClientController.sendToServer(msg); // sends message to server
    }

    private void onMessageReceived(Object actionObject) {
        if (!(actionObject instanceof ActionJsonObject))
            return;

        Log.d("DEBUG", "ActionController::onMessageReceived/ " + ((ActionJsonObject) actionObject).getAction());

        ActionJsonObject actionJsonObject = (ActionJsonObject) actionObject;
        handleAction.handleAction(actionJsonObject.getAction(), actionJsonObject.getParam(), actionJsonObject.getFromPlayer(), actionJsonObject.getFields());
    }

    public void updateRoundsToSkip(int round) {
        Player player = WebsocketClientController.getPlayer();
        ActionJsonObject actionJsonObject = new ActionJsonObject(Action.UPDATE_ROUNDS_TO_SKIP, Integer.toString(round), player, null);
        String msg = WrapperHelper.toJsonFromObject(WebsocketClientController.getConnectedGameId(), Request.ACTION, actionJsonObject);
        WebsocketClientController.sendToServer(msg);
    }
}

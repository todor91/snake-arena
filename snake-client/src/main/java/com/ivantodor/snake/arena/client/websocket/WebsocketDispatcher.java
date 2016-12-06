//package com.ivantodor.snake.arena.client.websocket;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.websocket.MessageHandler;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Observable;
//
///**
// * @author Ivan Todorovic
// */
//public class WebsocketDispatcher extends Observable
//{
//    private Logger logger = LoggerFactory.getLogger(getClass().getName());
//
//    private HashMap<String, JsonMessageListener> listenerMap = new HashMap<String, JsonMessageListener>();
//
//    public void onMessage(String s)
//    {
//        logger.debug("Received message: {}", s);
//
//        try
//        {
//            JsonNode json = new ObjectMapper().readTree(s);
//            String messageType = json.get("type").textValue();
//
//            if(!listenerMap.containsKey(messageType))
//            {
//                logger.warn("No listeners attached for message type: {}", messageType);
//            }
//            else
//            {
//                listenerMap.get(messageType).onMessage(messageType, json);
//            }
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    public void registerListener(List<String> types, JsonMessageListener listener)
//    {
//        for(String type : types)
//        {
//            registerListener(type, listener);
//        }
//    }
//
//    public void registerListener(String type, JsonMessageListener listener)
//    {
//        listenerMap.put(type, listener);
//    }
//}

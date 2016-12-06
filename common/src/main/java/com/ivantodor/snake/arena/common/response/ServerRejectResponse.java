package com.ivantodor.snake.arena.common.response;

import com.ivantodor.snake.arena.common.Message;

/**
 * @author Ivan Todorovic
 */
public class ServerRejectResponse extends Message
{
    public static final String TYPE = "serverReject";
    private String message;

    public ServerRejectResponse()
    {
        super(TYPE);
        this.message = "";
    }

    public ServerRejectResponse(String message) {
        this();
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }
}

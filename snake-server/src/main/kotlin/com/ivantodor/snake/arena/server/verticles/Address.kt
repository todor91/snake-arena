package com.ivantodor.snake.arena.server.verticles

/**
 * @author Ivan Todorovic
 */

object Address {
    val AddressPrefix = "SNAKE.ARENA"

    object Match {
        val MatchPrefix = "MATCH"

        // Common addresses(broadcast)
        val Discover = "${AddressPrefix}.${MatchPrefix}.DISCOVER"

        // Match specific address
        fun status(matchId: String) = "${AddressPrefix}.${MatchPrefix}.$matchId.STATUS"

        // Match verticle receives client's move commands on this address
        fun action(matchId: String) = "${AddressPrefix}.${MatchPrefix}.$matchId.ACTION"
    }

    object Client {
        val ClientPrefix = "CLIENT"

        //Address to send invitationRequest to (to all the players that should be in the game)
        fun invitationRequest(clientId: String) = "${AddressPrefix}.${ClientPrefix}.$clientId.INVITE.REQUEST"

        //When client sends requests to all players, this is the address where all responses will be processed
        fun invitationResponse(clientId: String) = "${AddressPrefix}.${ClientPrefix}.$clientId.INVITE.RESPONSE"

        fun clientHanlder(clientId: String) = "${AddressPrefix}.${ClientPrefix}.$clientId.HANDLER"
    }

    object MatchOrganizer {
        val MatchOrganizerPrefix = "MATCH_ORGANIZER"

        val Invite = "${AddressPrefix}.${MatchOrganizerPrefix}.INVITE.REQUEST"

        val Respond = "${AddressPrefix}.${MatchOrganizerPrefix}.INVITE.RESPONSE"
    }
}

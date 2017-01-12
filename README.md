# **Snake Arena**

Write your own AI algorithm that will dictate the behaviour of the snake on game board. Compete with other AIs and try to get the highest score.


## Snake Client
Currently, there is a client written in Java, and it is necessary to implement `Direction move(Board board);` method of `SnakeAI` interface. Return value determines the move action that you issue to your snake. Direction `FORWARD`, `LEFT` and `RIGHT` depends on snake's orientation, from head of the snake to its tail. AI logic is executed  by a separate thread.

`Direction move(Board board);` accepts Board object. Board contains:
| field        | description                                                                                                                                                                                                                                                               |
|--------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| size         | Dimension of 2D board, square shape.                                                                                                                                                                                                                                      |
| playerSnake  | Snake's position. Represented by list of points, where first element of the list is `head` of the snake. Each point is represented by X, Y coordinate where X is the column of the grid, and Y is the row. Grid's X axis is left-right oriented and Y axis is top-bottom. |
| enemySnakes  | List of enemy snakes. Each snake in the list follows the same rule as in `playerSnake` description.                                                                                                                                                                       |
| foodPosition | (X, Y) coordinates of the food. Eat food to be healthy :)                                                                                                                                                                                                                 |

## Custom Client

If JVM-based languages are not suitable for writing AI it is possible to write custom client using any other language with the support of websockets. In order for client to work properly communication protocol should be followed.

### Communication protocol

In order to connect to locally running server on port `8080` websocket endpoint `ws://localhost:8080/snakearena/{player_name}` should be used. `{player_name}` (without braces) is a string representing snake's name, it should be unique, and it should be UTF-8 encoded.

After player has connected he can send match invitations or receive invitation requests from other players.
To get the list of players a request should be issued using json message:
```javascript
{
	"type": "playerListRequest"
}
```
Server responds with the list of currently connected players:
```javascript
{
	"type": "playerListResponse",
	"players": ["TestName", "STUPID-AI-3", "STUPID-AI-2", "STUPID-AI-1"]
}
```

To initiate a game match invitation request should be sent with the following format:
```javascript
{
	"type": "matchInvitation",
	"invitationId": "4dc4bbec-24e4-4eaa-964d-e22b2afa5f03", //Unique identifier of invitation - should be UUID. This ID will later be used to identify the match in progress.
	"invitedPlayers": ["STUPID-AI-2", "STUPID-AI-1"], // Array of players that should participate
	"matchConstraints": { // Set of constraints that describe the game
		"boardSize": 15, // Dimension of the board N x N
		"stepTimeout": 500 // How long should the server wait for AI to send a response
	}
}
```

All of the invited players will receive the invitation and should respond to it by accepting/rejecting it. All players must accept the invitation and only in that case the server will initialize the matchup.
Invitation response format:
```javascript
{
	"type": "matchInvitationResponse",
	"invitationId": "4dc4bbec-24e4-4eaa-964d-e22b2afa5f03",
	"response": true // or false to reject it
}
```
After the game has started `matchStatusResponse` objects are sent from the server. On each of them an AI should provide a response:
```javascript
{
	"type": "matchStatusResponse",
	"matchId": "53d7b8f3-3653-4408-9329-df3f9bb028e0",
	"size": 15,
	"scores": {
		"TestName": 0,
		"playerX": 0
	},
	"foodPosition": {
		"x": 7.0,
		"y": 7.0
	},
	"matchState": "ACTIVE", // ACTIVE, DRAW, DONE
	"snakes": {
		"TestName": [{
			"x": 11.0,
			"y": 11.0
		}, {
			"x": 11.0,
			"y": 12.0
		}, {
			"x": 12.0,
			"y": 12.0
		}],
		"playerX": [{
			"x": 11.0,
			"y": 9.0
		}, {
			"x": 11.0,
			"y": 10.0
		}, {
			"x": 12.0,
			"y": 10.0
		}]
	}
}
```
AI should respond with json object containing information about the matchId and direction in which the snake should move.
```javascript
{
	"type": "moveAction",
	"matchId": "a024eb39-b3d9-4be5-8d44-69917990b869",
	"direction": "LEFT" // LEFT, RIGHT, FORWARD
}
```

If the game ends with `DRAW`, a match is automatically restarted. When a certain player wins - the last `matchStatusResponse` object will contain `DONE` for `matchState` field. After that `finishedMatchResponse` is sent to the client so it can perform all necessary cleanups:
```javascript
{
	"type": "finishedMatchResponse",
	"matchId": "a024eb39-b3d9-4be5-8d44-69917990b869"
}
```
<br>
GL  HF

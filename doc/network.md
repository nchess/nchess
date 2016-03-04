# Network protocol #

## Messages ##

Data is exchanged through messages. These messages are JSON strings, each message having at
least a type field. This field must be a string, and indicates the message type. Case-sensitive.

On the Java side, based on this type field, the message can be deserialized into a specialized
Message instance.

## Server ##

The server acts as a broadcaster and judge.

The server notifies clients when its their turn, and waits for their move. Then, this move is
validated, then broadcasted. The server also keeps these moves in a list, as a history.

To ensure that clients are in sync, they must acknowledge each move received. This way the
server knows which one is the last move they know about, and can send the remainder when needed.  

For the first version, each server handles only one game. This can later be changed.

## Clients ##

Clients receive moves from the server, keep track of the table, and send their own moves.
Basically that's what they need to do from the networking point of view.

## Game ##

1. The client sends a join request. The client can either join as a player or as a spectator.
  * The server refuses this request if there are no free player slots or no more spectators
  allowed.
2. The server sends the client the current game state. This includes the map, pieces and players.
   Spectators also get the move history. Since players are there through the whole game, they
   don't need the history.
3. The server and the clients wait for sufficient players to join. Then, the game begins.
4. The server notifies the next client that it's its turn.
5. The client responds with its move.
6. If the move is valid, the server broadcasts it and notifies the next client. Otherwise, the
   move is rejected and the clients needs to send another move. After a certain amount of
   invalid moves, the client is dropped.
7. Once an end-condition is met, the match ends and the server notifies each client of the
   outcome.  

If at any point in time a player disconnects, the game ends.

> TODO: It would be cool if the game was then saved, and could be continued later. Clients could
get a key, then request the server to continue that game. This would probably kick in once a lobby
system happens. 

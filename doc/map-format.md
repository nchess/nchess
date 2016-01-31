# Map format #

## JSON ##

JSON is a text-based format that is human-readable, yet is easy to parse. Since it is widespread,
it has mature parsing libraries for most languages - most importantly, C++ and Java. This makes
it is a pretty viable format for saving data in this project.

Hence, we will be using JSON for storing our maps, too.

_JSON has no method for writing comments. However, to illustrate various concepts, Python-like
comments will be used._

## Structure ##

The map is a directed graph. Some nodes may have pieces attached to them. The map file also
needs to store data about the gameplay - currently this consists of a list of players.

**TODO:** Maps could store multiple players and piece layouts for different player counts.
Currently staying with one setup per map.

So, our JSON file will look something like this:

    {
    	"nodes": [...],
    	"links": [...],
    	"players": [...],
    	"pieces": [...]
    }

This a straightforward mapping of the above - there are **nodes**, with **links** connecting
them ( __not bidirectional__! ); there are players, and there are pieces placed on the table.

Let's look at each of the above elements:

### Nodes ###

_nodes_ is a list of node object. A node object is structured as follows:

    {
        "id": 0,
        "x": 0,
        "y": 0,
        "visible": true
    }

Each node object has an explicitly given id. This id can - in theory - be any JSON object.
However, it's more practical to use integers or strings.

Nodes also have locations. Nodes are positioned in 2D space. Even though the game aims to be
quite unconventional, it still has its limits.

For aesthetics purposes, some _invisible buffer nodes_ are appended onto the sides of the maps. This is to
make sure each visible node has enough neighbors to form pleasant boundaries. Otherwise, some
polygons generated from neighbors could go on to infinity.

These are only the _mandatory_ attributes. Later on, optional attributes might be added to enhance
gameplay. These attributes will be strictly optional most of the time, and will assume a default
value if not given explicitly.

The order of the items in the list does not matter.

### Links ###

_links_ is a list of node pairs:

    "links":
    [
        [0,1],
        [1,0],
        [1,2],
        [2,1]
    ]

Every item in the _links_ list must be a list consisting of two items. These items must be
existing node id's.

Do note that these links are not bidirectional. See the 'repetition' above.

The order of the items in the list does not matter.

### Players ###

_players_ is a list of player descriptions. Players have a name and a color for their pieces
to be displayed with.

__NOTE:__ Why colors? Why not make it selectable, or just determine them on the fly.
Then again, without colors, the _players_ list would be a simple list of names.
These names are still needed, because otherwise players would be addressed by arbitraty integers,
and these integers would need to be checked for holes. Which is not an idea the format allows,
since we are already pretty explicit with our data.

    "players":
    [
        {"name": "white", "color": "rgb(255,255,255)"},
        {"name": "black", "color": "black"},
        {"name": "red", "color": "#FF0000"},
        ...
    ]

This time around, the order of the order of the items __does__ matter: players will make their
moves in the order they appear in this list.

For color indicators, standard CSS colors can be used: color by name, rgb or by hex code.

### Pieces ###

_pieces_ describe the initial state of the map - where each piece starts. Each piece has a type,
belongs to a player, and is at a node. These are all indicated:

    "pieces":
    [
        {
            "type": "pawn",
            "player": "white",
            "at": 0
        },
        ...
    ]

The types of pieces recognized depend on the server.

However, the traditional pieces must always be recognized, regardless of the server:
pawn, rook, knight, bishop, queen, king.

The order of the items in the list does not matter.
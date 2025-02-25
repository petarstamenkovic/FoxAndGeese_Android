
# Fox and geese android game

This project is the first task from an Android app development course on a Master year. Fox and Geese is a fun 2-player client-server game. For the implementation, I used Java programming language. As for software, I used Android Studio for developing GUI and client application and NetBeans for server implementation. 


## Description

The game has 2 players with the following roles:
- Fox is a red circle whose goal is to reach the top row of the board. The fox can move one field on any diagonal, both up and down.
- Geese has 4 tokens presented as green circles, whose goal is to *trap* a fox. Trapping the fox means that after a geese move, the fox has no more valid squares to go to. Geese can also move one field diagonally, but only downwards since they start from the top row.

In this implementation, both players move across *dark* fields, but this can easily be modified in a board setup part of the code. The map is loaded through Resources folder and ImageViews so you can simply add any image for the color of the fields or even represent fox with an actual fox sticker, as well as geese. 

The idea of the project was to learn about Android concepts, multi-thread coding and basic GUI operations, so pardon me if you don't fancy the design of the app :D 
## Features

- Application has 2 activities, Main and Game
- Client - server communication
- Basic GUI 
- Ability to challenge players connected to a server


## Screenshots

1. Main activity GUI : Enter IP address and name of the player. Below you can select a player from a list and by pressing a button *challenge player*, you send a request to them. With buttons bellow they can accept or decline a challenge.
   
![LoginScreen](https://github.com/petarstamenkovic/FoxAndGeese_Android/assets/113508828/2746ebc2-a7f0-46c8-ab01-33fd193ca640)

2. Game activity GUI : Board representation and indicator below that shows whose move is it. Example of the geese win situation, fox has no more valid moves.

![GeeseWin](https://github.com/petarstamenkovic/FoxAndGeese_Android/assets/113508828/68edd1bf-72fa-4fb0-b19b-af9565d3718b)

3. Game activity GUI : Situation where fox reaches the top row and wins. Pop-up window that can enable a rematch.

![FoxWin](https://github.com/petarstamenkovic/FoxAndGeese_Android/assets/113508828/a01b4c0b-9c64-4c51-bbf2-6b2dd1a7b4f2)


## FAQ

#### 1. What are these two activities?

As mentioned, the game has 2 activities : 
- *Main activity* handles the logging-in process. It receives the name of the player and IP address. Also, here I handle the part where players can challenge each other and accept/decline the game invite.
- *Game activity* draws the initial board and begins the game. Also, after each move, each player token moves its position. Here are also handled the pop-up windows that warn players if they are trying to play illegal moves.

#### 2. Is the game playable on any Android device?

Yes, in a creation part of a project, I setup one of the oldest android version as a minimum requirement.

#### 3. Can a player decline a challenge? 

Yes, a player can decline a challenge, and after that he is still available for a new one, from a different player. In case you challenge a player that is already in the game, your request will be denied as the requested user is busy.


#### 4. Is the scoring included?

So far no, the scoring is not implemented. This could make sense since this is a client-server game, so server part could store the number of wins for each player. Currently, you can only win or lose against your opponent and feel good after defeating him.




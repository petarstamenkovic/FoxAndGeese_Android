# FoxAndGeese_Android
First task from Android App Development from course on a Master year. 
I implemented a "fox and geese" client-server game, where I used Android Studio(client implementation) and NetBeans(server implementation). Programming language is Java.
App has 2 activities : 
  1. MainActivity - Handles the logging in process (name, ip address and challenge issueing)
  2. GameActivity - Board draws out and users begin with the game.

As mentioned the game client(user) can be one of two players : 
  1. Fox - Fox is a red circle whose goal is to reach the top row of the board. Fox can move 1 field in any diagonal.
  2. Geese - Geese have to trap the fox in terms that it does not have any more valid moves. Geese can move one field diagonal but only downwards.

In my implementation all players move across the dark fields, but this can easily be modified. Also map is loaded through Resource folder and ImageViews so 
you can simply add any image fox the color of the fields, fox image or geese image. 

The idea of the project was to learn about Android concepts, multithread coding and basic GUI operations, so pardon me if you dont fancy the design of the app :D 



![LoginScreen](https://github.com/petarstamenkovic/FoxAndGeese_Android/assets/113508828/2746ebc2-a7f0-46c8-ab01-33fd193ca640)


![GeeseWin](https://github.com/petarstamenkovic/FoxAndGeese_Android/assets/113508828/68edd1bf-72fa-4fb0-b19b-af9565d3718b)


![FoxWin](https://github.com/petarstamenkovic/FoxAndGeese_Android/assets/113508828/a01b4c0b-9c64-4c51-bbf2-6b2dd1a7b4f2)

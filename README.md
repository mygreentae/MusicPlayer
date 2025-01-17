# CSC 335 Music Player
Authors: 
Seth, Leighanna Pipatanangkura, Paris Garcia, Jackson Covey

## Functional aspects: 
 - [x] The ability to download Songs and store in a library.
 - [x] Songs are saved in a .txt file that is then loaded into the program when restarted.
 - [x] Library/Playlist sorting, based on artist, title, date
 - [x] Play, pause, and fast-forward buttons, and  seek and volume bars for the MediaPlayer.
 - [x] Skip 10 seconds forward/backwards buttons.
 - [x] The ability to make Playlists.
 - [x] a Favorites PlayList, with favorite/unfavorite buttons.
 - [x] Playlists are saved in a .txt file that is then loaded into the program when restarted.
 - [x] A million visual/Spotify API glitches we can't account for.


## How to Use: 
A lengthy ReadMe on how to use our music player, as well as a deeper explanation as to how it works. 
## Common issues when trying to run the Music Player: 
1. JavaFX not working: This can be for a couple reasons. Since we are using a JavaFX MediaPlayer, we have to add javafx.media, into our gigantic VM arguments. During developement we had a lot of problems on linux, MacOs, and Windows machines with getting JavaFX to consistently work, including but not limited to, deleting entire branches, adding VM arguments, and manually adding JavaFX .jar files to classpaths.
2. JUNIT: We did include JUnit Test Suite that may or may not run. When switching between branches, JUnit would uninstall itself. Reinstalling it resulted in us having to delete an entire branch because it just stopped working. It will probably result in a, "Errors in WorkSpace" message which can be ignored. Moreover, we inevitably changed some of our back-end design, so many of the tests are no longer functional, and the entire suit if full of error notifications. It can be ignored, but at one point we had 84% coverage, and it was used to test our Model and Controller before we starting programming the GUI View.

## Common issues when running the Music Player:

### Searching for a Song Bugs:
1. Search format requirements: The format MUST be in a "Artist, Title" format. You will get an error Alert if it is not.
2. "Ooos! Something went Wrong!" message: This means the Spotify API said "nope" to whatever song you tried to search up. We have intentionally kept the stack trace because it is interesting. This can be for a lot of reasons, some of which we predict are included here:
    1. The Spotify API cannot find the song based on what you put in the search bar.
    2. The Spotify API does not want you to play the song for copyright reasons???
    3. The distributer of the Song does not want you to play the song for copyright reasons???
    4. The Spotify API doesn't like playing that song???
3. Sometimes it gets the completely wrong song. This is because in our API implementation, it picks the first thing that comes up in the search result via the Spotyify API. Thus, it is a Spotfiy Web API Issue
#### Visual Bugs:
4. If you're playing a playlist, and in the middle of typing something in the Search bar, when update() is called, it will interupt your typing. It is not an actual like bug persay, but it is somewhat frustrating.
5. Album art not loading: This can be attributed to Eclipse, but it can only be fixed some of the time. Because of the way Eclipse works, you need to go to preferences -> General -> Workspace and click the below checkboxes.
- [x] Refresh using native hooks or polling
- [x] Refresh on access
6. This should fix, one of the four types of visual errors. When a song is searched and added to the Music Player, this will allow Eclipse to auto-refresh the project folder. This will allow you to view SOME of the album covers.
7. The second type is those that eventually show up. After a while, some album covers will just magically start working for no explicable reason. 
8. The third type is those that never show up (within a reasonable amount of time). To fix this, or any of the other visual glitches, stop and re-run the program.
9. The fourth type is where the actual song does not even show up. We know our code works because we've tested it extensively. This is also why we know this bug exists. Sometimes for no reason, the song does not appear in the Song Library when viewing the Playlist View Window. It just does not show up. This is of course fixed when you restart the program, but it is of note. 
10. A fifth type has been discovered that stems from one of the Search for a Song Bugs. There are times where a searching for a Song will result in an error message, as well as a Stack Trace print; however, when the View calls update(), there will be 2 copies of the Song in the Playlist View Window. No idea why it does this, it just does, although not very often.
11. Sometimes the MediaBar seek does not function correctly. Not common, but has happened. 
#### Auditory Bugs:
11. Auditory glitches: Sometimes, the Spotify API is able to find a song, but it will play .5 seconds of it, and immediately move to the next song if there is one. There is no way to fix this, it just happens. Restarting the program, deleting the data from the .txt file, and re-searching for the song does not fix this either, so we have concluded it is a Spotify API quirk.

# Running the Music Player:

## When booting up the Main Window:
![](https://i.imgur.com/xwzQDoG.png)

You will see 4 of the 7 main components of our GUI.
1. Menu
2. Album Art
3. Playlist View Window
4. Control Menu


At this time, the control buttons do not work, and clicking the Play Button will send an Alert, saying you must select a Song. 

### Menu: 
Upon the first initial launch, the only way to play a Song is by using the "Search" button to use the Spotify API to get a 30 second preview of a song. 
 
Because the Music Player has no songs, the only button that is functional is the "Make Playlist" button. Switching to any playlist made that is empty will result in an alert message. 

### Album Art
Until a song is played, the Album Art section will display no-cover-art-found.png

### Playlist View Window
Until a song is added, this will remain empty. 


### Control Menu
At this time, the control buttons do not work, and clicking the Play Button will send an Alert, saying you must select a Song. 

##### Control Buttons:
There are 6 Control Buttons total. These buttons are only effective on the current Song. Thus, if no Song is playing, these buttons will not do anything. 
1. The Play Button: Pause and Play the current Song. 
2. The Seek 10 Seconds Forward Button: Seeks 10 seconds forward or back.
3. The Seek 10 Seconds Back Button: Seeks 10 seconds back.
4. The Skip button: Skips to the next song if applicable
5. The Back button: Skips to the previous song if applicable
6. The Shuffle button: This is a toggle button that will show when the playlist is shuffled. This button has a specific implementation due to complexity that will be mentioned later. When active, the button will be green.

## After Searching a Song: 
![](https://i.imgur.com/AFRwlM3.png)
![](https://i.imgur.com/EfjzERU.png)

### Playing a Song:
Once a song has been added, you are able to play it one of two ways. You can click the Shuffle button which will shuffle the current Playlist, or by hovering over the song and clickling the play button in the Playlist View Window. If shuffle is already toggled, clicking the song's playbutton will also put it into shuffle mode. 

![](https://i.imgur.com/IOV9fSU.jpg)

Once a song is played, it will be highlighted, and remain highlighted even when not hovered on by the mouse. We can now see the remaining 3 parts of our GUI.

5. Song Menu
6. Current Song Display
7. Media Bar

### Song Menu: 
Our Song Menu has 2 buttons, "Add Song to Playlist", and "Favorite Song". Adding a song to a playlist requires that a playlist other than Song Library and Favorites exist. Adding duplicate songs to playlists is not allowed, nor is adding songs to the Favorites playlist allowed using this button. 

To add a song to the Favorites playlist, you must click the "Favorite Song" button to toggle it. It will then automatically appear in the Favorites playlist. 

### Current Song Display
This will show the currently playing song, including when the Playlist View Window switches to a new playlist. It shows the song's title, artists, and what playlist is currently playing. 

### Media Bar
This section allows us to seek to specific points in the song. It also has a volume slider, but the slider resets whenever update() is called.

# Implementation/Use Intstructions:
![](https://i.imgur.com/sW715HX.png)

After adding a couple songs, we can now see the full implementation of our program. 
(Note) this is one of the visual ablum cover glitches that will most-likely be fixed once another song is added to the Music Player. (In testing the song was skipped, and then selected again which allowed the album art to load.)

## Controls that are now available:
1. Sort by Artist
2. Sort by Title
3. Sort by Date
4. Back
5. Control Buttons

### Sorting/Back:
By clicking any sorting button, it will sort the Playlist View Window in that order. THIS WILL NOT CHANGE THE PLAY ORDER. The songs will play in the order dictated by the order of the original playlist before sorting. This is an intentional design choice because implementation was deemed too difficult based on time constraints. You are able to click the "Back" button in order to view the original order of the playlist. The "Back" button is also able to revert back to the current playlist if you have created a playlist and are currently viewing it. If you are playing a playlist, the "Back" button will allow you to view the Song Library list of songs.  

### Control Buttons:
1. Prev/Skip
2. Shuffle

#### Prev/Skip
These buttons will now move forward and backward through the playlist in its playOrder. If at the end/beginning of the playlist, each respective button will not do anything. The Music Player does not allow for repeating songs or looping playlists.

#### Shuffle
Implementation:
As mentioned previously, this button is used as a toggle for if the song order should be randomized. At any point, you can activate the "Shuffle" button by clicking it. It will then appear green to indicate activity. When toggling "on", it will randomly start playing a song in whatever playlist is currently in the Playlist View Window. This will put the Music Player into shuffle mode. While in shuffle mode, clicking any song in the Playlist View Window will start a random playlist starting with the clicked song. 

Toggling the "Shuffle" button out of shuffle mode while playing a playlist WILL NOT change the random song order. This is also a design implementation deemed too difficult due to time constraints. In order to play the songs in an unshuffled order, the shuffle mode must be off, and a song must be played via the Playlist View Window. Then the playlist will play in order, starting from the song selected. 

Using the Skip/Prev buttons in shuffle mode will work as a user expects. 



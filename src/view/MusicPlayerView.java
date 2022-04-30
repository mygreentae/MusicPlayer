package view;



import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import controller.MusicPlayerController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.ConstraintsBase;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.MusicPlayerModel;
import song.Song;
import utilities.PlayList;
import javafx.scene.control.ProgressBar;


import utilities.SongLibrary;

public class MusicPlayerView extends Application implements Observer{
	
	private static MusicPlayerController controller;
	private static MusicPlayerModel model; 
	private static SongLibrary songLibrary;
	
	private MediaPlayer player;
	private static final long JUMP_BY = 5000; // this is in milli secs
	
	
	//edit modes/ control what is shown in playlistView
	private static Boolean editMode;
	private static Boolean playListViewMode;
	private static Boolean searchMode;
	//private static something backbutton shit that
	//probably edits these to be either true or false.
	//that makes sense i think, click like "Back" and it makes edit mode 
	//false which changes the view
	
	//media player stuff
	private Media media;
	private MediaPlayer mediaPlayer;
	private MediaView mediaView;
	private MediaBar mediaBar;
	
	private String MEDIA_URL = "";
	
	private ArrayList<Thread> threads;
	private ArrayList<MediaPlayer> mediaPlayers;
	

	
	
	
	private static final int TILE_HEIGHT = 50;
	private static final int TILE_WIDTH = 100;
	private static final int TITLE_FONT_SIZE = 18;
	private static final int CUR_TITLE_SIZE = 30;
	private static final int CUR_ARTIST_SIZE = 10;
	private static final int ARTIST_FONT_SIZE = 10;
	private static final int SCROLL_MAX_HEIGHT = 332;
	private static final int SCROLL_MAX_WIDTH = 250;
	private static Stage mainStage;
	
	
	public static void main(String[] args) {
		launch(args);
	}


	@Override
	public void start(Stage stage) throws IOException, URISyntaxException {
		mainStage = stage;
		songLibrary = new SongLibrary();
		model = new MusicPlayerModel(songLibrary);
		controller = new MusicPlayerController(model);

		threads = new ArrayList<>();
		mediaPlayers = new ArrayList<>();
		
		Song song = songLibrary.getSongs().get(0);
		String path = song.getAudioPath();
		File file = new File(path);
		String MEDIA_URL = file.toURI().toString();
		//System.out.println(MEDIA_URL);
		media = new Media(MEDIA_URL);
		mediaPlayer = new MediaPlayer(media);
		mediaView = new MediaView(mediaPlayer);

		model.addObserver(this);
		
//		URI uri = new URI("");

		//Media media = new Media(mediaURL);
		//player = new MediaPlayer(media);
		
		/*
		EventHandler<KeyEvent> pausePlay = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {

				if (keyEvent.getCode() == KeyCode.SPACE) {
					if (controller.getCurSong() != null) {
						if (controller.getCurSong().isPlaying()) {
							controller.pause();
						} else {
							controller.resume();
						}
					}
				}
			}
		};
		*/
//		Song song = controller.search("400km");
//		controller.changeSong(song);

		
		VBox root = new VBox();
		HBox hbox = new HBox();
		
		// get and set cover art
//		ImageView imageView = new ImageView();
//		imageView.setImage(new Image("images/no-cover-art-found.jpg"));
//		imageView.setFitHeight(400);		model.unShuffle(playlist);
//		imageView.setFitWidth(400);
		
		ImageView image = setAlbumArt(controller.getCurSong());
		
		hbox.setPadding(new Insets(10, 10, 10, 10));
		
		VBox UI = new VBox();
		BorderPane menu = new Menu(songLibrary);
		BorderPane bottomMenu = new BottomMenu(songLibrary);
		
		ScrollPane songView = playListView();
		songView.setPrefViewportWidth(SCROLL_MAX_WIDTH);
		songView.setPrefViewportHeight(SCROLL_MAX_HEIGHT);
		
		UI.getChildren().addAll(menu, songView, bottomMenu);
		
		
		
		hbox.getChildren().addAll(image, UI);
		
		VBox curSongView = showCurSong();
		GridPane controls = setButtons();
		
		
		//MediaBar bar = new MediaBar(player);
		
		curSongView.setAlignment(Pos.CENTER);
		controls.setAlignment(Pos.CENTER);
		root.getChildren().addAll(hbox, curSongView);
//		root.getChildren().add(new MediaView(player));
		
		
		//root.getChildren().add(p2);
		Scene scene = new Scene(root);
//		scene.setOnKeyReleased(pausePlay);
		stage.setScene(scene);
		stage.setTitle("Music Player");
		stage.show();
//		handleEvents(playPause, stop);
		
	}
	
	private VBox showCurSong() {
		VBox vbox = new VBox();
		
		String title = "";
		String artist = "";
		Song song = controller.getCurSong();
		if (song != null) {
			title = song.getName();
			artist = song.getArtist();
		}
		
		Text titleText = new Text();
		Text artistText = new Text();
		Text typeText = new Text();
		
		titleText.setText(title);
		artistText.setText(artist);
		
		titleText.setFont(new Font(CUR_TITLE_SIZE));
		titleText.setFill(Color.BLACK);
		titleText.setStyle("-fx-font-weight: bold");
		
		artistText.setFont(new Font(CUR_ARTIST_SIZE));
		artistText.setFill(Color.GRAY);
		artistText.setStyle("-fx-font-weight: bold");
		
		typeText.setFont(new Font(CUR_ARTIST_SIZE));
		typeText.setFill(Color.GRAY);
		typeText.setStyle("-fx-font-weight: bold");
		
		if (controller.isPlayingPlaylist()) {
			String playlist = controller.getCurPlaylist().getName();
			typeText.setText("Playing: " + playlist);
		} else if (controller.isPlayingQueue()) {
			typeText.setText("Queue");
		}
		
		
		vbox.setPadding(new Insets(0, 0, 20, 0));
		vbox.getChildren().addAll(titleText, artistText, typeText);
		
		return vbox;
	}
	
	private GridPane setButtons() {
		GridPane gp = new GridPane();
        gp.setHgap(10);

        // play/pause/resume can all be the same button
 //       Button play = new Button("Play");
//        play.setShape(new Circle(15));
//        GridPane.setConstraints(play, 0,0);
//        play.setOnAction(event->  playAudio());

        Button back = new Button("Unskip");
        GridPane.setConstraints(back, 1,0);
        back.setOnAction(event -> pauseAudio());
        if (controller.getCurSong() == null){
        	back.setText("Back");
        }
        
        
        Button pause = new Button("Play/Pause");
        GridPane.setConstraints(pause, 2,0);
        pause.setOnAction(event -> pauseAudio());
        if (controller.getCurSong() == null){
        	pause.setText("Pick Song");
        } else {
        	pause.setText("Play/Pause");
        }

//        Button resume = new Button("Resume");
//        resume.setShape(new Circle(15));
//        GridPane.setConstraints(resume, 2,0);
//        resume.setOnAction(event -> resumeAudio());

        //right side of play, also need a back button
        Button skip = new Button("Skip");
        GridPane.setConstraints(skip, 3,0);
        skip.setOnAction(event ->  skipAudio());

        
        //idk what we need these for
        Button restart = new Button("Restart");
        GridPane.setConstraints(restart, 4,0);
        restart.setOnAction(event ->  restartAudio());

        Button jump = new Button("Jump");
        //GridPane.setConstraints(jump, 5,0);
        jump.setOnAction(event ->  jump(JUMP_BY));
        
        Label searchLabel = new Label();
        searchLabel.setText("Search");
        GridPane.setConstraints(searchLabel, 5, 0);
        TextField search = new TextField();
        GridPane.setConstraints(search, 6, 0);
        // figure out!
//        Label time = new Label();
//        GridPane.setConstraints(time, 6,0);
//        time.textProperty().bind(player.currentTimeProperty().asString("%.4s") );

//        gp.getChildren().addAll(play, pause, resume, skip, restart, jump);
        gp.getChildren().addAll(back, pause, skip, search);
        
        return gp;
	}
	
	//play audio 
    public void playAudio() {
        //player.play();
    	controller.changeSong(controller.getCurSong());
    }

    //pause audio
    public  void pauseAudio() {
    	// media player
    	/*
        if (player.getStatus().equals(Status.PAUSED)) {
            System.out.println("audio is already paused");
            return;
        }
        player.pause();
        */
    	
    	// back-end
    	
    	if (controller.getCurSong() == null) {
    		return;
    	}
    	if (controller.isPlayingSong()) {
    		controller.pause();
    	} else {
    		controller.resume();
    	}
    }

    //resume audio
    public void resumeAudio()
    {	// media player
    	/*
        if (player.getStatus().equals(Status.PLAYING))
        {
            System.out.println("Audio is already playing");
            return;
        }
        playAudio();
       	*/
    	
    	// back-end
    	if (!controller.isPlayingSong()) {
    		controller.resume();
    	} else {
    		System.out.println("audio is already playing");
    	}
    }

    //restart audio
    public void restartAudio() {
        //player.seek(Duration.ZERO);
        //playAudio();
    	controller.restart();
    }

    // stop audio
    public void skipAudio() {
    	if (controller.getCurSong() == null) {
    		return;
    	}
       //player.stop();
    	controller.skip();
    }

    //jump by c millis 
    public void jump(long c) {
        player.seek(player.getCurrentTime().add(Duration.millis(c)));
    }
    
    public void makePlayList() {
    	String name = "";
    	controller.makePlaylist(name);
    }

	private ScrollPane playListView() {
		ScrollPane scroller = new ScrollPane();
		GridPane songView = new GridPane();
//		songView.setPadding(new Insets(5, 10, 0, 20));
		
		//change to current song queue
		//ArrayList<Song> songList = controller.getCurPlaylist().getSongList();
		// add if (curPlaylist == null) {
		// then this line, otherwise, songList == curPlaylist;
		
		ArrayList<Song> songList = songLibrary.getSongs();
		PlayList playlist = controller.getCurPlaylist();
		if (playlist != null) {
			songList = playlist.getSongList();
		} 
		
		EventHandler<MouseEvent> playSong = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				stopThreads();
				mediaPlayers = new ArrayList<>();
				
				Node source = (Node)mouseEvent.getTarget();
				Node p = source.getParent(); //idk why SongTile is double parent.
				Song song = ((SongTile)p).getSong();

				
				Media file = new Media(new File(song.getAudioPath()).toURI().toString());
				mediaPlayer = new MediaPlayer(file);
				mediaView = new MediaView(mediaPlayer);
				mediaPlayers.add(mediaPlayer);
				Runnable runnable =
					    new Runnable(){
							public void run() {
								mediaPlayer.setAutoPlay(true);
					        }
					    };
				//controller.(librarySongs, false, null);  
				Thread thread = new Thread(runnable);
				threads.add(thread);
				thread.start();

				controller.changeSong(song);
			}
		};

//		EventHandler<MouseEvent> playSong = new EventHandler<MouseEvent>() {
//			@Override
//			public void handle(MouseEvent mouseEvent) {
//				Node source = (Node)mouseEvent.getTarget();
//				Node p = source.getParent();
//				Song song = ((SongTile)source).getSong();
//				controller.changeSong(song);
//			}
//		};
		
		EventHandler<MouseEvent> highlightSong = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				Node source = (Node)mouseEvent.getTarget();
				SongTile t = (SongTile)source;
				Background highlight = new Background(new BackgroundFill(Color.LIGHTGREY, new CornerRadii(0), Insets.EMPTY));
				t.setBackground(highlight);
				t.getPlayButton().setVisible(true);
	
				//controller.changeSong(song);
			}
		};
		
		EventHandler<MouseEvent> unhighlightSong = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				Node source = (Node)mouseEvent.getTarget();
				SongTile t = (SongTile)source;
				Background highlight = new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), Insets.EMPTY));
				t.setBackground(highlight);
				t.getPlayButton().setVisible(false);
				
				if (t.getSong() == controller.getCurSong()) {
					Background highlight2 = new Background(new BackgroundFill(Color.LIGHTGREY, new CornerRadii(0), Insets.EMPTY));
					t.setBackground(highlight2);
				};			
				//controller.changeSong(song);
			}
		};
		
		for (int i = 0; i < songList.size(); i++) {
			SongTile songTile = new SongTile(songList.get(i));
//			songTile.getIndex().setText(Integer.toString(i+1));
			songTile.getTitle().setText(songList.get(i).getName());
			songTile.getArtist().setText(songList.get(i).getArtist());
			
			//songTile.setOnMouseClicked(playSong);
			songTile.getPlayButton().addEventFilter(MouseEvent.MOUSE_CLICKED, playSong);

			songTile.setOnMouseEntered(highlightSong);
			songTile.setOnMouseExited(unhighlightSong);
			songTile.setPrefWidth(250);
			songView.add(songTile, 1, i);
			songTile.border.autosize();
		}
		//songView.getColumnConstraints().add(new ColumnConstraints(75));
		scroller.setContent(songView);
		return scroller;
	}
	
	private void handleEvents(Button playPause, Button stop) {
		playPause.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent ae) {
				controller.resume();
				
			}
			
		});
		stop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent ae) {
				controller.pause();
				
			}
			
		});
	}
	
	private void changePlaylist(String name) {
		// controller.changePlaylist(String name)
	}

	private ImageView setAlbumArt(Song curSong) {
		System.out.println("cursong");
		System.out.println(curSong);
		
    	ImageView imageView = new ImageView();
    	if (curSong == null) {
    		imageView.setImage(new Image("images/no-cover-art-found.jpg"));
    	}
    	else if (curSong.getCover() == null) {
    		imageView.setImage(new Image("images/no-cover-art-found.jpg"));
    	} 
    	else {
    		System.out.println(curSong.getCover().substring(4));
    		System.out.println("curSong cover");
    		//imageView.setImage(new Image("images/monteroArt.jpg"));
    		//imageView.setImage(new Image("images/monteroArt.jpg"));
    		try {
    			Image i = new Image(curSong.getCover().substring(4).strip());
    			imageView.setImage(i);
    			System.out.println("image path");
    			System.out.println(i.getUrl());
    		} catch (IllegalArgumentException e) {
    			System.out.println("error with album image");
    			imageView.setImage(new Image("images/no-cover-art-found.jpg"));
    		}
    		// change
    	}
    	
    	imageView.setFitHeight(400);
		imageView.setFitWidth(400);
		
    	return imageView;
    }
	

	
	
	
	
	private class SongTile extends BorderPane {
		
		private Button playButton;
		private Text title;
		private Text artist;
		
		public BorderPane border;
		private Rectangle titleRect;
		private Rectangle artistRect;
		
		private StackPane titleStack;
		private StackPane artistStack;
		
		private Song song;
		
		private SongTile(Song song) {
			this.song = song;
			playButton = new Button();
			title = new Text();
			artist = new Text();
			
			border = new BorderPane();
			
			if (song == controller.getCurSong()) {
				Background highlight = new Background(new BackgroundFill(Color.LIGHTGREY, 
						new CornerRadii(0), Insets.EMPTY));
				title.setFill(Color.AQUA);
				this.setBackground(highlight);
			};
			
			
			
			titleRect = new Rectangle();
			artistRect = new Rectangle();
			
			titleStack = new StackPane();
			artistStack = new StackPane();
			
			titleStack.getChildren().addAll(titleRect, title);
			artistStack.getChildren().addAll(artistRect, artist);
			
			title.setFont(new Font(TITLE_FONT_SIZE));
			
			artist.setFont(new Font(ARTIST_FONT_SIZE));
			artist.setFill(Color.GRAY);
			
			setAlignment(title, Pos.TOP_RIGHT);
			setAlignment(artist, Pos.BOTTOM_RIGHT);
			
			setMargin(border, new Insets(5, 5, 5, 5));
			setMargin(playButton, new Insets(10, 5, 5, 5));
			
			border.setTop(title);
			border.setBottom(artist);
		
			
			playButton = new Button();
			playButton.setVisible(false);
			//Button button= new Button();
//			playButton.setPrefHeight(40);
//			playButton.setPrefWidth(40);
//			playButton.setStyle("-fx-shape: 'M 0 0 0 40 20 20 z'; -fx-border-color: rgb(49, 89, 23); -fx-border-radius: 5");
			//playButton.setStyle("-fx-padding: 3 6 6 6");
		   // "-fx-border-radius: 5"
		    //"-fx-padding: 3 6 6 6"

			playButton.setShape(new Circle(10));
//			button.setText("play");
	        ImageView imageView = new ImageView(new Image ("utilities/buttons/play.png"));
	        playButton.setGraphic(imageView);
	        imageView.setFitHeight(25);
	        imageView.setFitWidth(25);
	        imageView.setPreserveRatio(true);
	        //Important otherwise button will wrap to text + graphic size (no resizing on scaling).
	        playButton.setMaxWidth(Double.MAX_VALUE);    
	        playButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

	        
			setMargin(playButton, new Insets(10, 10, 10, 5));
			
			setLeft(playButton);
			playButton.setAlignment(Pos.CENTER);
			setRight(border);
			
			setStyle("-fx-border-color: black; -fx-border-style: solid hidden solid hidden;");
		}
		
		/**
		 * Returns the index object that is placed in the middle
		 * of the rectangle
		 * @return
		 * 		The index object that appears in the center of the
		 * 		rectangle
		 */
		private Button getButton() {
			return playButton;
		}
		
		/**
		 * Returns the title object that is placed in the middle
		 * of the rectangle
		 * @return
		 * 		The title object that appears in the center of the
		 * 		rectangle
		 */
		private Text getTitle() {
			return title;
		}
		
		private Button getPlayButton() {
			return playButton;
		}
		
		/**
		 * Returns the artist object that is placed in the middle
		 * of the rectangle
		 * @return
		 * 		The artist object that appears in the center of the
		 * 		rectangle
		 */
		private Text getArtist() {
			return artist;
		}
		
		public Song getSong() {
			return song;
		}
	
	}
	
	
	
	
	private class Menu extends BorderPane {
	
	
	public BorderPane border;
	private GridPane menu;

	
	
	private Button playButton;
	private Button shuffleButton;
	private Button artistButton;
	private Button titleButton;
	private Button dateButton;
	
	private Song song;
	private SongLibrary songLibrary;
	
	private Menu(SongLibrary songLibrary) {
		this.songLibrary = songLibrary;
		border = new BorderPane();
		playButton = new Button("Play");
		shuffleButton = new Button("Shuffle");
		artistButton = new Button("Artist");
		titleButton = new Button("Title");
		dateButton = new Button("Date");
		menu = new GridPane();
		
		GridPane.setConstraints(playButton, 1, 0);
		GridPane.setConstraints(shuffleButton, 2, 0);
		GridPane.setConstraints(artistButton, 3, 0);
		GridPane.setConstraints(titleButton, 4, 0);
		
		menu.getChildren().addAll(playButton, shuffleButton, artistButton, titleButton);
		
		menu.setHgap(10);
        menu.setVgap(10);
		
		border.setCenter(menu);
		
		Background highlight = new Background(new BackgroundFill(Color.LIGHTGREY, new CornerRadii(0), Insets.EMPTY));
		this.setBackground(highlight);
		addEventHandlers();
		setCenter(border); 
		
		
	}
	
	
	private void addEventHandlers() {	
		EventHandler<MouseEvent> playPlaylist = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				stopThreads();
				System.out.println("played playlist");
				PlayList librarySongs = new PlayList(songLibrary.getSongs());
				if (controller.getCurPlaylist() == null) {
					controller.playPlaylist(librarySongs, false, null);
					//controller.playPlaylist(librarySongs, false, null);
				} else {
					controller.playPlaylist(controller.getCurPlaylist(), false, null);			
				}
				
				Song song = controller.getCurPlaylist().getSongList().get(0);
				controller.changeSong(song);
				Runnable runnable =
					    new Runnable(){
					        public void run(){
					        	for (Song song : controller.getCurPlaylist().getSongList()) {
					        		//controller.changeSong(song);
					        		Media file = new Media(new File(song.getAudioPath()).toURI().toString());
									mediaPlayer = new MediaPlayer(file);
									mediaView = new MediaView(mediaPlayer);
									mediaPlayers.add(mediaPlayer);
									
									mediaPlayer.setOnEndOfMedia(() -> {
									      System.out.println();
									 });
					        	}
					        	mediaPlayers.get(0).play();
					        }
					    };
				//controller.playPlaylist(librarySongs, false, null);  
				Thread thread = new Thread(runnable);
				threads.add(thread);
				thread.start();
			} 
			
		};
		
		EventHandler<MouseEvent> shufflePlaylist = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				stopThreads();
				System.out.println("shuffled playlist");
				
				PlayList librarySongs = new PlayList(songLibrary.getSongs());
				if (controller.getCurPlaylist() == null) {
					controller.playPlaylist(librarySongs, true, null);
				} else {
					controller.playPlaylist(controller.getCurPlaylist(), true, song);			
				}
				
				Song song = controller.getCurPlaylist().getSongList().get(0);

			}
		};
		
		EventHandler<MouseEvent> sortPlaylistbyArtist = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				System.out.println("sorted by artist");
				PlayList curPlaylist = controller.getCurPlaylist();
				
				ArrayList<Song> songs = songLibrary.getSongs();
				if (curPlaylist != null){
					songs = curPlaylist.getSongList();
					curPlaylist.setSongList(controller.sortArtist(songs));
				} else {
					songLibrary.setSongs(controller.sortArtist(songs));
					for (Song song : songLibrary.getSongs()) {
						System.out.println(song.getName());
					}
					update(model, null);
				}
			}
		};
		
		EventHandler<MouseEvent> sortPlaylistbyTitle = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				System.out.println("sorted by artist");
				PlayList curPlaylist = controller.getCurPlaylist();
				
				ArrayList<Song> songs = songLibrary.getSongs();
				if (curPlaylist != null){
					songs = curPlaylist.getSongList();
					curPlaylist.setSongList(controller.sortTitle(songs));
				} else {
					songLibrary.setSongs(controller.sortTitle(songs));
					for (Song song : songLibrary.getSongs()) {
						System.out.println(song.getName());
					}
					update(model, null);
				}
			}
		};
		playButton.addEventHandler(MouseEvent.MOUSE_CLICKED, playPlaylist);
		shuffleButton.addEventHandler(MouseEvent.MOUSE_CLICKED, shufflePlaylist);
		artistButton.addEventHandler(MouseEvent.MOUSE_CLICKED, sortPlaylistbyArtist);
		titleButton.addEventHandler(MouseEvent.MOUSE_CLICKED, sortPlaylistbyTitle);
		}	
	}

	
	private class BottomMenu extends BorderPane {
		
		
		public BorderPane border;
		private GridPane menu;

		
		
		private Button playButton;
		private Button shuffleButton;
		private Button editButton;
		private Button playlistButton;
		
		private Song song;
		private SongLibrary songLibrary;
		
		private BottomMenu(SongLibrary songLibrary) {
			this.songLibrary = songLibrary;
			border = new BorderPane();
			playButton = new Button("Back");
			shuffleButton = new Button("idk2");
			editButton = new Button("Search?");
			playlistButton = new Button("Playlists");
			menu = new GridPane();
			
			GridPane.setConstraints(playButton, 1, 0);
			GridPane.setConstraints(shuffleButton, 2, 0);
			GridPane.setConstraints(editButton, 3, 0);
			GridPane.setConstraints(playlistButton, 4, 0);
			
			menu.getChildren().addAll(playButton, shuffleButton, editButton, playlistButton);
			
			menu.setHgap(10);
	        menu.setVgap(10);
			
			border.setCenter(menu);
			
			Background highlight = new Background(new BackgroundFill(Color.LIGHTGREY, new CornerRadii(0), Insets.EMPTY));
			this.setBackground(highlight);
			//addEventHandlers();
			setCenter(border); 			
		}
		
		
		private void addEventHandlers() {
			
			EventHandler<MouseEvent> playPlaylist = new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					System.out.println("played playlist");
					PlayList librarySongs = new PlayList(songLibrary.getSongs());
					if (controller.getCurPlaylist() == null) {
						controller.playPlaylist(librarySongs, false, null);
					} else {
						controller.playPlaylist(controller.getCurPlaylist(), false, null);
					}
				} 
				
			};
			
			EventHandler<MouseEvent> shufflePlaylist = new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					System.out.println("shuffled");
					PlayList librarySongs = new PlayList(songLibrary.getSongs());
					if (controller.getCurPlaylist() == null) {
						controller.playPlaylist(librarySongs, true, null);
					} else {
						controller.playPlaylist(controller.getCurPlaylist(), true, null);
					}

				}
			};
			playButton.addEventHandler(MouseEvent.MOUSE_CLICKED, playPlaylist);
			shuffleButton.addEventHandler(MouseEvent.MOUSE_CLICKED, shufflePlaylist);
		}	
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		/*
		if (controller.getCurSong() != null) {
			 String path = controller.getCurSong().getAudioPath();
		     File file = new File(path);
		     String path2 = file.toURI().toString();
		     //Instantiating Media class  
		     Media media = new Media(path2); 
		     mediaPlayer = new MediaPlayer(media);  
	          
	        //by setting this property to true, the audio will be played   
	        mediaPlayer.setAutoPlay(true); 
		}
		*/
		
		
		VBox root = new VBox();
		HBox hbox = new HBox();
		
		// get and set cover art
//		ImageView imageView = new ImageView();
//		imageView.setImage(new Image("images/no-cover-art-found.jpg"));
//		imageView.setFitHeight(400);
//		imageView.setFitWidth(400);
		hbox.setPadding(new Insets(10, 10, 10, 10));
		
		ImageView image = setAlbumArt(controller.getCurSong());

		VBox UI = new VBox();
		BorderPane menu = new Menu(songLibrary);
		BorderPane bottomMenu = new BottomMenu(songLibrary);
		
		ScrollPane songView = playListView();
		songView.setPrefViewportWidth(SCROLL_MAX_WIDTH);
		songView.setPrefViewportHeight(SCROLL_MAX_HEIGHT);
		
		UI.getChildren().addAll(menu, songView, bottomMenu);
		
		hbox.getChildren().addAll(image, UI);
		
		VBox curSongView = showCurSong();
		GridPane controls = setButtons();
		
		if (controller.isPlayingSong()) {
			mediaBar = new MediaBar(mediaPlayers);
			curSongView.setAlignment(Pos.CENTER);
			controls.setAlignment(Pos.CENTER);
			root.getChildren().addAll(hbox, curSongView, mediaBar);
		} else {
			curSongView.setAlignment(Pos.CENTER);
			controls.setAlignment(Pos.CENTER);
			root.getChildren().addAll(hbox, curSongView);
		}
		Scene scene = new Scene(root);
		mainStage.setScene(scene);
		
	}
	
	/**
	 * Stops any running threads
	 */
	private void stopThreads() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
		}
		for (Thread thread : threads) {
			thread.stop();
		}
	}

}

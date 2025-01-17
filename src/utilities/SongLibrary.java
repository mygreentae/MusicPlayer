package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import song.Song;

/**
 * Creates and stores all the Songs that will be used in the Music Player.
 * 
 * Accesses data.txt in order to get information to build Songs. 
 * Uses them to link them to .wav files in the Audio file.
 * 
 * Can throw an IOException, but is handled in the constructor.
 * IOException only occurs if Files don't exist. 
 * 
 * 
 * Properties: 
 * 
 * songLibrary:
 * An ArrayList of all the Songs created when this class is initialized. 
 * 
 * @author Leighanna/Jackson/Paris
 */
public class SongLibrary {
	
	ArrayList<Song> songLibrary;
	ArrayList<String> songTitleArtist;
	ArrayList<PlayList> playlists;
	

	/**
	 * Creates the song Library
	 */
	public SongLibrary() {
		songLibrary = new ArrayList<>();
		playlists = new ArrayList<>();
		try {
			addSongs();
			loadPlaylists();
		} catch (IOException e) {
			
			return;
		}
	}

	/**
	 * Adds all Songs to library from data.txt
	 * @throws IOException
	 */
	private void addSongs() throws IOException {
		List<String> dataList = Files.readAllLines(Paths.get("data.txt"), StandardCharsets.UTF_8);
		File dir = new File("Audios");
		File[] paths = dir.listFiles();
		if (paths != null) {
			for (File p : paths) {
				if (p.toString().endsWith(".wav")) {
					for (String data : dataList) {
						if (data.contains(p.toString())) {
							String[] info = data.split(",");
							Song song = new Song(info[0].trim(), info[1].trim(), info[2].trim(), info[3].trim(), info[4].trim(), info[5].trim());
							songLibrary.add(song);
							song.setCover(info[3].trim());
						} 
					}			
				}
			}
		} 
		
		// this works, arts have to have the name formatted to match the name 
		// of the song, including spaces and weird stuff
		File dir2 = new File("src/images");
		File[] paths2 = dir2.listFiles();
		if (paths2 != null) {
			for (File p : paths2) {
				for (Song song : songLibrary) {
					if (p.toString().contains(song.getName())){
						song.setCover(p.toString());
					} 
				}
			}
		}
	}
	
	/**
	 * Returns the ArrayList of Songs
	 * 
	 * @return the ArrayList of Songs
	 */
	public ArrayList<Song> getSongs(){
		return songLibrary;
	}
	
	/**
	 * Returns the ArrayList of Songs
	 * 
	 * @return the ArrayList of Songs
	 */
	public void setSongs(ArrayList<Song> list){
		songLibrary = list;
	}
	

	/**
	 * Returns the ArrayList of PlayLists
	 * 
	 * @return the ArrayList of PlayLists
	 */
	public ArrayList<PlayList> getPlaylists(){
		return playlists;
	}
	
	/**
	 * Reads a file that contains playlist data
	 * 
	 * @throws IOException when file cannot be found
	 */
	public void loadPlaylists() throws IOException {
		List<String> dataList = readFile("playlists.txt");
		for (String data : dataList) {
			String[] lines = data.split(":");
			String playlistName = lines[0];
			if (lines.length == 1) {
				return;
			}
			String[] songs = lines[1].split(";");
			PlayList p = new PlayList(playlistName);
			if (!p.getName().equals("Song Library")) {
				for (Song song : songLibrary) {
					String name = song.getName() + ", " + song.getArtist();
					for (String songData: songs) {
						if (name.equals(songData.strip())){
							p.addSong(song);
						}
					}
				}
				playlists.add(p);
			}
		} 
	}

	
	/**
	 * Adds an individual song to the song library
	 * 
	 * @param song
	 * 		is the individual song to be added to the song library
	 */
	public void addSong(Song song) {
		File dir2 = new File("src/images");
		File[] paths2 = dir2.listFiles();
		if (paths2 != null) {
			for (File p : paths2) {
				if (p.toString().contains(song.getName())){
					song.setCover(p.toString());
				} 
			}
		}
		
		for (Song songs : songLibrary) {
			if (songs.getArtPath().equals(song.getArtPath())) {
				return;
			}
		}
		songLibrary.add(song);

	} 
	
	public void removeSong(Song song) {
		String artPath = song.getArtPath();
		String audioPath = song.getAudioPath();
		removeArt(artPath);
		removeAudio(audioPath);
		songLibrary.remove(song);
		for (PlayList p : playlists) {
			p.removeSong(song);
		}
		try {
			removeFromFile(song.getName(),song.getArtist());
		} catch (IOException e) {
			System.out.println();
		}
		
	}
	
	private void removeArt(String artPath) {
		File dir2 = new File("src/images");
		File[] paths2 = dir2.listFiles();
		if (paths2 != null) {
			for (File p : paths2) {
				if (p.toString().equals(artPath)){
					p.delete();
				} 
			}
		}
	}
	
	private void removeAudio(String audioPath) {
		File dir2 = new File("Audios");
		File[] paths2 = dir2.listFiles();
		if (paths2 != null) {
			for (File p : paths2) {
				if (p.toString().equals(audioPath)){
					p.delete();
				} 
			}
		}
	}
	
	private void removeFromFile(String name, String artist) throws IOException {
		List<String> dataList = Files.readAllLines(Paths.get("data.txt"), StandardCharsets.UTF_8);
		for (String data : dataList) {
			if (data.contains(name) && data.contains(artist)) {
				data = "";
			} 
		}			
	}

	
	
	
	/**
	 * This is a helper function that reads in the playlist.txt file
	 * 
	 * @param fileName, the name of the file you want to open
	 * @return an ArrayList of Strings that contain the files contents
	 */
	public static ArrayList<String> readFile(String fileName) {
		List<String> dataList = new ArrayList<>();
	    try {
	      File myObj = new File(fileName);
	      Scanner myReader = new Scanner(myObj);
	      while (myReader.hasNextLine()) {
	        String data = myReader.nextLine();
	        dataList.add(data);
	      }
	      myReader.close();
	    } catch (FileNotFoundException e) {
	      System.out.println("An error occurred.");
	      e.printStackTrace();
	    }
	    return (ArrayList<String>) dataList;
	  }
}

package bg.sofia.uni.fmi.mjt.server.repositories;

import bg.sofia.uni.fmi.mjt.server.playlist.Playlist;
import bg.sofia.uni.fmi.mjt.server.track.Track;

import java.util.Map;

public interface PlaylistRepository {
    void addSongToPlaylist(Track song, String playlistName);

    void removeSongFromPlaylist(Track song, String playlistName);

    void createPlaylist(String playlistName);

    void deletePlaylist(String playlistName);

    Playlist getPlaylist(String playlistName);

    void savePlaylistToFile(String playlistName);

    Playlist loadPlaylistFromFile(String playlistName);

    Map<String, Playlist> loadAllPlaylistsFromFile();
}

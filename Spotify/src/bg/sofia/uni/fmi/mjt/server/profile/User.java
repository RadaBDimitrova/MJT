package bg.sofia.uni.fmi.mjt.server.profile;

import bg.sofia.uni.fmi.mjt.server.playlist.Playlist;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String email;
    private String password;
    private List<Playlist> playlists;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.playlists = new ArrayList<>();
    }

    public boolean authenticate(String password) {
        return this.password.equals(password);
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void addPlaylist(Playlist playlist) {
        playlists.add(playlist);
    }

    public Playlist getPlaylist(String name) {
        for (Playlist playlist : playlists) {
            if (playlist.getName().equals(name)) {
                return playlist;
            }
        }
        return null;
    }
}

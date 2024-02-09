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

    public boolean authenticate(Integer hashCode) {
        return this.password.hashCode() == hashCode;
    }

    public String getEmail() {
        return email;
    }

    public Integer getPassword() {
        return password.hashCode();
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

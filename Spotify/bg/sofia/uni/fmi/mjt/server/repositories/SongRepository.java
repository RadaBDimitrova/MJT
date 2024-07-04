package bg.sofia.uni.fmi.mjt.server.repositories;

import bg.sofia.uni.fmi.mjt.server.track.Track;

import java.util.List;

public interface SongRepository {

    Track searchSongByName(String songName, String artistName);

    List<Track> searchSongByKeyword(String songName);

    List<Track> topNSongsByTimesPlayed(int n);
}

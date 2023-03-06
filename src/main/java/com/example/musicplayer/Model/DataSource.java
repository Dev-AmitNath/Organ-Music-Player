package com.example.musicplayer.Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataSource {
    public static final String DB_NAME = "music.db";
    public static final String CONNECTION_STRING = "jdbc:sqlite:src\\main\\resources\\" + DB_NAME;

    //Using column indices are more efficient because the getter methods exactly know the position of the column.
    public static final String TABLE_ALBUMS = "albums";
    public static final String COLUMN_ALBUM_ID = "_id";
    public static final String COLUMN_ALBUM_NAME = "name";
    public static final String COLUMN_ALBUM_ARTIST = "artist";
    public static final int INDEX_ALBUM_ID = 1;
    public static final int INDEX_ALBUM_NAME = 2;
    public static final int INDEX_ALBUM_ARTIST = 3;

    public static final String TABLE_ARTISTS = "artists";
    public static final String COLUMN_ARTIST_ID = "_id";
    public static final String COLUMN_ARTIST_NAME = "name";
    public static final int INDEX_ARTIST_ID = 1;
    public static final int INDEX_ARTIST_NAME = 2;

    public static final String TABLE_SONGS = "songs";
    public static final String COLUMN_SONG_ID = "_id";
    public static final String COLUMN_SONG_TRACK = "track";
    public static final String COLUMN_SONG_TITLE = "title";
    public static final String COLUMN_SONG_ALBUM = "album";
    public static final String COLUMN_SONG_LOC = "location";
    public static final String COLUMN_SONG_ALB_LOC = "album_location";
    public static final int INDEX_SONG_ID = 1;
    public static final int INDEX_SONG_TRACK = 2;
    public static final int INDEX_SONG_TITLE = 3;
    public static final int INDEX_SONG_ALBUM = 4;

    public static final int ORDER_BY_NONE = 1;
    public static final int ORDER_BY_ASC = 2;
    public static final int ORDER_BY_DESC = 3;

    public static final String QUERY_ALBUMS_BY_ARTISTS_START = "SELECT " + TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME + " FROM " + TABLE_ALBUMS + " INNER JOIN " + TABLE_ARTISTS+ " ON " + TABLE_ALBUMS + "." + COLUMN_ALBUM_ARTIST + " = " + TABLE_ARTISTS + "." + COLUMN_ARTIST_ID + " WHERE " + TABLE_ARTISTS + "." + COLUMN_ARTIST_NAME + " = \"";

    public static final String QUERY_ALBUMS_BY_ARTISTS_SORT = " ORDER BY " + TABLE_ALBUMS + "." + COLUMN_ALBUM_NAME + " COLLATE NOCASE ";

    public static final String INSERT_ARTIST = "INSERT INTO " + TABLE_ARTISTS + "(" + COLUMN_ARTIST_NAME + ")" + "VALUES(?)";
    public static final String INSERT_ALBUMS = "INSERT INTO " + TABLE_ALBUMS + "(" + COLUMN_ALBUM_NAME + ", " + COLUMN_ALBUM_ARTIST + ") VALUES(?, ?)";

    public static final String INSERT_SONGS = "INSERT INTO " + TABLE_SONGS + "(" + COLUMN_SONG_TRACK + ", " + COLUMN_SONG_TITLE + ", "+ COLUMN_SONG_ALBUM + ", " + COLUMN_SONG_LOC + ") VALUES(?,?,?,?)";

    public static final String QUERY_ARTIST = "SELECT " + COLUMN_ARTIST_ID + " FROM " + TABLE_ARTISTS + " WHERE " + COLUMN_ARTIST_NAME + " = ?";

    public static final String QUERY_ALBUM = "SELECT " + COLUMN_ALBUM_ID + " FROM " + TABLE_ALBUMS + " WHERE " + COLUMN_ALBUM_NAME + " = ?";

    public static final String QUERY_ALBUMS_BY_ARTIST_ID = "SELECT *FROM " + TABLE_ALBUMS + " WHERE " + COLUMN_ALBUM_ARTIST + " = ? ORDER BY " + COLUMN_ALBUM_NAME + " COLLATE NOCASE";

    public static final String UPDATE_ARTIST_NAME = "UPDATE " + TABLE_ARTISTS + " SET " + COLUMN_ARTIST_NAME + " = ? WHERE " + COLUMN_ARTIST_ID + " = ?";

    private Connection conn;
    private PreparedStatement queryAlbumsByArtistId;
    private PreparedStatement insertIntoArtists;
    private PreparedStatement insertIntoAlbums;
    private PreparedStatement insertIntoSongs;

    private PreparedStatement queryArtists;
    private PreparedStatement queryAlbums;
    private PreparedStatement updateArtistName;

    private static DataSource instance = new DataSource();

    public static DataSource getInstance(){
        return instance;
    }

    public boolean open(){
        try{
            conn = DriverManager.getConnection(CONNECTION_STRING);
            insertIntoArtists = conn.prepareStatement(INSERT_ARTIST, Statement.RETURN_GENERATED_KEYS);
            insertIntoAlbums = conn.prepareStatement(INSERT_ALBUMS, Statement.RETURN_GENERATED_KEYS);
            insertIntoSongs = conn.prepareStatement(INSERT_SONGS);
            queryArtists = conn.prepareStatement(QUERY_ARTIST);
            queryAlbums = conn.prepareStatement(QUERY_ALBUM);
            queryAlbumsByArtistId = conn.prepareStatement(QUERY_ALBUMS_BY_ARTIST_ID);
            updateArtistName = conn.prepareStatement(UPDATE_ARTIST_NAME);

            return true;
        }catch(SQLException e){
            System.out.println("Couldn't connect to the database: " + e.getMessage());
            return false;
        }
    }

    public void close(){
        try{
            if(insertIntoArtists != null){
                insertIntoArtists.close();
            }
            if(insertIntoAlbums != null){
                insertIntoAlbums.close();
            }
            if(insertIntoSongs != null){
                insertIntoSongs.close();
            }
            if(queryArtists != null){
                queryArtists.close();
            }
            if(queryAlbums != null){
                queryAlbums.close();
            }
            if(conn != null){
                conn.close();
            }
            if(queryAlbumsByArtistId != null){
                queryAlbumsByArtistId.close();
            }
            if(updateArtistName != null){
                updateArtistName.close();
            }
        }catch(SQLException e){
            System.out.println("Couldn't close connection: " + e.getMessage());
        }
    }

    public List<Song> querySong(int sortOrder){
        int i = 1;
        StringBuilder sb = new StringBuilder("SELECT *FROM ");
        sb.append(TABLE_SONGS);
        if(sortOrder != ORDER_BY_NONE){
            sb.append(" ORDER BY ");
            sb.append(COLUMN_SONG_TITLE);
            sb.append(" COLLATE NOCASE ");
            if(sortOrder == ORDER_BY_DESC){
                sb.append("DESC");
            }else{
                sb.append("ASC");
            }
        }

        try(Statement statement = conn.createStatement();
        ResultSet results = statement.executeQuery(sb.toString())){

            List<Song> songs = new ArrayList<>();
            while(results.next()){
                Song song = new Song();
                song.setIndex(i);
                song.setId(results.getInt(1));
                song.setTrack(results.getInt(2));
                song.setName(results.getString(3));
                song.setAlbumId(results.getInt(4));
                song.setLocation(results.getString(5));
                songs.add(song);
                i++;
            }
            return songs;
        }catch(SQLException e){
            System.out.println("Query Failed: " + sb.toString());
            return null;
        }

    }


    public List<Artist> queryArtist(int sortOrder){

        StringBuilder sb = new StringBuilder("SELECT *FROM ");
        sb.append(TABLE_ARTISTS);
        if(sortOrder != ORDER_BY_NONE){
            sb.append(" ORDER BY ");
            sb.append(COLUMN_ARTIST_ID);
            sb.append(" COLLATE NOCASE ");
            if(sortOrder == ORDER_BY_DESC){
                sb.append("DESC");
            }else{
                sb.append("ASC");
            }
        }

        try(Statement statement = conn.createStatement();
        ResultSet results = statement.executeQuery(sb.toString());){

            List<Artist> artists = new ArrayList<>();
            while(results.next()){
                Artist artist = new Artist();
                artist.setId(results.getInt(INDEX_ARTIST_ID));
                artist.setName(results.getString(INDEX_ARTIST_NAME));
                artists.add(artist);
            }

            return artists;
        }catch(SQLException e){
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }

    }

    public List<Album> queryAlbum(int sortOrder){
        int i = 1;
        StringBuilder sb = new StringBuilder("SELECT *FROM ");
        sb.append(TABLE_ALBUMS);
        if(sortOrder != ORDER_BY_NONE){
            sb.append(" ORDER BY ");
            sb.append(COLUMN_ARTIST_ID);
            sb.append(" COLLATE NOCASE ");
            if(sortOrder == ORDER_BY_DESC){
                sb.append("DESC");
            }else{
                sb.append("ASC");
            }
        }

        try(Statement statement = conn.createStatement();
            ResultSet results = statement.executeQuery(sb.toString());){

            List<Album> albums = new ArrayList<>();
            while(results.next()){
                Album album = new Album();
                album.setId(i);
                album.setName(results.getString(INDEX_ALBUM_NAME));
                albums.add(album);
                i++;
            }

            return albums;
        }catch(SQLException e){
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }

    }

    public List<Album> queryAlbumForAristId(int id){
        try{
           queryAlbumsByArtistId.setInt(1, id);
           ResultSet results = queryAlbumsByArtistId.executeQuery();

           List<Album> albums = new ArrayList<>();
           while(results.next()){
               Album album = new Album();
               album.setId(results.getInt(1));
               album.setName(results.getString(2));
               album.setArtistId(id);
               albums.add(album);
           }
           return albums;
        }catch(SQLException e){
            System.out.println("Query Failed: " + e.getMessage());
            return null;
        }
    }


    public List<String> queryAlbumsForArtist(String artistName, int sortOrder){
        StringBuilder sb = new StringBuilder(QUERY_ALBUMS_BY_ARTISTS_START);
        sb.append(artistName);
        sb.append("\"");

        if(sortOrder != ORDER_BY_NONE){
            sb.append(QUERY_ALBUMS_BY_ARTISTS_SORT);
            if(sortOrder == ORDER_BY_DESC){
                sb.append("DESC");
            }else{
                sb.append("ASC");
            }
        }

        System.out.println("SQL statement: " + sb.toString());

        try(Statement statement = conn.createStatement();
        ResultSet results = statement.executeQuery(sb.toString())){

            List<String> albums = new ArrayList<>();
            while(results.next()){
                albums.add(results.getString(1));
            }
            return albums;

        }catch(SQLException e){
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }
    }


    private int insertArtist(String name) throws SQLException{

        queryArtists.setString(1, name);
        ResultSet results = queryArtists.executeQuery();
        if(results.next()){
            return results.getInt(1);
        }else{
            // Insert Artists
            insertIntoArtists.setString(1, name);
            int affectedRows = insertIntoArtists.executeUpdate();

            if(affectedRows != 1){
                throw new SQLException("Couldn't insert artist!");
            }

            ResultSet generatedKeys = insertIntoArtists.getGeneratedKeys();
            if(generatedKeys.next()){
                return generatedKeys.getInt(1);
            }else{
                throw new SQLException("Couldn't get _id for artists.");
            }
        }
    }

    private int insertAlbum(String name, int artistId) throws SQLException{

        queryAlbums.setString(1, name);
        ResultSet results = queryAlbums.executeQuery();
        if(results.next()){
            return results.getInt(1);
        }else{
            // Insert Artists
            insertIntoAlbums.setString(1, name);
            insertIntoAlbums.setInt(2, artistId);
            int affectedRows = insertIntoAlbums.executeUpdate();

            if(affectedRows != 1){
                throw new SQLException("Couldn't insert Album!");
            }

            ResultSet generatedKeys = insertIntoAlbums.getGeneratedKeys();
            if(generatedKeys.next()){
                return generatedKeys.getInt(1);
            }else{
                throw new SQLException("Couldn't get _id for Album.");
            }
        }
    }

    public boolean updateArtistname(int id, String newName){
        try{
            updateArtistName.setString(1, newName);
            updateArtistName.setInt(2, id);
            int affectedRecords = updateArtistName.executeUpdate();

            return affectedRecords == 1;

        }catch(SQLException e){
            System.out.println("Query Failed: " + e.getMessage());
            return false;
        }
    }

    public void insertSong(String title, String artist, String album, int track, String location) {

        try {
            conn.setAutoCommit(false);
            int artistId = insertArtist(artist);
            int albumId = insertAlbum(album, artistId);
            insertIntoSongs.setInt(1, track);
            insertIntoSongs.setString(2, title);
            insertIntoSongs.setInt(3, albumId);
            insertIntoSongs.setString(4,location);
            int affectedRows = insertIntoSongs.executeUpdate();

            if (affectedRows == 1) {
                conn.commit();
            }else{
                throw new SQLException("The song insert Failed.");
            }
        } catch (Exception e) {
            System.out.println("Insert Song Exception: " + e.getMessage());
            try {
                System.out.println("Performing Rollback");
                conn.rollback();
            } catch (SQLException e2) {
                System.out.println("Oh boy! Something is really wrong !" + e2.getMessage());
            }
        } finally {
            try {
                System.out.println("Setting Default commit behaviour.");
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Couldn't reset auto-commit: " + e.getMessage());
            }
        }
    }
}

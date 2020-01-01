package com.doctor.blue.superstorm.providers

import android.content.Context
import android.os.AsyncTask
import android.provider.MediaStore
import android.util.Log
import com.doctor.blue.superstorm.model.Song
import java.io.IOException

class SongProvider() : AsyncTask<Void, Void, List<Song>>() {
    private lateinit var context: Context
    private val songs: MutableList<Song> = mutableListOf()
    lateinit var setSongList: (List<Song>) -> Unit

    constructor(context: Context, setSongList: (songs: List<Song>) -> Unit) : this() {
        this.context = context
        this.setSongList = setSongList
    }

    override fun doInBackground(vararg p0: Void?): List<Song> {
        getAllAudioFromDevice()
        return songs
    }

    override fun onPostExecute(result: List<Song>?) {
        super.onPostExecute(result)
        setSongList(songs)
    }

    @Throws(IOException::class)
    fun getAllAudioFromDevice() {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.ArtistColumns.ARTIST
        )
        val c = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        ) //MediaStore.Audio.Media.DATA + " like ? ", //new String[]{"%utm%"}, null);
        if (c != null) {
            while (c.moveToNext()) { // Create a model object.
                val song = Song()
                val path = c.getString(0) // Retrieve path.
                val name = c.getString(1) // Retrieve name.
                // val album = c.getString(2) // Retrieve album name.
                val artist = c.getString(3) // Retrieve artist name.
                // Set data to the model object.
                song.songName = name
                song.songArtist = artist
                song.songPath = path
                Log.e("Path :", "Artist :$path")
                Log.e("SONG: ",name)
                // Add the model object to the list .
                songs.add(song)
            }
            c.close()
        }
    }
}
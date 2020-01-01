package com.doctor.blue.superstorm.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.doctor.blue.superstorm.model.Song

class SongPlayer private constructor() {
    companion object {
        private var instance: SongPlayer? = null
        fun getInstance(): SongPlayer? {
            synchronized(lock = SongPlayer::class.java) {
                if (instance == null)
                    instance = SongPlayer()

            }
            return instance
        }
    }

    private lateinit var player: MediaPlayer
    var songs: List<Song> = listOf()
        set(value) {
            field = value
            setUrl()
        }
    lateinit var context: Context
    var currentSongPosition: Int = 0
    var isRepeatOne: Boolean = false
    private var isStart = false

    private fun setUrl() {
        val uri = Uri.parse(songs[currentSongPosition].songPath)
        player = MediaPlayer.create(context, uri)
    }

    fun start() {
        if (songs.isNotEmpty()) {
            setUrl()
            player.start()
            isStart = true
        }
    }

    fun pause() {
        player.pause()
    }

    fun play() {
        if (isStart) {
            if (songs.isNotEmpty()){
                val pos=player.currentPosition
                start()
                player.seekTo(pos)
            }
        } else {
            start()
        }
    }


    fun nextSong() {
       if (!isRepeatOne){
           currentSongPosition++
           if (currentSongPosition >= songs.size)
               currentSongPosition = 0
           stop()
       }

        start()
    }


    fun preSong() {
        if (currentSongPosition > 0) {
            currentSongPosition--
            stop()
            start()

        }
    }

    fun stop() {
        if (songs.isNotEmpty()) {
            player.stop()
            player.release()
        }
    }

    fun getCurrentSongName(): String {
        return songs[currentSongPosition].songName
    }

    fun getCurrentSongArtist(): String {
        return songs[currentSongPosition].songArtist
    }

    fun getDuration(): Int {
        return player.duration
    }

    fun getCurrentTime(): Int {
        return player.currentPosition
    }

    fun seekTo(pos: Int) {
        player.seekTo(pos)
    }

    fun isPlaying(): Boolean {
        return player.isPlaying
    }

}

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
        if (songs.isNotEmpty()) {
            val uri = Uri.parse(songs[currentSongPosition].songPath)
            player = MediaPlayer.create(context, uri)
        }
    }

    fun start() {
        if (songs.isNotEmpty()) {
            setUrl()
            player.start()
            isStart = true
        }
    }

    fun pause() {
        if (songs.isNotEmpty())
            player.pause()
    }

    fun play() {
        if (isStart) {
            if (songs.isNotEmpty()) {
                val pos = player.currentPosition
                start()
                player.seekTo(pos)
            }
        } else {
            start()
        }
    }


    fun nextSong() {
        currentSongPosition++
        if (currentSongPosition >= songs.size)
            currentSongPosition = 0
        stop()
        start()
    }
    fun repeat(){
        if (isRepeatOne){
            stop()
            start()
        }else
            nextSong()
    }


    fun preSong() {
        if (currentSongPosition > 0) {
            currentSongPosition--
        } else
            currentSongPosition = songs.size - 1
        stop()
        start()
    }

    fun stop() {
        if (songs.isNotEmpty()) {
            player.stop()
            player.release()
        }
    }

    fun getCurrentSongName(): String {
        return if (songs.isEmpty()) "Hello world" else songs[currentSongPosition].songName
    }

    fun getCurrentSongArtist(): String {
        return if (songs.isEmpty()) "Unknown" else songs[currentSongPosition].songArtist
    }

    fun getDuration(): Int {
        return if (songs.isEmpty()) 0 else player.duration
    }

    fun getCurrentTime(): Int {
        return if (songs.isEmpty()) 0 else player.currentPosition
    }

    fun seekTo(pos: Int) {
        if (songs.isNotEmpty())
            player.seekTo(pos)
    }

    fun isPlaying(): Boolean {
        return if (songs.isEmpty()) false else player.isPlaying
    }

    fun getLocationToPlacePhotos(num: Int): Int {
        var position = currentSongPosition + num
        if (position >= songs.size || position < 0) position = songs.size - 2
        return position
    }

}

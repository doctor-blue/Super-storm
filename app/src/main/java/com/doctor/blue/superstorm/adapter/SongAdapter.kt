package com.doctor.blue.superstorm.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.doctor.blue.superstorm.R
import com.doctor.blue.superstorm.model.Song

class SongAdapter(
    private val context: Context,
    private val itemClick: (Int) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongHolder>() {

    var songs: List<Song> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false)
        return SongHolder(view, itemClick)
    }

    override fun getItemCount(): Int {
        return songs.count()
    }

    override fun onBindViewHolder(holder: SongHolder, position: Int) {
        holder.bindSong(songs[position],position)
    }

    inner class SongHolder(itemView: View, val itemClick: (Int) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val txtSongName: TextView = itemView.findViewById(R.id.txt_song_name)
        private val txtSongArtist: TextView = itemView.findViewById(R.id.txt_song_artist)
        fun bindSong(song: Song,position: Int) {
            txtSongName.text = song.songName
            txtSongArtist.text = song.songArtist
            itemView.setOnClickListener { this.itemClick(position) }
        }
    }

}
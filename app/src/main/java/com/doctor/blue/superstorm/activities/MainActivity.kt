package com.doctor.blue.superstorm.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.doctor.blue.superstorm.R
import com.doctor.blue.superstorm.adapter.SongAdapter
import com.doctor.blue.superstorm.base.BaseActivity
import com.doctor.blue.superstorm.model.Song
import com.doctor.blue.superstorm.providers.SongProvider
import com.doctor.blue.superstorm.utils.SongPlayer
import com.doctor.blue.superstorm.utils.TimeUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_player.*
import java.util.*
import kotlin.concurrent.timerTask


class MainActivity : BaseActivity() {
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var songs: List<Song>
    private lateinit var songAdapter: SongAdapter
    private var timer: Timer = Timer()
    private val songPlayer = SongPlayer.getInstance()
    private var duration = 0


    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun init(savedInstanceState: Bundle?) {
        initMain()
        initSeekBar()
        initBottomSheet()
    }

    private fun initMain() {
        bottomSheetBehavior = BottomSheetBehavior.from(layout_player)
        songPlayer!!.context = this

        songAdapter = SongAdapter(this) {
            songPlayer.currentSongPosition = it
            timer.cancel()
            setSongInformation()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            songPlayer.stop()
            songPlayer.start()

        }
        //Get songs from device
        SongProvider(this, setSongList).execute()


        val layoutManager = LinearLayoutManager(this)
        rcy_song.layoutManager = layoutManager
        rcy_song.setHasFixedSize(true)

        val dividerItemDecoration =
            DividerItemDecoration(rcy_song.context, layoutManager.orientation)
        rcy_song.addItemDecoration(dividerItemDecoration)

        rcy_song.adapter = songAdapter
    }

    private val setSongList: (songs: List<Song>) -> Unit = {
        songs = it
        songAdapter.songs = songs
        songPlayer!!.songs = songs
        setSongInformation()
        startUpdateProgressSong()
    }

    private fun initSeekBar() {
        seek_play_progress.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                songPlayer!!.seekTo(TimeUtils.formatSongProgress(seekBar.progress * 1.0 / 100 * duration))

            }
        })
    }

    private fun initBottomSheet() {
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        rcy_song.visibility = View.VISIBLE
                        timer.cancel()
                        songPlayer!!.pause()
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        rcy_song.visibility = View.GONE
                        txt_title_peek.visibility = View.GONE
                        btn_close_sheet.setImageResource(R.drawable.ic_expand_less)
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        rcy_song.visibility = View.VISIBLE
                        txt_title_peek.visibility = View.VISIBLE
                        btn_close_sheet.setImageResource(R.drawable.ic_expand_more)
                    }

                }
            }

        })
    }

    private fun setSongInformation() {
        txt_current_song_artist.text = songPlayer!!.getCurrentSongArtist()
        txt_current_song_name.text = songPlayer.getCurrentSongName()
        txt_title_peek.text = songPlayer.getCurrentSongName()

        btn_play.setImageResource(R.drawable.ic_play)
    }

    private fun startUpdateProgressSong() {
        timer = Timer()
        timer.schedule(timerTask {
            runOnUiThread {
                val currentTimePosition = songPlayer!!.getCurrentTime()
                duration = songPlayer.getDuration()

                seek_play_progress.max = 100
                txt_song_duration.text = TimeUtils.formateMilliSeccond(duration.toLong())
                txt_progress_song.text = TimeUtils.formateMilliSeccond(currentTimePosition.toLong())

                seek_play_progress.progress =
                    TimeUtils.formatSongProgress(currentTimePosition * 1.0 / duration * 100)
                if (duration - currentTimePosition < 1000) {
                    songPlayer.nextSong()
                    setSongInformation()
                }
                if (songPlayer.isPlaying()) {
                    btn_play.setImageResource(R.drawable.ic_pause)
                } else {
                    btn_play.setImageResource(R.drawable.ic_play)
                }
            }
        }, 0, 500)
    }

    fun nextSong(view: View) {
        songPlayer!!.nextSong()
        setSongInformation()
    }

    fun playCurrentSong(view: View) {
        if (songPlayer!!.isPlaying()) {
            btn_play.setImageResource(R.drawable.ic_play)
            songPlayer.pause()
        } else {
            btn_play.setImageResource(R.drawable.ic_pause)
            timer.cancel()
            startUpdateProgressSong()
            songPlayer.play()
        }
    }

    fun setRepeat(view: View) {
        if (songPlayer!!.isRepeatOne) {
            songPlayer.isRepeatOne = false
            btn_repeat.setImageResource(R.drawable.ic_repeat)
        } else {
            songPlayer.isRepeatOne = true
            btn_repeat.setImageResource(R.drawable.ic_repeat_one)
        }
    }

    fun preSong(view: View) {
        songPlayer!!.preSong()
        setSongInformation()
    }

}

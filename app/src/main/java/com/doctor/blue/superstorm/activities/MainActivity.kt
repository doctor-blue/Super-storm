package com.doctor.blue.superstorm.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.doctor.blue.superstorm.R
import com.doctor.blue.superstorm.adapter.SongAdapter
import com.doctor.blue.superstorm.base.BaseActivity
import com.doctor.blue.superstorm.model.Song
import com.doctor.blue.superstorm.providers.ImageProvider
import com.doctor.blue.superstorm.providers.SongProvider
import com.doctor.blue.superstorm.utils.SongPlayer
import com.doctor.blue.superstorm.utils.TimeUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_player.*
import java.util.*
import kotlin.concurrent.timerTask


class MainActivity : BaseActivity() {
    private val READ_EXTERNAL_STORAGE_PERMISSION_CODE = 123
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
        addSeekBarEvent()
        addBottomSheetEvent()
    }

    private fun initMain() {
        bottomSheetBehavior = BottomSheetBehavior.from(layout_player)
        songPlayer!!.context = this

        songAdapter = SongAdapter(this) {
            songPlayer.currentSongPosition = it
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            songPlayer.stop()
            songPlayer.start()
            setSongInformation()
            startNextAnimation()
        }

        //check permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //Get songs from device
            SongProvider(this, setSongList).execute()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_PERMISSION_CODE
            )
        }


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
        if (songs.isNotEmpty()) {
            startUpdateProgressSong()
            loadImage(0, img_top_card)
            loadImage(0, img_middle_card)
            loadImage(songs.size - 1, img_bottom_card)
        }
        runAnimation()
    }

    private fun addSeekBarEvent() {
        seek_play_progress.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (duration != 0)
                    songPlayer!!.seekTo(TimeUtils.formatSongProgress(seekBar.progress * 1.0 / 100 * duration))

            }
        })
    }

    private fun addBottomSheetEvent() {
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        rcy_song.visibility = View.VISIBLE
                        songPlayer!!.pause()
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        rcy_song.visibility = View.GONE
                        txt_title_peek.visibility = View.GONE
                        btn_close_sheet.setImageResource(R.drawable.ic_expand_less)
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        rcy_song.visibility = View.VISIBLE
                        runAnimation()
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
                    if (duration == 0) 0 else TimeUtils.formatSongProgress(currentTimePosition * 1.0 / duration * 100)


                if (duration - currentTimePosition < 1000) {
                    songPlayer.repeat()
                    if (!songPlayer.isRepeatOne)
                        startNextAnimation()
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
        startNextAnimation()
    }

    fun playCurrentSong(view: View) {
        if (songPlayer!!.isPlaying()) {
            btn_play.setImageResource(R.drawable.ic_play)
            songPlayer.pause()
        } else {
            btn_play.setImageResource(R.drawable.ic_pause)
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
        startPreAnimation()
    }

    private fun startPreAnimation() {
        when (root.currentState) {
            R.id.fanOut -> {
                root.setTransition(R.id.fanOut, R.id.topCardOnTop)
                root.transitionToEnd()
                collapsedCardCompletedListener(R.id.middleCardOnTop)
            }
            R.id.topCardOnTop -> {
                root.setTransition(R.id.topCardOnTop, R.id.bottomCardOnTop)
                root.transitionToEnd()
                if (songs.isNotEmpty())
                    loadImage(songPlayer?.getLocationToPlacePhotos(0)!!, img_bottom_card)
            }
            R.id.middleCardOnTop -> {
                root.setTransition(R.id.middleCardOnTop, R.id.topCardOnTop)
                root.transitionToEnd()
                if (songs.isNotEmpty())
                    loadImage(songPlayer?.getLocationToPlacePhotos(0)!!, img_top_card)
            }
            R.id.bottomCardOnTop -> {
                root.setTransition(R.id.bottomCardOnTop, R.id.middleCardOnTop)
                root.transitionToEnd()
                if (songs.isNotEmpty())
                    loadImage(songPlayer?.getLocationToPlacePhotos(0)!!, img_middle_card)
            }
        }
    }

    private fun startNextAnimation() {
        when (root.currentState) {
            R.id.fanOut -> {
                root.setTransition(R.id.fanOut, R.id.topCardOnTop)
                root.transitionToEnd()
                collapsedCardCompletedListener(R.id.middleCardOnTop)
                if (songs.isNotEmpty())
                    loadImage(songPlayer?.getLocationToPlacePhotos(0)!!, img_middle_card)
            }
            R.id.topCardOnTop -> {
                root.setTransition(R.id.topCardOnTop, R.id.middleCardOnTop)
                root.transitionToEnd()
                if (songs.isNotEmpty())
                    loadImage(songPlayer?.getLocationToPlacePhotos(0)!!, img_middle_card)

            }
            R.id.middleCardOnTop -> {
                root.setTransition(R.id.middleCardOnTop, R.id.bottomCardOnTop)
                root.transitionToEnd()
                if (songs.isNotEmpty())
                    loadImage(songPlayer?.getLocationToPlacePhotos(0)!!, img_bottom_card)
            }
            R.id.bottomCardOnTop -> {
                root.setTransition(R.id.bottomCardOnTop, R.id.topCardOnTop)
                root.transitionToEnd()
                if (songs.isNotEmpty())
                    loadImage(songPlayer?.getLocationToPlacePhotos(0)!!, img_top_card)
            }
        }
    }

    fun expandBottomSheet(view: View) {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun collapsedCardCompletedListener(id: Int) {
        root.setTransitionListener(object : TransitionAdapter() {

            override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                if (currentId == R.id.topCardOnTop) {
                    root.setTransition(R.id.topCardOnTop, id)
                    root.transitionToEnd()
                }
                root.setTransitionListener(null)
            }
        })
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults.size >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun loadImage(pos: Int, img: ImageView) {
        Glide
            .with(this)
            .load(ImageProvider(this).execute(songs[pos].songPath).get())
            .error(R.drawable.img_default)
            .centerCrop()
            .placeholder(R.drawable.img_default)
            .into(img)
    }
    private fun runAnimation() {
        val controller: LayoutAnimationController =
            AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_slide_from_bottom)
        rcy_song.layoutAnimation = controller
        songAdapter.notifyDataSetChanged()
        rcy_song.scheduleLayoutAnimation()
    }
}

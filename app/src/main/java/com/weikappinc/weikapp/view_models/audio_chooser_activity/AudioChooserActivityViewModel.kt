package com.weikappinc.weikapp.view_models.audio_chooser_activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weikappinc.weikapp.adapters.audio_chooser_activity.AudioItemPreview
import com.weikappinc.weikapp.components.AudioPlayerManager
import com.weikappinc.weikapp.data.ioThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class AudioChooserActivityViewModel(private val audioFileRecolector: AudioFileRecolector) : ViewModel() {

    private var list: MutableList<AudioItemPreview> = mutableListOf()
    private var copyOfList: MutableList<AudioItemPreview> = mutableListOf()

    private var searchTermSubject: PublishSubject<String>? = null

    val alarmItemPreviewLiveData: MutableLiveData<MutableList<AudioItemPreview>> = MutableLiveData()

    private val disposables: CompositeDisposable = CompositeDisposable()

    private var mPlayer: AudioPlayerManager = AudioPlayerManager()

    private var audioChecked: AudioItemPreview? = null

    private var audioIdPlaying = ""

    var isSearchBarOpen = false
    var lastSearchTerm: String? = null

    fun setSearchingFunctionality() {
        createPublishSubject()
        createCopyOfList()
        observeSearchingTerms()
    }

    private fun createPublishSubject() {
        searchTermSubject = PublishSubject.create()
    }

    private fun createCopyOfList() {
        ioThread {
            copyOfList.clear()
            copyOfList = list.toMutableList()
        }
    }

    private fun fillListFromTheCopy() {
        ioThread {
            list.clear()
            list = copyOfList.toMutableList()

            flushAudioItemPreviewListAsync()
        }
    }

    private fun observeSearchingTerms() {
        searchTermSubject?.run {
            disposables.add(
                this
                .debounce(500, TimeUnit.MILLISECONDS)
                .skip(1)
                .subscribe({
                    if(it != lastSearchTerm && !(it == "" && lastSearchTerm == null)) {
                        stopAndReleaseAudio()

                        audioIdPlaying = ""

                        lastSearchTerm = it

                        list.clear()

                        for (audio in copyOfList) {
                            if(audio.name.toLowerCase().contains(it.toLowerCase())) {
                                audio.isPlaying = false
                                list.add(audio)
                            }
                        }

                        flushAudioItemPreviewListAsync()
                    }
                }, {

                }, {
                    fillListFromTheCopy()
                })
            )
        }
    }

    fun onNewSearchTerm(term: String) {
        if(term == "-1") {
            searchTermSubject?.onComplete()
            disposables.clear()
        } else {
            searchTermSubject?.onNext(term)
        }
    }

    fun loadAudioItemPreviewList() {
        list = audioFileRecolector.getAudioItems()

        setAudioItemPreviewListState()

        flushAudioItemPreviewList()
    }

    private fun flushAudioItemPreviewList() {
        alarmItemPreviewLiveData.value = list
    }

    private fun flushAudioItemPreviewListAsync() {
        alarmItemPreviewLiveData.postValue(list)
    }

    fun setAudioChecked(audio: AudioItemPreview?) {
        audioChecked = audio

        setAudioItemPrewviewListCheckedState()

        flushAudioItemPreviewList()
    }

    fun getAudioChecked() = audioChecked

    fun setAudioPlaying(id: String, path: String?) {
        if (id.isNotEmpty()) {
            startAudio(path!!)
        } else {
            stopAndReleaseAudio()
        }

        audioIdPlaying = id

        setAudioItemPrewviewListPlayingState()

        flushAudioItemPreviewList()
    }

    private fun startAudio(audioPath: String) {
        mPlayer.start(audioPath)

        mPlayer.player?.setOnCompletionListener {
            setAudioPlaying("", null)
        }
    }

    private fun stopAndReleaseAudio() {
        mPlayer.stop()
        mPlayer.release()
    }

    private fun setAudioItemPreviewListState() {
        setAudioItemPrewviewListCheckedState()
        setAudioItemPrewviewListPlayingState()
    }

    private fun setAudioItemPrewviewListCheckedState() {
        list.forEach { it.isChecked = it.id == audioChecked?.id }
    }

    private fun setAudioItemPrewviewListPlayingState() {
        list.forEach { it.isPlaying = it.id == audioIdPlaying }
    }

    override fun onCleared() {
        super.onCleared()

        disposables.dispose()
    }
}

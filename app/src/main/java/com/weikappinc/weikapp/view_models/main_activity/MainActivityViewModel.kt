package com.weikappinc.weikapp.view_models.main_activity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.weikappinc.weikapp.data.entities.Alarm
import com.weikappinc.weikapp.data_view_models.MessageItemModel
import com.weikappinc.weikapp.repositories.AlarmRepository
import io.reactivex.disposables.CompositeDisposable

data class AlarmsFragmentState (val list: MutableList<Alarm>?, val errors: Error?, val loading: Boolean)

private const val TAG = "MainActivityViewModel"

class MainActivityViewModel(private val alarmRepository: AlarmRepository) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val alarmState: MutableLiveData<AlarmsFragmentState> = MutableLiveData()

    init {
        //alarmList = LiveDataReactiveStreams.fromPublisher(observable as Publisher<MutableList<AlarmItemModel>>)
    }

    fun getAlarms(): MutableLiveData<AlarmsFragmentState> {
        alarmState.postValue(AlarmsFragmentState(null, null, true))

        val observable = alarmRepository.getFlowableAlarms()

        disposables.add(
            observable.subscribe({
                alarmState.postValue(AlarmsFragmentState(it, null, false))
            },

            {
                it.printStackTrace()
                Log.e(TAG, it.message)
                alarmState.postValue(AlarmsFragmentState(null, Error("No se han podido obtener las alarmas"), false))
            })
        )

        return alarmState
    }

    fun updateAlarms(vararg alarms: Alarm) {
        alarmRepository.updateAlarms(*alarms)
    }

    val messageList = MutableLiveData<MutableList<MessageItemModel>>(
        mutableListOf(
            MessageItemModel("Alberto", "Hola qué tal?", "20 feb.", "https://upload.wikimedia.org/wikipedia/commons/3/39/Official_portrait_of_Alberto_Costa_crop_2.jpg"),
            MessageItemModel("Marta", "Buenos días me alegro mucho de haberte conocido", "21 feb.", "https://cstpv.wp.st-andrews.ac.uk/files/2018/10/20180921_173308.jpg"),
            MessageItemModel("Ana", "Me ha encantado la frase que me has dedicado. Gracias.", "20 feb.", "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/cenicienta-disney-1543333968.jpg?crop=1xw:1xh;center,top&resize=480:*"),
            MessageItemModel("Jorge", "Mañana nos vemos. Buenos noches", "10 feb.", "https://www.sanger.ac.uk/sites/default/files/userpics/picture-jdlr.jpg"),
            MessageItemModel("Ramón", "Adios", "05 feb.", "https://pbs.twimg.com/profile_images/554680375922814976/HsNvDbS8_400x400.jpeg"),
            MessageItemModel("Sergio", "Hola como estas? espero que bien ya me contarás", "20 abr.", "https://i.imgur.com/DvpvklR.png"),
            MessageItemModel("Carlos", "Hola qué tal?", "10 mar.", "https://www.mccormick.northwestern.edu/images/research-and-faculty/directory/nocedal-jorge.jpg"),
            MessageItemModel("Pedro", "Hola qué tal?", "19 feb.", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQHSpZmzg13VFip2i0MxMxSPFQ1asLYae4IuU3Qtoi5Im3EppYx"),
            MessageItemModel("Claudio", "Hola qué tal?", "20 feb.", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/71/Premios_Goya_2018_-_Jorge_Drexler.jpg/245px-Premios_Goya_2018_-_Jorge_Drexler.jpg"),
            MessageItemModel("Sofía", "Hola qué tal?", "20 feb.", "https://frostsnow.com/uploads/gossip/2018/01/04/xhow-much-money-has-actor-jorge-garcia-estimated-for-his-net-worth-awards-career-and-source.jpg.pagespeed.ic.ZXkSGiBKgJ.jpg"),
            MessageItemModel("Paco", "Hola qué tal?", "20 feb.", "https://images.tmz.com/2018/09/07/090718-jorge-nava-primary-1200x630.jpg"),
            MessageItemModel("Miguel", "Hola qué tal?", "20 feb.", "https://i0.web.de/image/400/30288400,pd=1/jorge-gonz-lez.jpg"),
            MessageItemModel("Arnaldo", "Hola qué tal?", "20 feb.", "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/jorge-fernandez-home-1533730709.jpg"),
            MessageItemModel("Rosendo", "Hola qué tal?", "20 feb.", "https://i.imgur.com/DvpvklR.png"),
            MessageItemModel("Andrea", "Hola qué tal?", "20 feb.", "https://yt3.ggpht.com/a-/AAuE7mAm629mtQUFywpIEMYzl4bvTpmaKvQoBcruJA=s900-mo-c-c0xffffffff-rj-k-no"),
            MessageItemModel("Ovidiu", "Hola qué tal?", "20 feb.", "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0e/CSKA-RM18_%285%29.jpg/245px-CSKA-RM18_%285%29.jpg"),
            MessageItemModel("Javier Martínez", "Hola qué tal?", "20 feb.", "https://i.imgur.com/DvpvklR.png"),
            MessageItemModel("Mario Alberto", "Hola qué tal?", "20 feb.", "https://i.imgur.com/DvpvklR.png"),
            MessageItemModel("Cañizares", "Hola qué tal?", "20 feb.", "https://i.imgur.com/DvpvklR.png"),
            MessageItemModel("Casillas", "Hola qué tal?", "20 feb.", "https://i.imgur.com/DvpvklR.png"),
            MessageItemModel("Figo", "Hola qué tal?", "20 feb.", "https://i.imgur.com/DvpvklR.png"),
            MessageItemModel("Eltiolavarra", "Hola qué tal?", "20 feb.", "https://i.imgur.com/DvpvklR.png"),
            MessageItemModel("Alberto", "Hola qué tal?", "20 feb.", "https://i.imgur.com/DvpvklR.png"),
            MessageItemModel("Alberto", "Hola qué tal?", "20 feb.", "https://i.imgur.com/DvpvklR.png"),
            MessageItemModel("Alberto", "Hola qué tal?", "20 feb.", "https://i.imgur.com/DvpvklR.png")
        )
    )

    val numberOfNewMessages = MutableLiveData(5)

    fun newMessagesSeen() {
        numberOfNewMessages.postValue(0)
    }

    override fun onCleared() {
        super.onCleared()

        disposables.dispose()
    }
}
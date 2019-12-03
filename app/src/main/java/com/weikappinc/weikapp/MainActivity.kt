package com.weikappinc.weikapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.jakewharton.rxbinding3.material.itemSelections
import com.weikappinc.weikapp.fragments.AlarmsFragment
import com.weikappinc.weikapp.fragments.MessagesFragment
import com.weikappinc.weikapp.fragments.SettingsFragment
import com.weikappinc.weikapp.fragments.SocialFragment
import com.weikappinc.weikapp.view_models.main_activity.MainActivityViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_notification_badge.*
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

const val TAG = "MainActivity"
const val ITEM_ID_SELECTED_STATE = "ItemIdSelected"

const val TAG_ALARMS_FRAGMENT = "1"
const val TAG_SOCIAL_FRAGMENT = "2"
const val TAG_MESSAGES_FRAGMENT = "3"
const val TAG_SETTINGS_FRAGMENT = "4"

class MainActivity : AppCompatActivity() {
    private val fm: FragmentManager = supportFragmentManager
    private lateinit var activeFragment: Fragment

    // @byDelegates.notNull() is telling to the compiler that the property will be initialized when it is accessed
    // Para poder usar itemIdSelected primero hay que inicializarlo con un valor
    private var itemIdSelected: Int by Delegates.notNull()

    private val viewModel: MainActivityViewModel by viewModel()

    //private val viewModel: MainActivityViewModel by lazy { MainActivityViewModel.create(this) }

    private val disposables = CompositeDisposable()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*ioThread {
            val db = AppDatabase.getAppDatabase(this.applicationContext)

            val alarmDao = db?.alarmDao()
            val currentUserData = db?.currentUserData()

            alarmDao?.insert(Alarm("06:30", "Alarma para ir al trabajo", "Lunes 12 abr.", true, currentUserData?.getUserId() ?: "UPS!"))
        }*/

        /*disposables.add(
            Observable.fromCallable {

            }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
        )*/

        // Sólo inicializamos los fragmentos cuando es la primera vez que la actividad se inicia. De esta manera evitamos
        // inicializar los fragmentos varias veces cada vez que la orientación de la pantalla cambia
        if (savedInstanceState == null) loadFragments()

        setBottomNavigationView(savedInstanceState)

        setMainContentState()

        observeData()
    }

    private fun loadFragments() {
        loadFragmentIntoBackground(SettingsFragment(), TAG_SETTINGS_FRAGMENT)
        loadFragmentIntoBackground(MessagesFragment(), TAG_MESSAGES_FRAGMENT)
        loadFragmentIntoBackground(SocialFragment(), TAG_SOCIAL_FRAGMENT)

        activeFragment = AlarmsFragment()

        fm.beginTransaction().add(R.id.main_container, activeFragment, TAG_ALARMS_FRAGMENT).commit()
    }

    private fun setBottomNavigationView(savedInstanceState: Bundle?) {
        itemIdSelected = savedInstanceState?.getInt(ITEM_ID_SELECTED_STATE) ?: R.id.action_alarms

        botNavView.selectedItemId = itemIdSelected
    }

    /*
    * Cada vez que se pusla sobre uno de los botones se llama este método
    * y se cambia el estado de la actividad, así como el título de la barra
    * superior y el fragment de content main
    * */
    private fun setMainContentState() {
        when (itemIdSelected) {
            R.id.action_alarms -> setFragmentOnNavigationChange(fm.findFragmentByTag(TAG_ALARMS_FRAGMENT))

            R.id.action_social -> setFragmentOnNavigationChange(fm.findFragmentByTag(TAG_SOCIAL_FRAGMENT))

            R.id.action_messages -> {
                setFragmentOnNavigationChange(fm.findFragmentByTag(TAG_MESSAGES_FRAGMENT))

                hideMessageNotification()
            }

            R.id.action_settings -> setFragmentOnNavigationChange(fm.findFragmentByTag(TAG_SETTINGS_FRAGMENT))
        }
    }

    private fun observeData() {
        viewModel.numberOfNewMessages.observe(this, Observer {
            showMessageNotification(it)
        })
    }

    private fun showMessageNotification(numberOfNewMessage: Int) {
        if(numberOfNewMessage > 0) {
            val bottomNavigationMenuView = botNavView.getChildAt(0) as BottomNavigationMenuView
            val v = bottomNavigationMenuView.getChildAt(2)
            val itemView = v as BottomNavigationItemView

            LayoutInflater.from(this).inflate(R.layout.view_notification_badge, itemView, true)

            notifications_badge?.text = numberOfNewMessage.toString()
        }
    }

    private fun hideMessageNotification() {
        notifications_badge?.setVisibleOrGone(false)

        viewModel.newMessagesSeen()
    }

    /*
    * @fragment: Uno de los 4 fragments (Alarms, Social, Messages, Settings)
    * Oculta el fragment activo y lo sustituye por el que se ha pulsado
    * */
    private fun setFragmentOnNavigationChange(fragment: Fragment?) {
        fragment?.let {
            // Si activeFragment está inicializado se procede a ocultarlo y se muestra el nuevo fragment
            // El nuevo fragment pasa a ser el activeFragment
            if(::activeFragment.isInitialized) {
                fm.beginTransaction().hide(activeFragment).show(it).commit()
                activeFragment = it

                // Si por el contrario resulta que el activeFragment no está inicializado significa que se ha
                // producido un cambio de orientación y no se ha podido llamar al método loadFragments() el cual inicializa
                // el activeFragment
            } else {
                activeFragment = it
                fm.beginTransaction().show(activeFragment).commit()
            }
        }
    }

    /*
    * @fragment: El fragmento que se va a iniciar y ocultar en segundo plano
    * @positionTag: El nombre del fragmento que puede ser cualquier string
    * */
    private fun loadFragmentIntoBackground(fragment: Fragment, positionTag: String) {
        fm.beginTransaction().add(R.id.main_container, fragment, positionTag).hide(fragment).commit()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        /*Save the current state of the bottom navigation so that the next time the activity is created the title
        and the item selected maintain each other syncronized*/
        outState?.putInt(ITEM_ID_SELECTED_STATE, botNavView.selectedItemId)
    }

    override fun onStart() {
        super.onStart()

        disposables.add(
            botNavView.itemSelections()
                .skip(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if(itemIdSelected != it.itemId) {
                        itemIdSelected = it.itemId
                        setMainContentState()
                    }
                }
        )
    }

    // Sólo nos salimos de la aplicación cuando se pusla el back button y nos encontramos en la pantalla Alarmas
    // Si nos encontrasemos en otra pantalla, navegariamos directamente a la pantalla Alarmas y no nos saldríamos de la
    // aplicación
    override fun onBackPressed() {
        if(itemIdSelected == R.id.action_alarms) {
            super.onBackPressed()
        } else {
            itemIdSelected = R.id.action_alarms
            setMainContentState()
            botNavView.selectedItemId = itemIdSelected
        }
    }

    override fun onStop() {
        super.onStop()

        disposables.clear()
    }
}

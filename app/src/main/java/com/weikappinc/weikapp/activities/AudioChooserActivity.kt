package com.weikappinc.weikapp.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.EditText
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.weikappinc.weikapp.BaseActivity
import com.weikappinc.weikapp.R
import com.weikappinc.weikapp.adapters.audio_chooser_activity.AudioListAdapter
import com.weikappinc.weikapp.components.PrimaryActionModeCallback
import com.weikappinc.weikapp.databinding.ActivityAudioChooserBinding
import com.weikappinc.weikapp.eventbus.events.audio_chooser_activity.*
import com.weikappinc.weikapp.view_models.audio_chooser_activity.AudioChooserActivityViewModel
import kotlinx.android.synthetic.main.activity_audio_chooser.*
import kotlinx.android.synthetic.main.toolbar_close_title_ok.toolbar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.viewmodel.ext.android.viewModel

const val RESULT_AUDIO_PATH = "AudioPath"

class AudioChooserActivity : BaseActivity() {

    private lateinit var mBinding: ActivityAudioChooserBinding

    private val viewModel: AudioChooserActivityViewModel by viewModel()

    private var mActionMode: ActionMode? = null

    override fun bindLayout() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_audio_chooser)
    }

    override fun init(savedInstanceState: Bundle?) {
        mBinding.title = "SELECCIONA UN AUDIO"

        recyclerViewAudioList.apply {
            layoutManager = LinearLayoutManager(this@AudioChooserActivity)
            adapter = AudioListAdapter()
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }

        viewModel.alarmItemPreviewLiveData.observe(this@AudioChooserActivity, Observer {
            (recyclerViewAudioList.adapter as AudioListAdapter).updateListOfAudios(it)
        })

        if(viewModel.getAudioChecked() != null) {
            if(mActionMode == null) {
                mActionMode = startSupportActionMode(PrimaryActionModeCallback())
            }

            mActionMode?.title = viewModel.getAudioChecked()!!.name
        }
    }

    override fun onResume() {
        super.onResume()

        if(!viewModel.isSearchBarOpen) {
            viewModel.loadAudioItemPreviewList()
        }
    }

    override fun onStart() {
        super.onStart()

        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()

        EventBus.getDefault().unregister(this)

        if(!isChangingConfigurations) {
            viewModel.setAudioPlaying("", null)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAudioPlay(event: AudioPlayEvent) {
        viewModel.setAudioPlaying(event.audio.id, event.audio.path)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAudioStop(event: AudioStopEvent) {
        viewModel.setAudioPlaying("", null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAudioChecked(event: AudioCheckedEvent) {
        Handler().postDelayed({
            viewModel.setAudioChecked(event.audio)
        }, 50)

        if(mActionMode == null) {
            mActionMode = startSupportActionMode(PrimaryActionModeCallback())
        }

        mActionMode?.title = event.audio.name
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAudioSelected(event: AudioSelectedEvent) {
       val resultIntent = Intent()

        resultIntent.putExtra(RESULT_AUDIO_PATH, viewModel.getAudioChecked()?.path ?: "Path igual a null")

        setResult(Activity.RESULT_OK, resultIntent)

        finish()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAudioFinishSelected(event: AudioCancelSelectedEvent) {
        viewModel.setAudioChecked(null)

        mActionMode = null
    }

    override fun getHomeIcon(): Int = R.drawable.ic_arrow_back_white_24dp

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(getMenuLayout(), menu)

        val searchView = (menu?.findItem(R.id.action_search_audio)?.actionView as SearchView)

        searchView.queryHint = "Buscar audio..."

        searchView.maxWidth = Integer.MAX_VALUE

        searchView.setIconifiedByDefault(true)

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { return true }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onNewSearchTerm(newText ?: "")

                return true
            }
        })

        val editText: EditText = searchView.findViewById(R.id.search_src_text)
        editText.setHintTextColor(ContextCompat.getColor(this@AudioChooserActivity, R.color.colorSlightlyWhite))

        val menuItem = menu.findItem(R.id.action_search_audio)

        if(viewModel.isSearchBarOpen) {
            menuItem.expandActionView()

            animateSearchToolbar(1, containsOverflow = true, show = true)

            editText.setText(viewModel.lastSearchTerm)

            editText.setSelection(editText.text.length)
        }

        menuItem.setOnActionExpandListener(object: MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                viewModel.setSearchingFunctionality()

                viewModel.isSearchBarOpen = true

                animateSearchToolbar(1, containsOverflow = true, show = true)

                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                if(menuItem.isActionViewExpanded) {
                    animateSearchToolbar(1, containsOverflow = false, show = false)
                }

                viewModel.isSearchBarOpen = false

                viewModel.onNewSearchTerm("-1")

                return true
            }
        })

        return true
    }

    @SuppressLint("PrivateResource")
    fun animateSearchToolbar(numberOfMenuIcon: Int, containsOverflow: Boolean, show: Boolean) {

        toolbar.setBackgroundColor(ContextCompat.getColor(this@AudioChooserActivity, R.color.colorAccent))

        if (show) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val width = toolbar.width -
                        (if (containsOverflow) resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) else 0) -
                        resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon / 2
                val createCircularReveal = ViewAnimationUtils.createCircularReveal(
                    toolbar,
                    if (isRtl(resources)) toolbar.width - width else width,
                    toolbar.height / 2,
                    0.0f,
                    width.toFloat()
                )
                createCircularReveal.duration = 350
                createCircularReveal.start()
            } else {
                val translateAnimation = TranslateAnimation(0.0f, 0.0f, (-toolbar.height).toFloat(), 0.0f)
                translateAnimation.duration = 320
                toolbar.clearAnimation()
                toolbar.startAnimation(translateAnimation)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val width = toolbar.width -
                        (if (containsOverflow) resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) else 0) -
                        resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon / 2
                val createCircularReveal = ViewAnimationUtils.createCircularReveal(
                    toolbar,
                    if (isRtl(resources)) toolbar.width - width else width,
                    toolbar.height / 2,
                    width.toFloat(),
                    0.0f
                )
                createCircularReveal.duration = 350
                createCircularReveal.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        toolbar.setBackgroundColor(getThemeColor(this@AudioChooserActivity, R.color.colorPrimaryDark))
                    }
                })
                createCircularReveal.start()
            } else {
                val alphaAnimation = AlphaAnimation(1.0f, 0.0f)
                val translateAnimation = TranslateAnimation(0.0f, 0.0f, 0.0f, (- toolbar.height).toFloat())
                val animationSet = AnimationSet(true)
                animationSet.addAnimation(alphaAnimation)
                animationSet.addAnimation(translateAnimation)
                animationSet.duration = 320
                animationSet.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}

                    override fun onAnimationEnd(animation: Animation) {
                        toolbar.setBackgroundColor(getThemeColor(this@AudioChooserActivity, R.color.colorPrimaryDark))
                    }

                    override fun onAnimationRepeat(animation: Animation) {}
                })
                toolbar.startAnimation(animationSet)
            }
        }
    }

    private fun isRtl(resources: Resources): Boolean {
        return resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
    }

    private fun getThemeColor(context: Context, id: Int) : Int {
        val theme = context.theme as Resources.Theme
        val a = theme.obtainStyledAttributes(intArrayOf(id))
        val result = a.getColor(0, 0)
        a.recycle()
        return result
    }

    override fun getMenuLayout(): Int = R.menu.toolbar_search_menu

    override fun onMenuAction(id_item: Int) {}

    companion object {
        fun getStartIntent(context: Context) : Intent {
            return Intent(context, AudioChooserActivity::class.java)
        }
    }
}

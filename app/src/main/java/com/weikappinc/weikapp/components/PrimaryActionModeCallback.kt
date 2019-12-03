package com.weikappinc.weikapp.components

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import com.weikappinc.weikapp.R
import com.weikappinc.weikapp.eventbus.events.audio_chooser_activity.AudioCancelSelectedEvent
import com.weikappinc.weikapp.eventbus.events.audio_chooser_activity.AudioSelectedEvent
import org.greenrobot.eventbus.EventBus

class PrimaryActionModeCallback : ActionMode.Callback {

    private var menuResId: Int = R.menu.toolbar_ok_menu

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.menuInflater.inflate(menuResId, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        return true
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        if(item.itemId == R.id.action_ok) {
            EventBus.getDefault().post(AudioSelectedEvent())
        }

        mode.finish()

        return true
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        EventBus.getDefault().post(AudioCancelSelectedEvent())
    }
}

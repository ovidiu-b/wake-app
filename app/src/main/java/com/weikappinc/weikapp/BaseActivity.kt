package com.weikappinc.weikapp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.toolbar_close_title_ok.*

abstract class BaseActivity : AppCompatActivity() {

    private var isActionSaveItemEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindLayout()

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Enable the back button
        enableBackButton(true)
        supportActionBar?.setHomeAsUpIndicator(getHomeIcon())

        init(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        val ivIcon = findViewById<View>(android.R.id.home) as? ImageView

        ivIcon?.setPadding(0, 0, 0, 0)
    }

    protected fun enableBackButton(enable: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(enable)
        supportActionBar?.setDisplayShowHomeEnabled(enable)
    }

    protected fun enableActionSaveItem(enable: Boolean) {
        isActionSaveItemEnabled = enable
        invalidateOptionsMenu()
    }

    abstract fun bindLayout()
    abstract fun init(savedInstanceState: Bundle?)
    abstract fun getHomeIcon(): Int
    abstract fun getMenuLayout(): Int
    abstract fun onMenuAction(id_item: Int)

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(getMenuLayout(), menu)

        val actionSaveItem: MenuItem? = menu?.findItem(R.id.action_ok)

        actionSaveItem?.isVisible = isActionSaveItemEnabled

        /*val item: MenuItem? = menu?.findItem(R.id.action_ok)
        val text = SpannableString("Guardar")
        text.setSpan(RelativeSizeSpan(0.9f), 0,text.length, 0) // set size
        text.setSpan(ForegroundColorSpan(Color.WHITE), 0, text.length, 0)
        item?.title = text*/

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_ok -> {
                onMenuAction(R.id.action_ok)

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        Log.d("BaseActivity", "onBackPressed()")

        return true
    }
}

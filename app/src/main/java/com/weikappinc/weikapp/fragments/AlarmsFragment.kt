package com.weikappinc.weikapp.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding3.view.clicks
import com.weikappinc.weikapp.R
import com.weikappinc.weikapp.activities.MESSAGE_ALARM_ACTIVATED
import com.weikappinc.weikapp.activities.SingleAlarmActivity
import com.weikappinc.weikapp.adapters.main_activity.AlarmListAdapter
import com.weikappinc.weikapp.showThisShort
import com.weikappinc.weikapp.view_models.main_activity.MainActivityViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.content_with_toolbar_and_list_fragment.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

private const val TAG = "AlarmsFragment"

private const val REQUEST_CODE_SINGLE_ALARM_ACTIVITY = 1

class AlarmsFragment : Fragment() {
    private val disposables = CompositeDisposable()

    private val viewModel: MainActivityViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_with_toolbar_and_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarTitle.text = getString(R.string.alarms_fragment_toolbar_title)

        requireContext().let {
            floatActionBtn.setImageDrawable(ContextCompat.getDrawable(it, R.drawable.ic_alarm_add_white_32dp))
        }

        disposables.add(
            floatActionBtn.clicks()
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    startActivityForResult(SingleAlarmActivity.getStartIntent(this.requireContext(), null), REQUEST_CODE_SINGLE_ALARM_ACTIVITY)
                })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = AlarmListAdapter()
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }

        // Observamos las alarmas que se actualizan y notificamos al viewmodel para que proceda con la actualización
        (recyclerView.adapter as AlarmListAdapter).updatesLiveData.observe(this, Observer { viewModel.updateAlarms(it) })

        disposables.add(
            (recyclerView.adapter as AlarmListAdapter)
                .clickOnAlarmEvents
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe{
                    startSingleAlarmActivityForResult(it)
                }
        )

        viewModel.getAlarms().observe(this, Observer {
            if (it.errors != null) {
                Log.e(TAG, "An error has occured when trying to retrive alarms from Database: ${(it.errors).message}")
            } else {
                it.list?.let { list -> (recyclerView.adapter as? AlarmListAdapter)?.updateListOfAlarms(list) }
            }
        })
    }

    private fun startSingleAlarmActivityForResult(id: String?) {
        startActivityForResult(SingleAlarmActivity.getStartIntent(this.requireContext(), id), REQUEST_CODE_SINGLE_ALARM_ACTIVITY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SINGLE_ALARM_ACTIVITY) {

            if (resultCode == Activity.RESULT_OK) {
                val message = data?.getStringExtra(MESSAGE_ALARM_ACTIVATED)

                message?.run {
                    requireActivity().showThisShort(this)
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        (recyclerView?.adapter as? AlarmListAdapter)?.clickOnAlarmEvents?.onComplete()
    }

    override fun onDetach() {
        super.onDetach()

        disposables.clear()
    }
}

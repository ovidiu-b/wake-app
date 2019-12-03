package com.weikappinc.weikapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.weikappinc.weikapp.R
import com.weikappinc.weikapp.adapters.main_activity.MessageListAdapter
import com.weikappinc.weikapp.view_models.main_activity.MainActivityViewModel
import kotlinx.android.synthetic.main.content_with_toolbar_and_list_fragment.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MessagesFragment : Fragment() {
    private lateinit var contextOfFragment: Context

    private var param1: String? = null
    private var param2: String? = null

    val viewModel: MainActivityViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_with_toolbar_and_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarTitle.text = getString(R.string.messages_fragment_toolbar_title)

        floatActionBtn.setImageDrawable(ContextCompat.getDrawable(contextOfFragment, R.drawable.ic_send_white_32dp))

        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = MessageListAdapter()
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }

        viewModel.messageList.observe(this, Observer {
            (recyclerView.adapter as MessageListAdapter).updateListOfAlarms(it)
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        contextOfFragment = context
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MessagesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MessagesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

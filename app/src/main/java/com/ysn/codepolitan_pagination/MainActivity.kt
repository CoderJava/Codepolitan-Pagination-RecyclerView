package com.ysn.codepolitan_pagination

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    lateinit var listData: ArrayList<String>
    lateinit var listViewType: ArrayList<Int>
    var isLoading by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isLoading = false
        repeat(10) { a ->
            listData.add(a.toString())
            listViewType.add(AdapterData.ITEM_VIEW_TYPE_CONTENT)
        }
        listData.add("")
        listViewType.add(AdapterData.ITEM_VIEW_TYPE_LOADING)

        val adapterData = AdapterData(
                listData = listData,
                listViewType = listViewType
        )
        val linearLayoutManager = LinearLayoutManager(this)
        recycler_view_data_activity_main.layoutManager = linearLayoutManager
        recycler_view_data_activity_main.adapter = adapterData
        recycler_view_data_activity_main.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                val countItem = linearLayoutManager.itemCount
                val lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                val isLastPosition = countItem.minus(1) == lastVisiblePosition
                if (!isLoading && isLastPosition) {
                    isLoading = true
                    doAsync {
                        repeat(10) { a ->
                            val lastValue: Int = when (a) {
                                0 -> {
                                    listData[listData.size - 2].toInt()
                                }
                                else -> {
                                    listData[listData.size - 1].toInt()
                                }
                            }
                            listData.add(lastValue.plus(1).toString())
                        }
                        Thread.sleep(1000 * 5)
                        // todo: do something in here
                    }
                }
            }
        })

    }

}

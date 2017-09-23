package com.ysn.codepolitan_pagination

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName
    lateinit var listData: ArrayList<String>
    lateinit var listViewType: ArrayList<Int>
    var countLoadMore by Delegates.notNull<Int>()
    var isLoading by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isLoading = false
        listData = ArrayList()
        listViewType = ArrayList()
        countLoadMore = 0
        repeat(30) { a ->
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
                Log.d(TAG, "isLoading: $isLoading & isLastPosition: $isLastPosition & countLoadMore: $countLoadMore")
                if (!isLoading && isLastPosition && countLoadMore < 3) {
                    isLoading = true
                    doAsync {
                        val lenTemp = listData.size - 1
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
                            listViewType.add(AdapterData.ITEM_VIEW_TYPE_CONTENT)
                        }
                        Thread.sleep(1000 * 10)
                        uiThread {
                            listData.removeAt(lenTemp)
                            listViewType.removeAt(lenTemp)

                            if (countLoadMore + 1 < 3) {
                                listData.add("")
                                listViewType.add(AdapterData.ITEM_VIEW_TYPE_LOADING)
                            }
                            adapterData.refresh(listData, listViewType)
                            countLoadMore += 1
                            isLoading = false
                        }
                    }
                }
            }
        })

    }

}

package com.fitbit.fitbitapp.adapters

import android.app.Activity
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fitbit.fitbitapp.R
import com.fitbit.fitbitapp.interfaces.OnLoadMoreListener
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*

/**
 * ActivitiesAdapter - An adapter to Shows list of all the Activities with Pagination Feature.\
 */
class ActivitiesAdapter(recyclerView: RecyclerView, private val activities: List<Any?>?, private val activity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1
    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var isLoading = false
    private val visibleThreshold = 2
    private var lastVisibleItem = 0
    private var totalItemCount = 0

    /**
     * Create View Holder to show either progressbar when paginating or to show a raw Activity
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(activity).inflate(R.layout.item_recycler_view_row, parent, false)
            return ActivitiesViewHolder(view)
        } else if (viewType == VIEW_TYPE_LOADING) {
            val view = LayoutInflater.from(activity).inflate(R.layout.item_loading, parent, false)
            return LoadingViewHolder(view)
        }
        val view = LayoutInflater.from(activity).inflate(R.layout.item_recycler_view_row, parent, false)
        return ActivitiesViewHolder(view)
    }

    /**
     * Bind some values with each row of View Type Activities
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ActivitiesViewHolder) { //Object object = dailyActivitySummary.getActivities().get(position);
            val stringBuilder = StringBuilder()
            printKeys(stringBuilder, activities!![position])
            //userViewHolder.phone.setText(contact.getEmail());
            holder.txt_activity.text = Html.fromHtml(stringBuilder.toString())
        } else if (holder is LoadingViewHolder) {
            holder.progressBar.isIndeterminate = true
        }
    }

    /**
     * If value is returned in number, It is formatted here.
     */
    private fun formatNumber(number: Number): String {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(number)
    }

    /**
     * It is used to identify if a value is an image url or not.
     */
    private fun isImageUrl(string: String): Boolean {
        return string.startsWith("http") &&
                (string.endsWith("jpg")
                        || string.endsWith("gif")
                        || string.endsWith("png"))
    }

    /**
     * It converts entire Activity Json Object into well formatted key value string.
     */
    protected fun printKeys(stringBuilder: StringBuilder, `object`: Any?) {
        var jsonObject: JSONObject? = null
        try {
            jsonObject = JSONObject(Gson().toJson(`object`))
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val value = jsonObject[key]
                if (value !is JSONObject
                        && value !is JSONArray) {
                    stringBuilder.append("<b>")
                    stringBuilder.append(key)
                    stringBuilder.append(":</b>&nbsp;")
                    if (value is Number) {
                        stringBuilder.append(formatNumber(value))
                    } else if (isImageUrl(value.toString())) {
                        stringBuilder.append("<br/>")
                        stringBuilder.append("<center><img src=\"")
                        stringBuilder.append(value.toString())
                        stringBuilder.append("\" width=\"150\" height=\"150\"></center>")
                    } else {
                        stringBuilder.append(value.toString())
                    }
                    stringBuilder.append("<br/>")
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return activities?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (activities!![position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    /**
     * Reset progressbar once pagination  is done
     */
    fun setLoaded() {
        isLoading = false
    }

    // "Loading item" ViewHolder
    private inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var progressBar: ProgressBar

        init {
            progressBar = view.findViewById<View>(R.id.progressBar1) as ProgressBar
        }
    }

    // "Normal item" ViewHolder
    private inner class ActivitiesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txt_activity: TextView

        init {
            txt_activity = view.findViewById<View>(R.id.txt_activity) as TextView
        }
    }

    fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener?) {
        onLoadMoreListener = mOnLoadMoreListener
    }

    init {
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = linearLayoutManager!!.itemCount
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener!!.onLoadMore()
                    }
                    isLoading = true
                }
            }
        })
    }
}
package com.fitbit.fitbitapp.fragments

import android.app.Fragment
import android.app.LoaderManager
import android.content.Loader
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.fitbit.api.loaders.ResourceLoaderResult
import com.fitbit.api.loaders.ResourceLoaderResult.ResultType
import com.fitbit.fitbitapp.R
import com.fitbit.fitbitapp.adapters.ActivitiesAdapter
import com.fitbit.fitbitapp.databinding.LayoutInfoBinding
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*

/**
 * InfoFragment - An abstract Fragment working as a base for all the fragments and handling operations of Resource Loading.
 */
abstract class InfoFragment<T> : Fragment(), LoaderManager.LoaderCallbacks<ResourceLoaderResult<T>?>, OnRefreshListener {
    protected var binding: LayoutInfoBinding? = null
    var activitiesAdapter: ActivitiesAdapter? = null
    protected val TAG = javaClass.simpleName


    /*override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_info, container, false)
        binding!!.setTitleText(titleResourceId)
        setMainText(activity.getString(R.string.no_data))
        binding!!.swipeRefreshLayout.setOnRefreshListener(this)
        binding!!.setLoading(true)
        binding!!.swipeRefreshLayout.isEnabled = false
        //find view by id and attaching adapter for the RecyclerView
        binding!!.recyclerView.layoutManager = LinearLayoutManager(activity)
        return binding!!.getRoot()
    }*/

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = inflater?.let { DataBindingUtil.inflate(it, R.layout.layout_info, container, false) }
        binding!!.setTitleText(titleResourceId)
        setMainText(activity.getString(R.string.no_data))
        binding!!.swipeRefreshLayout.setOnRefreshListener(this)
        binding!!.setLoading(true)
        binding!!.swipeRefreshLayout.isEnabled = false
        //find view by id and attaching adapter for the RecyclerView
        binding!!.recyclerView.layoutManager = LinearLayoutManager(activity)
        return binding!!.getRoot()
    }
    override fun onResume() {
        super.onResume()
        loaderManager.initLoader(loaderId, null, this).forceLoad()
    }

    override fun onLoadFinished(loader: Loader<ResourceLoaderResult<T>?>, data: ResourceLoaderResult<T>?) {
        binding!!.swipeRefreshLayout.isRefreshing = false
        binding!!.loading = false
        when (data!!.resultType) {
            ResultType.ERROR -> Toast.makeText(activity, R.string.error_loading_data, Toast.LENGTH_LONG).show()
            ResultType.EXCEPTION -> {
                Log.e(TAG, "Error loading data", data.exception)
                Toast.makeText(activity, R.string.error_loading_data, Toast.LENGTH_LONG).show()
            }
        }
    }

    abstract val titleResourceId: Int
    protected abstract val loaderId: Int
    override fun onLoaderReset(loader: Loader<ResourceLoaderResult<T>?>) { //no-op
    }

    override fun onRefresh() {
        loaderManager.getLoader<Any>(loaderId).forceLoad()
    }

    fun restartMyLoader() {
        if (loaderManager.getLoader<Any>(loaderId) != null) {
            loaderManager.destroyLoader(loaderId)
        }
        loaderManager.restartLoader(loaderId, null, this)
    }

    private fun formatNumber(number: Number): String {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(number)
    }

    private fun isImageUrl(string: String): Boolean {
        return string.startsWith("http") &&
                (string.endsWith("jpg")
                        || string.endsWith("gif")
                        || string.endsWith("png"))
    }

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
                    stringBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;<b>")
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

    protected fun setMainText(text: String?) {
        binding!!.webview.loadData(text, "text/html", "UTF-8")
    }
}
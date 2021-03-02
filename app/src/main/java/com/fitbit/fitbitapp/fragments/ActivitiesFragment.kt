package com.fitbit.fitbitapp.fragments

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Loader
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.fitbit.api.loaders.ResourceLoaderFactory
import com.fitbit.api.loaders.ResourceLoaderResult
import com.fitbit.api.models.DailyActivitySummary
import com.fitbit.api.services.ActivityService
import com.fitbit.fitbitapp.R
import com.fitbit.fitbitapp.adapters.ActivitiesAdapter
import com.fitbit.fitbitapp.interfaces.OnLoadMoreListener
import java.util.*

/**
 * ActivitiesFragment - Used to show list of all Users Activities
 */
class ActivitiesFragment : InfoFragment<DailyActivitySummary?>() {
    var dailyActivitySummary: DailyActivitySummary? = null
    var flagLoadMore = true
    override val titleResourceId: Int
        get() = R.string.activity_info

    protected override val loaderId: Int
        protected get() = 3

   /* override fun onCreateLoader(id: Int, args: Bundle): Loader<ResourceLoaderResult<DailyActivitySummary?>?>? {
        return ActivityService.getDailyActivitySummaryLoader(activity, Date())
    }*/

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<ResourceLoaderResult<DailyActivitySummary?>?> {
        return ActivityService.getDailyActivitySummaryLoader(activity, Date())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val menuInflater = activity.menuInflater
        menuInflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.pick_date -> {
                /* DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");*/
                val newCalendar = Calendar.getInstance()
                val StartTime = DatePickerDialog(activity, android.R.style.Theme_DeviceDefault_Light_Dialog, OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    val newDate = Calendar.getInstance()
                    newDate[year, monthOfYear] = dayOfMonth
                    val df = DateFormat()
                    val selectedDate = DateFormat.format("yyyy-MM-dd", newDate.time).toString()
                    //Toast.makeText(getActivity(),selectedDate, Toast.LENGTH_LONG).show();
//activitydate.setText(dateFormatter.format(newDate.getTime()));
                    dailyActivitySummary = null
                    dailyActivitySummary = DailyActivitySummary()
                    activitiesAdapter!!.notifyDataSetChanged()
                    ResourceLoaderFactory.afterDate = selectedDate
                    restartMyLoader()
                    binding!!.swipeRefreshLayout.isRefreshing = true
                    Handler().postDelayed({
                        loaderManager.getLoader<Any>(loaderId).forceLoad()
                        binding!!.swipeRefreshLayout.isRefreshing = false
                    }, 3000)
                }, newCalendar[Calendar.YEAR], newCalendar[Calendar.MONTH], newCalendar[Calendar.DAY_OF_MONTH])
                StartTime.show()
            }
        }
        return true
    }

    override fun onLoadFinished(loader: Loader<ResourceLoaderResult<DailyActivitySummary?>?>, data: ResourceLoaderResult<DailyActivitySummary?>?) {
        super.onLoadFinished(loader, data)
        if (data!!.isSuccessful) {
            if (dailyActivitySummary != null && dailyActivitySummary!!.activities.size > 0) {
                for (i in data.result!!.activities.indices) {
                    val `object` = data.result!!.activities[i]
                    dailyActivitySummary!!.activities.add(`object`)
                }
                dailyActivitySummary!!.pagination = data.result!!.pagination
                activitiesAdapter!!.notifyDataSetChanged()
                activitiesAdapter!!.setLoaded()
            } else {
                dailyActivitySummary = data.result
                setRecyclerView(dailyActivitySummary!!)
            }
            bindActivityData(data.result)
        }
    }

    fun bindActivityData(dailyActivitySummary: DailyActivitySummary?) {
        val stringBuilder = StringBuilder()
        val summary = dailyActivitySummary!!.summary
        val goals = dailyActivitySummary.goals
        val activities = dailyActivitySummary.activities
        val pagination = dailyActivitySummary.pagination
        stringBuilder.append("<b>ACTIVITIES</b> ")
        stringBuilder.append("<br />")
        for (i in activities.indices) {
            printKeys(stringBuilder, activities[i])
            stringBuilder.append("<br />")
        }
        stringBuilder.append("<br />")
        stringBuilder.append("<b>PAGINATION</b> ")
        stringBuilder.append("<br />")
        printKeys(stringBuilder, pagination)
        /*stringBuilder.append("<br />");
        stringBuilder.append("<b>TODAY</b> ");
        stringBuilder.append("<br />");
        printKeys(stringBuilder, summary);


        stringBuilder.append("<b>GOALS</b> ");
        stringBuilder.append("<br />");
        printKeys(stringBuilder, goals);*/setMainText(stringBuilder.toString())
    }

    /*protected fun setRecyclerView(dailyActivitySummary: DailyActivitySummary?) {
        activitiesAdapter = ActivitiesAdapter(binding!!.recyclerView, dailyActivitySummary!!.activities, activity)
        binding!!.recyclerView.adapter = activitiesAdapter
        //set load more listener for the RecyclerView adapter
        activitiesAdapter!!.setOnLoadMoreListener {
            if (ResourceLoaderFactory.NextAPIURL == null || ResourceLoaderFactory.NextAPIURL != null && !ResourceLoaderFactory.NextAPIURL.equals(dailyActivitySummary.pagination.next, ignoreCase = true)) {
                ResourceLoaderFactory.NextAPIURL = dailyActivitySummary.pagination.next
                if (!TextUtils.isEmpty(dailyActivitySummary.pagination.next)) {
                    dailyActivitySummary.activities.add(null)
                    activitiesAdapter!!.notifyItemInserted(dailyActivitySummary.activities.size - 1)
                    restartMyLoader()
                    Handler().postDelayed({
                        dailyActivitySummary.activities.removeAt(dailyActivitySummary.activities.size - 1)
                        activitiesAdapter!!.notifyItemRemoved(dailyActivitySummary.activities.size)
                        loaderManager.getLoader<Any>(loaderId).forceLoad()
                    }, 3000)
                }
            }
        }

    }*/

    protected fun setRecyclerView(dailyActivitySummary: DailyActivitySummary) {
        activitiesAdapter = ActivitiesAdapter(binding!!.recyclerView, dailyActivitySummary.activities, activity)
        binding!!.recyclerView.adapter = activitiesAdapter
        //set load more listener for the RecyclerView adapter
        activitiesAdapter!!.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() { //if (dailyActivitySummary.getActivities().size() <= 20) {
                if (ResourceLoaderFactory.NextAPIURL == null || ResourceLoaderFactory.NextAPIURL != null && !ResourceLoaderFactory.NextAPIURL.equals(dailyActivitySummary.pagination.next, ignoreCase = true)) {
                    ResourceLoaderFactory.NextAPIURL = dailyActivitySummary.pagination.next
                    if (!TextUtils.isEmpty(dailyActivitySummary.pagination.next)) {
                        dailyActivitySummary.activities.add(null)
                        activitiesAdapter!!.notifyItemInserted(dailyActivitySummary.activities.size - 1)
                        restartMyLoader()
                        Handler().postDelayed({
                            dailyActivitySummary.activities.removeAt(dailyActivitySummary.activities.size - 1)
                            activitiesAdapter!!.notifyItemRemoved(dailyActivitySummary.activities.size)
                            loaderManager.getLoader<Int>(loaderId).forceLoad()
                            //Generating more data
                            //onCreateLoader(getLoaderId(),null);
                            //onLoaderReset(getLoaderManager().<ResourceLoaderResult<DailyActivitySummary>>getLoader(getLoaderId()));
                            /*int index = contacts.size();
                                                        int end = index + 10;
                                                        for (int i = index; i < end; i++) {
                                                            Contact contact = new Contact();
                                                            contact.setPhone(phoneNumberGenerating());
                                                            contact.setEmail("DevExchanges" + i + "@gmail.com");
                                                            contacts.add(contact);
                                                        }
                                                        activitiesAdapter.notifyDataSetChanged();
                                                        activitiesAdapter.setLoaded();*/
                        }, 3000)
                    }
                    /*} else {
                    Toast.makeText(getActivity(), "Loading data completed", Toast.LENGTH_SHORT).show();
                }*/
                }
            }
        })
    }


}
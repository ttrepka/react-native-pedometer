package com.pedometer;

import com.facebook.react.ReactActivity;

import android.os.Bundle;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MainActivity extends ReactActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scheduleJob();
    }

    /**
     * Returns the name of the main component registered from JavaScript. This is
     * used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "pedometer";
    }

    private void scheduleJob() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (!preferences.getBoolean("firstRunComplete", false)) {
            // schedule the job only once.
            scheduleStepJob();

            // update shared preference
            // SharedPreferences.Editor editor = preferences.edit();
            // editor.putBoolean("firstRunComplete", true);
            // editor.commit();
        }
    }

    private void scheduleStepJob() {
        JobScheduler jobScheduler = (JobScheduler) getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);

        ComponentName componentName = new ComponentName(this, StepJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(1, componentName).setPeriodic(JobInfo.getMinPeriodMillis())
                .setPersisted(true).build();
        jobScheduler.schedule(jobInfo);
    }
}

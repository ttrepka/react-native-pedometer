package com.pedometer;

import android.app.job.JobParameters;
import android.app.job.JobService;

public class StepJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        // TODO do stuff
        System.out.println("StepJobService is running");

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}

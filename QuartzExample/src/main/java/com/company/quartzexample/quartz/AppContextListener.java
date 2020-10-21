
package com.company.quartzexample.quartz;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@WebListener
public class AppContextListener implements ServletContextListener
{

	private Scheduler storageCleaningScheduler;

	public static Logger LOG = LoggerFactory.getLogger(AppContextListener.class);

	@Override
	public void contextInitialized(final ServletContextEvent sce)
	{
		try
		{
			this.initializeAndStartBackupScheduler();
		}
		catch(final SchedulerException e)
		{
			AppContextListener.LOG.error("Starting storage backup scheduler failed!", e);
		}
	}

	@Override
	public void contextDestroyed(final ServletContextEvent sce)
	{
		try
		{
			if(this.storageCleaningScheduler != null)
			{
				this.storageCleaningScheduler.shutdown(true);
			}
		}
		catch(final SchedulerException e)
		{
			AppContextListener.LOG.error("Shutting down Backup scheduler failed.", e);
		}
	}

	private void initializeAndStartBackupScheduler() throws SchedulerException
	{
		/*
		 * Examples:
		 * Minutely: CronScheduleBuilder.cronSchedule("0 0/1 * * * ?")
		 * Half-Hourly: CronScheduleBuilder.cronSchedule("0 0/30 * * * ?")
		 * Hourly: CronScheduleBuilder.cronSchedule("0 * * * * ?")
		 *
		 * Each day at 23:30: CronScheduleBuilder.dailyAtHourAndMinute(23, 30)
		 */

		this.storageCleaningScheduler = StdSchedulerFactory.getDefaultScheduler();

		final JobDetail job = JobBuilder.newJob(StorageCleaningTask.class).build();

		// Productive Trigger - every 2 Minute
		final Trigger trigger = TriggerBuilder.newTrigger()
			.startNow()
			.withSchedule(CronScheduleBuilder
				.cronSchedule("0 */2 * ? * *"))
			.build();

		this.storageCleaningScheduler.scheduleJob(job, trigger);
		this.storageCleaningScheduler.start();
	}
}

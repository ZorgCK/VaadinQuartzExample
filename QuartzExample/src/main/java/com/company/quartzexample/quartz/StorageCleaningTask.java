
package com.company.quartzexample.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class StorageCleaningTask implements Job
{
	
	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException
	{
		System.out.println("Storage cleaning successfull");
	}
	
}

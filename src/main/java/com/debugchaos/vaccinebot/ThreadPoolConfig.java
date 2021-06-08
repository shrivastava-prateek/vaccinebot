package com.debugchaos.vaccinebot;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import lombok.Getter;
import lombok.Setter;

@Configuration
@EnableAsync(proxyTargetClass = true)
@ConfigurationProperties(prefix = "async.thread.pool")
@Getter @Setter
public class ThreadPoolConfig implements AsyncConfigurer,SchedulingConfigurer {

	private int coreSize;
	private int maxSize;
	private int queueCapacity;

	@Override
	public Executor getAsyncExecutor() {
		System.out.println("Inside Executor confid" + coreSize + " " + maxSize + " " + queueCapacity);
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(coreSize);
		executor.setMaxPoolSize(maxSize);
		executor.setQueueCapacity(queueCapacity);
		executor.setThreadNamePrefix("worker-exec-");
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.initialize();
		return executor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return (ex, method, params) -> {
			Class<?> targetClass = method.getDeclaringClass();
			Logger logger = LoggerFactory.getLogger(targetClass);
			logger.error(ex.getMessage(), ex);
		};
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		  	ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		  	taskScheduler.setThreadNamePrefix("scheduler-exec-");
	        taskScheduler.setPoolSize(coreSize);
	        taskScheduler.initialize();
	        taskRegistrar.setTaskScheduler(taskScheduler);		
	}

}

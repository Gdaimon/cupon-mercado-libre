package com.mercado.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class CuponAppApplication {

//	private static final Logger logger = LoggerFactory.getLogger ( CuponAppApplication.class );
	
	public static void main ( String[] args ) {
		SpringApplication.run ( CuponAppApplication.class, args );
	}
	
	@Bean ( name = "processExecutor" )
	public TaskExecutor workExecutor ( ) {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor ( );
		threadPoolTaskExecutor.setThreadNamePrefix ( "Async-" );
		threadPoolTaskExecutor.setCorePoolSize ( 3 );
		threadPoolTaskExecutor.setMaxPoolSize ( 3 );
		threadPoolTaskExecutor.setQueueCapacity ( 600 );
		threadPoolTaskExecutor.afterPropertiesSet ( );
//		logger.info ( "ThreadPoolTaskExecutor set" );
		return threadPoolTaskExecutor;
	}
	
}
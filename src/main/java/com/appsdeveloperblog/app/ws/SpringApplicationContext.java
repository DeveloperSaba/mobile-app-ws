/**
 * 
 */
package com.appsdeveloperblog.app.ws;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author saba
 *
 */
public class SpringApplicationContext implements ApplicationContextAware {

	private static ApplicationContext CONTEXT;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		CONTEXT = applicationContext;

	}
	
	public static Object getbean(String BeanName) {
		return CONTEXT.getBean(BeanName);
		
	}

}

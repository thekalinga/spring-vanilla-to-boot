package com.acme.mvc.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

class ShopWebApplicationInitializer implements WebApplicationInitializer {
  @Override
  public void onStartup(ServletContext servletContext) {
    // Create the 'root' Spring application context
    AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
    rootContext.register(RootConfiguration.class);
    rootContext.setClassLoader(getClass().getClassLoader()); // Refer to this on why this is necessary. https://stackoverflow.com/a/54754744/211794

    // Manage the lifecycle of the root application context
    servletContext.addListener(new ContextLoaderListener(rootContext));

    // Create the dispatcher servlet's Spring application context
    AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
    dispatcherContext.register(CustomWebApplicationContext.class);
    dispatcherContext.setServletContext(servletContext);

    // Register and map the dispatcher servlet
    ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/");
  }
}

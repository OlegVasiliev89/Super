/**
 * Configuration class for web-related settings in the Spring Boot application.
 * This class implements {@link WebMvcConfigurer} to customize various aspects
 * of Spring MVC, including CORS policies, resource handling, and view controllers.
 */
package com.project.SuperC.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures Cross-Origin Resource Sharing mappings for the application.
     * @param registry The {@link CorsRegistry} to configure CORS settings.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    /**
     * Configures resource handlers for serving static content and handling client-side routing.
     * @param registry The {@link ResourceHandlerRegistry} to configure resource handlers.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/*.html", "/*.css", "/*.js", "/*.ico",
                        "/*.txt", "/*.png", "/*.jpg", "/*.jpeg", "/*.gif", "/*.svg",
                        "/*.woff", "/*.woff2", "/*.ttf", "/*.eot")
                .addResourceLocations("classpath:/static/");

        registry.addResourceHandler("/")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        }
                        Resource indexHtml = location.createRelative("index.html");
                        if (indexHtml.exists() && indexHtml.isReadable()) {
                            return indexHtml;
                        }
                        return null;
                    }
                });

        registry.addResourceHandler("/search/**")
                .addResourceLocations("classpath:/static/search/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);

                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        }

                        if (!resourcePath.contains(".")) {
                            Resource indexHtml = location.createRelative("index.html");
                            if (indexHtml.exists() && indexHtml.isReadable()) {
                                return indexHtml;
                            }
                        }
                        return null;
                    }
                });
    }

    /**
     * Configures simple automated controllers for forwarding requests to specific views.
     * This method maps the root URL ("/") to forward to {@code index.html}.
     *
     * @param registry The {@link ViewControllerRegistry} to configure view controllers.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}

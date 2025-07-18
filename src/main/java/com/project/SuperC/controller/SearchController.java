package com.project.SuperC.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    /**
     * Serves the React application's index.html for requests to /search and /search/.
     * This method directly loads the index.html as a ClassPathResource.
     * Any sub-paths (e.g., /search/products) will be handled by the client-side React router,
     * with the WebConfig's ResourceHandler for /search/** serving index.html as a fallback.
     * @return A ResponseEntity containing the index.html content.
     */
    @GetMapping({"/search", "/search/"})
    public ResponseEntity<Resource> serveReactApp() {
        logger.info("SearchController: Request received for /search or /search/");
        Resource resource = new ClassPathResource("static/search/index.html");

        if (resource.exists() && resource.isReadable()) {
            logger.info("SearchController: Successfully found and serving static/search/index.html");
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(resource);
        } else {
            logger.error("SearchController: Failed to find or read static/search/index.html. Resource exists: {}, Readable: {}",
                    resource.exists(), resource.isReadable());
            return ResponseEntity.notFound().build();
        }
    }
}

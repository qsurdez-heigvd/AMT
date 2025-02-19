package ch.heig.amt.vineward.configuration;

import ch.heig.amt.vineward.io.ZipEntryResource;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

/**
 * Simple resource handler serving the wine images.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Configuration(proxyBeanMethods = false)
public class StaticImagesConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/wines/**")
            .resourceChain(true)
            .addResolver(new ZipResourceResolver());
    }

    class ZipResourceResolver implements ResourceResolver {
        @Override
        public Resource resolveResource(
            HttpServletRequest request,
            String requestPath,
            List<? extends Resource> locations,
            ResourceResolverChain chain
        ) {
            var imagePath = requestPath.replaceFirst("^/images/wines/", "");
            var zipResource = new ClassPathResource("static/wine-images.zip");
            return new ZipEntryResource(zipResource, imagePath);
        }

        @Override
        public String resolveUrlPath(
            String resourcePath,
            List<? extends Resource> locations,
            ResourceResolverChain chain
        ) {
            var imagePath = resourcePath.replaceFirst("^/images/wines/", "");

            try {
                var zipResource = new ClassPathResource("static/wine-images.zip");
                try (var zis = new ZipInputStream(zipResource.getInputStream())) {
                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null) {
                        if (entry.getName().equals(imagePath)) {
                            return "/images/wines/" + imagePath;
                        }

                        zis.closeEntry();
                    }
                }
            } catch (IOException e) {
                return chain.resolveUrlPath(resourcePath, locations);
            }

            return chain.resolveUrlPath(resourcePath, locations);
        }
    }
}

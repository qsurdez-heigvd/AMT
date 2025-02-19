package ch.heig.amt.vineward.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;

/**
 * {@link Resource} implementation for a ZIP archive entry.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
public class ZipEntryResource extends AbstractResource {
    private final Resource zipResource;
    private final String entryName;

    public ZipEntryResource(Resource zipResource, String entryName) {
        this.zipResource = zipResource;
        this.entryName = entryName;
    }

    @NonNull
    @Override
    public String getDescription() {
        return "ZIP Entry [" + entryName + "]";
    }

    @NonNull
    @Override
    public InputStream getInputStream() throws IOException {
        var zis = new ZipInputStream(zipResource.getInputStream());
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if (entry.getName().equals(entryName)) {
                return new FilterInputStream(zis) {
                    @Override
                    public void close() throws IOException {
                        zis.closeEntry();
                        super.close();
                    }
                };
            }
            zis.closeEntry();
        }
        zis.close();
        throw new IOException("Entry not found: " + entryName);
    }

    @Override
    public long contentLength() throws IOException {
        try (ZipInputStream zis = new ZipInputStream(zipResource.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals(entryName)) {
                    return entry.getSize();
                }
                zis.closeEntry();
            }
        }

        return -1;
    }

    @Override
    public long lastModified() throws IOException {
        try (ZipInputStream zis = new ZipInputStream(zipResource.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals(entryName)) {
                    return entry.getTime();
                }
                zis.closeEntry();
            }
        }

        return -1;
    }

    @Override
    public String getFilename() {
        return entryName;
    }
}

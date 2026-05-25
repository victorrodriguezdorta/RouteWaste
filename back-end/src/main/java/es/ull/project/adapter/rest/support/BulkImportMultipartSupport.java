package es.ull.project.adapter.rest.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.ull.project.adapter.rest.exception.FieldError;
import es.ull.project.adapter.rest.exception.ValidationException;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Parses uploaded JSON files into bulk request DTOs using the configured {@link ObjectMapper}.
 */
@Component
public class BulkImportMultipartSupport {

    private static final String FIELD_FILE = "file";
    private static final String MSG_FILE_REQUIRED = "File is required";
    private static final String MSG_FILE_EMPTY = "File must not be empty";
    private static final String MSG_INVALID_JSON = "Invalid JSON file";
    private static final int ZERO = 0;

    private final ObjectMapper objectMapper;

    /**
     * Creates support bean with the configured JSON mapper.
     *
     * @param objectMapper Jackson mapper with custom REST deserializers
     */
    @Autowired
    public BulkImportMultipartSupport(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Reads and deserializes a multipart JSON file.
     *
     * @param file              uploaded file part
     * @param bulkRequestType   target bulk request body class
     * @param <T>               bulk request type
     * @return deserialized bulk request
     */
    public <T> T parseJsonFile(MultipartFile file, Class<T> bulkRequestType) {
        if (file == null || file.isEmpty()) {
            throw new ValidationException(List.of(new FieldError(FIELD_FILE, MSG_FILE_REQUIRED)));
        }
        if (file.getSize() == ZERO) {
            throw new ValidationException(List.of(new FieldError(FIELD_FILE, MSG_FILE_EMPTY)));
        }
        try {
            return this.objectMapper.readValue(file.getInputStream(), bulkRequestType);
        } catch (IOException exception) {
            String detail = exception.getMessage() != null ? exception.getMessage() : MSG_INVALID_JSON;
            throw new ValidationException(List.of(new FieldError(FIELD_FILE, MSG_INVALID_JSON + ": " + detail)));
        }
    }
}

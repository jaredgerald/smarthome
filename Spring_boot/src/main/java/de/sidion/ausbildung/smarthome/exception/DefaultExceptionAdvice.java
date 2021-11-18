package de.sidion.ausbildung.smarthome.exception;

import de.sidion.ausbildung.smarthome.dto.ErrorResponseDTO;
import de.sidion.ausbildung.smarthome.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@RequiredArgsConstructor
public class DefaultExceptionAdvice extends ResponseEntityExceptionHandler {
    private final ResponseService responseService;

    @ExceptionHandler(value = { DataIntegrityViolationException.class})
    protected ResponseEntity<ErrorResponseDTO> handleConflict(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        return handleException(status, ex, request);
    }

    @ExceptionHandler(value = { RuntimeException.class, Exception.class})
    protected ResponseEntity<ErrorResponseDTO> handleDefaultException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return handleException(status, ex, request);
    }

    private ResponseEntity<ErrorResponseDTO> handleException(HttpStatus status, Exception ex, WebRequest request) {
        sendMqttResponse(status);
        return createResponse(status, ex, request);
    }

    private void sendMqttResponse(HttpStatus status) {
        responseService.sendResponse(status);
    }

    private ResponseEntity<ErrorResponseDTO> createResponse(HttpStatus status, Exception ex, WebRequest request) {
        final ErrorResponseDTO responseDTO = new ErrorResponseDTO(
                LocalDateTime.now(), ex, status, request.getDescription(false));
        return ResponseEntity.status(status).body(responseDTO);
    }
}

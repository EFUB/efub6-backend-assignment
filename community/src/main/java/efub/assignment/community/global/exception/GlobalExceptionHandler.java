package efub.assignment.community.global.exception;

import efub.assignment.community.global.exception.dto.ErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorDto> handleCustomException(CustomException e, HttpServletRequest request) {
        ErrorDto errorDto = new ErrorDto(
                LocalDateTime.now().toString(),
                e.getErrorCode().getStatus(),
                e.getErrorCode().name(),
                e.getErrorCode().getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorDto, HttpStatusCode.valueOf(e.getErrorCode().getStatus()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorDto> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("잘못된 요청입니다.");

        ErrorDto errorDto = new ErrorDto(
                LocalDateTime.now().toString(),
                ErrorCode.INVALID_INPUT.getStatus(),
                ErrorCode.INVALID_INPUT.name(),
                errorMessage,
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorDto> handleException(Exception e, HttpServletRequest request) {
        ErrorDto errorDto = new ErrorDto(
                LocalDateTime.now().toString(),
                ErrorCode.INTERNAL_SERVER_ERROR.getStatus(),
                ErrorCode.INTERNAL_SERVER_ERROR.name(),
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
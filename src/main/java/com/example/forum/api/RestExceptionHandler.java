package com.example.forum.api;

import com.example.forum.exception.DuplicateResourceException;
import com.example.forum.exception.InvalidCredentialsException;
import com.example.forum.exception.InvalidRequestException;
import com.example.forum.exception.ResourceNotFoundException;
import com.example.forum.exception.UserHasDependenciesException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ResourceNotFoundException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage(),
                "Проверьте идентификатор ресурса и убедитесь, что запись существует.",
                List.of(), request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(
            InvalidCredentialsException exception, HttpServletRequest request) {
        return error(HttpStatus.UNAUTHORIZED, exception.getMessage(),
                "Проверьте имя пользователя и пароль.", List.of(), request);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiError> handleInvalidRequest(
            InvalidRequestException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage(),
                "Исправьте указанные данные запроса и повторите попытку.",
                List.of(), request);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicate(
            DuplicateResourceException exception, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, exception.getMessage(),
                "Измените конфликтующее значение или используйте уже существующую запись.",
                List.of(), request);
    }

    @ExceptionHandler(UserHasDependenciesException.class)
    public ResponseEntity<ApiError> handleUserDependencies(
            UserHasDependenciesException exception, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, exception.getMessage(),
                "Сначала переназначьте или удалите перечисленные материалы. Если их нужно сохранить, "
                        + "оставьте пользователя в системе. Автоматического удаления связанных данных нет.",
                List.of(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<String> details = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();
        List<String> globalErrors = exception.getBindingResult().getGlobalErrors().stream()
                .map(error -> error.getDefaultMessage())
                .toList();
        details = java.util.stream.Stream.concat(details.stream(), globalErrors.stream()).toList();
        String message = details.isEmpty() ? "Проверка входных данных не пройдена." : details.get(0);
        return error(HttpStatus.BAD_REQUEST, message,
                "Проверьте обязательные поля, формат значений и ограничения длины.",
                details, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException exception, HttpServletRequest request) {
        List<String> details = exception.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();
        return error(HttpStatus.BAD_REQUEST, "Параметры запроса не прошли проверку.",
                "Исправьте значения параметров согласно требованиям API.", details, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableBody(
            HttpMessageNotReadableException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "Тело запроса имеет неверный формат или содержит некорректные значения.",
                "Проверьте JSON, названия полей, типы данных и допустимые значения перечислений.",
                List.of(), request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParameter(
            MissingServletRequestParameterException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST,
                "Отсутствует обязательный параметр запроса: " + exception.getParameterName() + ".",
                "Добавьте обязательный параметр в URL запроса.", List.of(), request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST,
                "Параметр '" + exception.getName() + "' имеет недопустимый тип или формат.",
                "Передайте значение ожидаемого типа, например числовой ID для идентификатора.",
                List.of(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataConflict(
            DataIntegrityViolationException exception, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT,
                "Операция нарушает ограничение базы данных: запись связана с другими данными "
                        + "или нарушает ограничение уникальности.",
                "Проверьте связанные записи и уникальность значений. Существующие данные не были "
                        + "автоматически удалены.", List.of(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, safeMessage(exception),
                "Проверьте входные данные и повторите запрос.", List.of(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(
            Exception exception, HttpServletRequest request) {
        log.error("Необработанная ошибка при {} {}", request.getMethod(), request.getRequestURI(), exception);
        return error(HttpStatus.INTERNAL_SERVER_ERROR,
                "Во время обработки запроса произошла внутренняя ошибка сервера.",
                "Повторите запрос позже. Если ошибка повторяется, обратитесь к администратору.",
                List.of(), request);
    }

    private ResponseEntity<ApiError> error(
            HttpStatus status, String message, String solution,
            List<String> details, HttpServletRequest request) {
        log.warn("Ошибка {} {}: {} ({})", status.value(), request.getRequestURI(), message, request.getMethod());
        ApiError body = new ApiError(LocalDateTime.now(), status.value(), status.getReasonPhrase(),
                message, request.getRequestURI(), solution, details);
        return ResponseEntity.status(status).body(body);
    }

    private String safeMessage(RuntimeException exception) {
        return exception.getMessage() == null || exception.getMessage().isBlank()
                ? "Некорректный запрос." : exception.getMessage();
    }
}

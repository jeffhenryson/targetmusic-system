package com.targetmusic.infra.handler;

import com.targetmusic.core.domain.exception.cliente.ClienteNotFoundException;
import com.targetmusic.core.domain.exception.cliente.ClienteTemOSEmAbertoException;
import com.targetmusic.core.domain.exception.estoque.EstoqueInsuficienteException;
import com.targetmusic.core.domain.exception.estoque.PecaNotFoundException;
import com.targetmusic.core.domain.exception.instrumento.InstrumentoNotFoundException;
import com.targetmusic.core.domain.exception.instrumento.InstrumentoTemOSEmAbertoException;
import com.targetmusic.core.domain.exception.os.OrdemDeServicoNotFoundException;
import com.targetmusic.core.domain.exception.os.OSNaoPodeSerRemovidaException;
import com.targetmusic.core.domain.exception.os.TransicaoStatusInvalidaException;
import com.targetmusic.core.domain.exception.ModuleDisabledException;
import com.targetmusic.core.domain.exception.auth.TotpSetupRequiredException;
import com.targetmusic.core.domain.exception.avatar.AvatarTooLargeException;
import com.targetmusic.core.domain.exception.avatar.InvalidAvatarFormatException;
import com.targetmusic.core.domain.exception.PermissionAlreadyExistsException;
import com.targetmusic.core.domain.exception.PermissionNotFoundException;
import com.targetmusic.core.domain.exception.RoleAlreadyExistsException;
import com.targetmusic.core.domain.exception.auth.AccountDisabledException;
import com.targetmusic.core.domain.exception.auth.AccountLockedException;
import com.targetmusic.core.domain.exception.auth.DevChallengeExpiredException;
import com.targetmusic.core.domain.exception.auth.InvalidPasswordException;
import com.targetmusic.core.domain.exception.auth.OAuthTokenInvalidException;
import com.targetmusic.core.domain.exception.auth.InvalidRefreshTokenException;
import com.targetmusic.core.domain.exception.auth.InvalidTotpCodeException;
import com.targetmusic.core.domain.exception.auth.PasswordResetTokenExpiredException;
import com.targetmusic.core.domain.exception.auth.PasswordResetTokenNotFoundException;
import com.targetmusic.core.domain.exception.auth.RefreshTokenAlreadyUsedException;
import com.targetmusic.core.domain.exception.auth.TotpAlreadyEnabledException;
import com.targetmusic.core.domain.exception.auth.TotpChallengeExpiredException;
import com.targetmusic.core.domain.exception.auth.TotpCodeRequiredException;
import com.targetmusic.core.domain.exception.auth.TotpNotConsecutiveException;
import com.targetmusic.core.domain.exception.auth.TotpNotEnabledException;
import com.targetmusic.core.domain.exception.auth.RefreshTokenExpiredException;
import com.targetmusic.core.domain.exception.auth.SessionNotFoundException;
import com.targetmusic.core.domain.exception.email.EmailAlreadyVerifiedException;
import com.targetmusic.core.domain.exception.email.EmailDeliveryException;
import com.targetmusic.core.domain.exception.email.EmailVerificationCodeExpiredException;
import com.targetmusic.core.domain.exception.email.EmailVerificationCodeNotFoundException;
import com.targetmusic.core.domain.exception.rbac.RoleNotFoundException;
import com.targetmusic.core.domain.event.AuditEvent;
import com.targetmusic.core.domain.exception.user.EmailAlreadyExistsException;
import com.targetmusic.core.domain.exception.user.UserNotFoundException;
import com.targetmusic.core.domain.exception.user.UsernameAlreadyExistsException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @org.springframework.beans.factory.annotation.Value("${auth.lockout.duration-minutes:15}")
    private long lockoutDurationMinutes;

    @Autowired(required = false)
    private ApplicationEventPublisher publisher;

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex, HttpServletRequest req) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), "USER_NOT_FOUND", req);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ApiError> handleRoleNotFound(RoleNotFoundException ex, HttpServletRequest req) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), "ROLE_NOT_FOUND", req);
    }

    @ExceptionHandler(PermissionNotFoundException.class)
    public ResponseEntity<ApiError> handlePermissionNotFound(PermissionNotFoundException ex, HttpServletRequest req) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), "PERMISSION_NOT_FOUND", req);
    }

    @ExceptionHandler(RoleAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleRoleExists(RoleAlreadyExistsException ex, HttpServletRequest req) {
        return error(HttpStatus.CONFLICT, ex.getMessage(), "ROLE_ALREADY_EXISTS", req);
    }

    @ExceptionHandler(PermissionAlreadyExistsException.class)
    public ResponseEntity<ApiError> handlePermissionExists(PermissionAlreadyExistsException ex, HttpServletRequest req) {
        return error(HttpStatus.CONFLICT, ex.getMessage(), "PERMISSION_ALREADY_EXISTS", req);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUsernameExists(UsernameAlreadyExistsException ex, HttpServletRequest req) {
        return error(HttpStatus.CONFLICT, ex.getMessage(), "USERNAME_ALREADY_EXISTS", req);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailExists(EmailAlreadyExistsException ex, HttpServletRequest req) {
        return error(HttpStatus.CONFLICT, ex.getMessage(), "EMAIL_ALREADY_EXISTS", req);
    }

    @ExceptionHandler(EmailAlreadyVerifiedException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyVerified(EmailAlreadyVerifiedException ex, HttpServletRequest req) {
        return error(HttpStatus.CONFLICT, ex.getMessage(), "EMAIL_ALREADY_VERIFIED", req);
    }

    @ExceptionHandler(EmailVerificationCodeNotFoundException.class)
    public ResponseEntity<ApiError> handleVerificationCodeNotFound(EmailVerificationCodeNotFoundException ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), "VERIFICATION_CODE_INVALID", req);
    }

    @ExceptionHandler(EmailVerificationCodeExpiredException.class)
    public ResponseEntity<ApiError> handleVerificationCodeExpired(EmailVerificationCodeExpiredException ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), "VERIFICATION_CODE_EXPIRED", req);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiError> handleAccountLocked(AccountLockedException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", String.valueOf(lockoutDurationMinutes * 60))
                .body(ApiError.of(ex.getMessage(), "ACCOUNT_LOCKED", req.getRequestURI(), MDC.get("traceId")));
    }

    @ExceptionHandler(AccountDisabledException.class)
    public ResponseEntity<ApiError> handleAccountDisabled(AccountDisabledException ex, HttpServletRequest req) {
        return error(HttpStatus.UNAUTHORIZED, "Credenciais inválidas", "INVALID_CREDENTIALS", req);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleDisabled(DisabledException ex, HttpServletRequest req) {
        return error(HttpStatus.UNAUTHORIZED, "Credenciais inválidas", "INVALID_CREDENTIALS", req);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuth(AuthenticationException ex, HttpServletRequest req) {
        return error(HttpStatus.UNAUTHORIZED, "Credenciais inválidas", "INVALID_CREDENTIALS", req);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        if (publisher != null) {
            publisher.publishEvent(AuditEvent.of(
                    AuditEvent.EventType.ACCESS_DENIED,
                    resolveUsername(),
                    Map.of("path", req.getRequestURI())
            ));
        }
        return error(HttpStatus.FORBIDDEN, "Acesso negado", "ACCESS_DENIED", req);
    }

    private static String resolveUsername() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";
        } catch (Exception ignored) {
            return "anonymous";
        }
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiError> handleInvalidPassword(InvalidPasswordException ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), "INVALID_PASSWORD", req);
    }

    @ExceptionHandler(PasswordResetTokenNotFoundException.class)
    public ResponseEntity<ApiError> handlePasswordResetTokenNotFound(PasswordResetTokenNotFoundException ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), "PASSWORD_RESET_TOKEN_INVALID", req);
    }

    @ExceptionHandler(PasswordResetTokenExpiredException.class)
    public ResponseEntity<ApiError> handlePasswordResetTokenExpired(PasswordResetTokenExpiredException ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), "PASSWORD_RESET_TOKEN_EXPIRED", req);
    }

    @ExceptionHandler(InvalidTotpCodeException.class)
    public ResponseEntity<ApiError> handleInvalidTotpCode(InvalidTotpCodeException ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), "INVALID_TOTP_CODE", req);
    }

    @ExceptionHandler(TotpChallengeExpiredException.class)
    public ResponseEntity<ApiError> handleTotpChallengeExpired(TotpChallengeExpiredException ex, HttpServletRequest req) {
        return error(HttpStatus.UNAUTHORIZED, ex.getMessage(), "TOTP_CHALLENGE_EXPIRED", req);
    }

    @ExceptionHandler(TotpAlreadyEnabledException.class)
    public ResponseEntity<ApiError> handleTotpAlreadyEnabled(TotpAlreadyEnabledException ex, HttpServletRequest req) {
        return error(HttpStatus.CONFLICT, ex.getMessage(), "TOTP_ALREADY_ENABLED", req);
    }

    @ExceptionHandler(TotpSetupRequiredException.class)
    public ResponseEntity<ApiError> handleTotpSetupRequired(TotpSetupRequiredException ex, HttpServletRequest req) {
        return error(HttpStatus.FORBIDDEN, ex.getMessage(), "TOTP_SETUP_REQUIRED", req);
    }

    @ExceptionHandler(TotpNotEnabledException.class)
    public ResponseEntity<ApiError> handleTotpNotEnabled(TotpNotEnabledException ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), "TOTP_NOT_ENABLED", req);
    }

    @ExceptionHandler(ModuleDisabledException.class)
    public ResponseEntity<ApiError> handleModuleDisabled(ModuleDisabledException ex, HttpServletRequest req) {
        return error(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), "MODULE_DISABLED", req);
    }

    @ExceptionHandler(TotpCodeRequiredException.class)
    public ResponseEntity<ApiError> handleTotpCodeRequired(TotpCodeRequiredException ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), "TOTP_CODE_REQUIRED", req);
    }

    @ExceptionHandler(TotpNotConsecutiveException.class)
    public ResponseEntity<ApiError> handleTotpNotConsecutive(TotpNotConsecutiveException ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), "TOTP_NOT_CONSECUTIVE", req);
    }

    @ExceptionHandler(DevChallengeExpiredException.class)
    public ResponseEntity<ApiError> handleDevChallengeExpired(DevChallengeExpiredException ex, HttpServletRequest req) {
        return error(HttpStatus.GONE, ex.getMessage(), "DEV_CHALLENGE_EXPIRED", req);
    }

    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ApiError> handleSessionNotFound(SessionNotFoundException ex, HttpServletRequest req) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), "SESSION_NOT_FOUND", req);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiError> handleInvalidRefreshToken(InvalidRefreshTokenException ex, HttpServletRequest req) {
        return error(HttpStatus.UNAUTHORIZED, "Token de atualização inválido", "INVALID_REFRESH_TOKEN", req);
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<ApiError> handleRefreshTokenExpired(RefreshTokenExpiredException ex, HttpServletRequest req) {
        return error(HttpStatus.UNAUTHORIZED, "Sessão expirada — faça login novamente", "REFRESH_TOKEN_EXPIRED", req);
    }

    @ExceptionHandler(RefreshTokenAlreadyUsedException.class)
    public ResponseEntity<ApiError> handleRefreshTokenReuse(RefreshTokenAlreadyUsedException ex, HttpServletRequest req) {
        return error(HttpStatus.UNAUTHORIZED, "Sessão inválida — faça login novamente", "REFRESH_TOKEN_REUSED", req);
    }

    @ExceptionHandler(OAuthTokenInvalidException.class)
    public ResponseEntity<ApiError> handleOAuthTokenInvalid(OAuthTokenInvalidException ex, HttpServletRequest req) {
        return error(HttpStatus.UNAUTHORIZED, ex.getMessage(), "OAUTH_TOKEN_INVALID", req);
    }

    @ExceptionHandler(AvatarTooLargeException.class)
    public ResponseEntity<ApiError> handleAvatarTooLarge(AvatarTooLargeException ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), "AVATAR_TOO_LARGE", req);
    }

    @ExceptionHandler(InvalidAvatarFormatException.class)
    public ResponseEntity<ApiError> handleInvalidAvatarFormat(InvalidAvatarFormatException ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), "INVALID_AVATAR_FORMAT", req);
    }

    @ExceptionHandler(EmailDeliveryException.class)
    public ResponseEntity<ApiError> handleEmailDelivery(EmailDeliveryException ex, HttpServletRequest req) {
        // Conta criada, mas email não entregue. Cliente deve orientar o usuário a usar resend-verification.
        return error(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), "EMAIL_DELIVERY_FAILED", req);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, "Requisição inválida", "BAD_REQUEST", req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return error(HttpStatus.BAD_REQUEST, message, "VALIDATION_ERROR", req);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        String message = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining(", "));
        return error(HttpStatus.BAD_REQUEST, message, "VALIDATION_ERROR", req);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableBody(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido ou ausente", "UNREADABLE_BODY", req);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String message = "Valor inválido para o parâmetro '" + ex.getName() + "'";
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            String valid = Arrays.stream(ex.getRequiredType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            message += ". Valores aceitos: " + valid;
        }
        return error(HttpStatus.BAD_REQUEST, message, "INVALID_ENUM_VALUE", req);
    }

    @ExceptionHandler(ClienteNotFoundException.class)
    public ResponseEntity<ApiError> handleClienteNotFound(ClienteNotFoundException ex, HttpServletRequest req) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), "CLIENTE_NOT_FOUND", req);
    }

    @ExceptionHandler(ClienteTemOSEmAbertoException.class)
    public ResponseEntity<ApiError> handleClienteTemOS(ClienteTemOSEmAbertoException ex, HttpServletRequest req) {
        return error(HttpStatus.CONFLICT, ex.getMessage(), "CLIENTE_TEM_OS_ABERTA", req);
    }

    @ExceptionHandler(InstrumentoNotFoundException.class)
    public ResponseEntity<ApiError> handleInstrumentoNotFound(InstrumentoNotFoundException ex, HttpServletRequest req) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), "INSTRUMENTO_NOT_FOUND", req);
    }

    @ExceptionHandler(InstrumentoTemOSEmAbertoException.class)
    public ResponseEntity<ApiError> handleInstrumentoTemOS(InstrumentoTemOSEmAbertoException ex, HttpServletRequest req) {
        return error(HttpStatus.CONFLICT, ex.getMessage(), "INSTRUMENTO_TEM_OS_ABERTA", req);
    }

    @ExceptionHandler(OrdemDeServicoNotFoundException.class)
    public ResponseEntity<ApiError> handleOSNotFound(OrdemDeServicoNotFoundException ex, HttpServletRequest req) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), "OS_NOT_FOUND", req);
    }

    @ExceptionHandler(TransicaoStatusInvalidaException.class)
    public ResponseEntity<ApiError> handleTransicaoInvalida(TransicaoStatusInvalidaException ex, HttpServletRequest req) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), "TRANSICAO_STATUS_INVALIDA", req);
    }

    @ExceptionHandler(OSNaoPodeSerRemovidaException.class)
    public ResponseEntity<ApiError> handleOSNaoPodeSerRemovida(OSNaoPodeSerRemovidaException ex, HttpServletRequest req) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), "OS_NAO_PODE_SER_REMOVIDA", req);
    }

    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<ApiError> handleEstoqueInsuficiente(EstoqueInsuficienteException ex, HttpServletRequest req) {
        return error(HttpStatus.CONFLICT, ex.getMessage(), "ESTOQUE_INSUFICIENTE", req);
    }

    @ExceptionHandler(PecaNotFoundException.class)
    public ResponseEntity<ApiError> handlePecaNotFound(PecaNotFoundException ex, HttpServletRequest req) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), "PECA_NOT_FOUND", req);
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleNoResource(HttpServletRequest req) {
        return error(HttpStatus.NOT_FOUND, "Recurso não encontrado", "NOT_FOUND", req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno inesperado", "INTERNAL_ERROR", req);
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String message, String code, HttpServletRequest req) {
        return ResponseEntity.status(status)
                .body(ApiError.of(message, code, req.getRequestURI(), MDC.get("traceId")));
    }
}

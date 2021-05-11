package pl.dietadvisor.common.shared.exception.custom;

import static java.lang.String.format;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message, Object... arguments) {
        super(format(message, arguments));
    }
}

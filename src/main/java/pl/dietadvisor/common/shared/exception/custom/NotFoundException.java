package pl.dietadvisor.common.shared.exception.custom;

import static java.lang.String.format;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message, Object... arguments) {
        super(format(message, arguments));
    }
}

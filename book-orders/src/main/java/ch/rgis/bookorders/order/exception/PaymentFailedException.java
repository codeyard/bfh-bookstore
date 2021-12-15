package ch.rgis.bookorders.order.exception;

public class PaymentFailedException extends Exception {

    ErrorCode errorCode;

    public PaymentFailedException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getCode() {
        return errorCode;
    }

    public enum ErrorCode {
        AMOUNT_EXCEEDS_LIMIT,
        CREDIT_CARD_EXPIRED,
        INVALID_CREDIT_CARD
    }

}

package simo.com.alco.api;

/**
 * Represents API constant values for order statuses.
 */
public enum OrderStatuses {
    WAITING_PROCESSING(1),
    PROCESSING(2),
    SHIPPED(3),
    DELIVERING(4),
    DELIVERED(5),
    CANCELLED(6),
    WAITING_PAYMENT(7),
    PAYED(8);

    int value;

    int getValue() {
        return value;
    }

    void setValue(int x) {
        value = x;
    }

    OrderStatuses(int statusValue) {
        setValue(statusValue);
    }

    public static OrderStatuses fromValue(int x) {
        for (OrderStatuses type : values()) {
            if (type.value == x) {
                return type;
            }
        }
        return null;
    }
}

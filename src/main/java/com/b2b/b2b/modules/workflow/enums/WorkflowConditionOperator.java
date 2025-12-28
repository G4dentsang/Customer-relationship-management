package com.b2b.b2b.modules.workflow.enums;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public enum WorkflowConditionOperator {

    EQUALS {
        public boolean apply(String actual, String expected) {
            return Objects.equals(actual.toLowerCase(), expected.toLowerCase());
        }
    },

    NOT_EQUALS {
        public boolean apply(String actual, String expected) {
            return !Objects.equals(actual.toLowerCase(), expected.toLowerCase());
        }
    },

    LESS_THAN {
        public boolean apply(String actual, String expected) {
            return parseDouble(actual) < parseDouble(expected);
        }
    },

    GREATER_THAN {
        public boolean apply(String actual, String expected) {
            return parseDouble(actual) > parseDouble(expected);
        }
    },

    LESS_OR_EQUALS {
        public boolean apply(String actual, String expected) {
            return parseDouble(actual) <= parseDouble(expected);
        }
    },

    GREATER_OR_EQUALS {
        public boolean apply(String actual, String expected) {
            return parseDouble(actual) >= parseDouble(expected);
        }
    },

    /***********Strings*********/

    CONTAINS {
        public boolean apply(String actual, String expected) {
            return actual != null && actual.toLowerCase().contains(expected.toLowerCase());
        }
    },

    NOT_CONTAINS {
        public boolean apply(String actual, String expected) {
            return actual == null || !actual.toLowerCase().contains(expected.toLowerCase());
        }
    },

    STARTS_WITH {
        public boolean apply(String actual, String expected) {
            return actual != null && actual.startsWith(expected);
        }
    },

    ENDS_WITH {
        public boolean apply(String actual, String expected) {
            return actual != null && actual.endsWith(expected);
        }
    },

    /**************Dates**************/

    BEFORE {
        public boolean apply(String actual, String expected) {
            return parseDate(actual).isBefore(parseDate(expected));
        }
    },

    AFTER {
        public boolean apply(String actual, String expected) {
            return parseDate(actual).isAfter(parseDate(expected));
        }
    },

    /**************Others***************/

    IS_EMPTY {
        public boolean apply(String actual, String expected) {
            return actual == null || actual.isEmpty();
        }
    },

    IS_NOT_EMPTY {
        public boolean apply(String actual, String expected) {
            return actual != null && !actual.isEmpty();
        }
    },

    IS_TRUE {
        public boolean apply(String actual, String expected) {
            return Boolean.parseBoolean(actual);
        }
    },

    IS_FALSE {
        public boolean apply(String actual, String expected) {
            return !Boolean.parseBoolean(actual);
        }
    };

    public abstract boolean apply(String actual, String expected);


    /**************Helpers**************/

    private static double parseDouble(String value) {
        try {
            return (value == null || value.isEmpty()) ? 0 : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static LocalDate parseDate(String value) {
        try {
            return (value == null || value.isEmpty()) ? LocalDate.MIN : LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            return LocalDate.MIN;
        }
    }

    private static LocalDateTime parseDateTime(String value) {
        try {
            return (value == null || value.isEmpty()) ? LocalDateTime.MIN : LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            return LocalDateTime.MIN;
        }
    }
}

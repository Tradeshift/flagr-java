package com.tradeshift.flagr;

public class FlagrException extends RuntimeException {
    FlagrException(String s) {
        super(s);
    }

    FlagrException(String s, Iterable<String> errorList) {
        super(String.format("%s:\n%s", s, String.join("\n", errorList)));
    }
}

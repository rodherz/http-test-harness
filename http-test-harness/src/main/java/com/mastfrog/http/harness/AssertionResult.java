/*
 * The MIT License
 *
 * Copyright 2022 Tim Boudreau.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.mastfrog.http.harness;

import com.fasterxml.jackson.annotation.JsonIgnore;
import static com.mastfrog.util.preconditions.Checks.notNull;
import com.mastfrog.util.strings.Escaper;
import com.mastfrog.util.strings.Strings;
import static java.nio.charset.StandardCharsets.US_ASCII;
import java.util.Objects;

/**
 * The result of running one assertion.
 *
 * @author Tim Boudreau
 */
public final class AssertionResult {

    private final AssertionStatus status;
    private final FailureSeverity severity;
    private final Object actualValue;
    private final String message;

    public AssertionResult(
            AssertionStatus status,
            FailureSeverity severity,
            String message, Object actualValue) {
        this.status = notNull("status", status);
        this.severity = notNull("severity", severity);
        this.message = notNull("message", message);
        this.actualValue = actualValue;
    }

    public String message() {
        return message;
    }

    public AssertionStatus status() {
        return status;
    }

    public FailureSeverity severity() {
        return severity;
    }

    public final Object actualValue() {
        return actualValue;
    }

    @JsonIgnore
    public boolean isOk() {
        return !status.isFailure();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        status.ifFailure(() -> sb.append(severity).append(": "));
        sb.append(status).append(' ').append(message);
        sb.append(" (value: '").append(
                Strings.escape(Objects.toString(actualValue),
                        Escaper.escapeUnencodableAndControlCharacters(US_ASCII)));
        String type = actualValue == null
                ? "null"
                : actualValue instanceof Throwable
                        ? Strings.toString((Throwable) actualValue)
                        : actualValue.getClass().getName();
        sb.append(actualValue instanceof Throwable
                ? "' with stack\n"
                : "' of type ")
                .append(type)
                .append(')');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.status);
        hash = 97 * hash + Objects.hashCode(this.severity);
        hash = 97 * hash + Objects.hashCode(this.actualValue);
        hash = 97 * hash + Objects.hashCode(this.message);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || obj.getClass() != AssertionResult.class) {
            return false;
        }
        final AssertionResult other = (AssertionResult) obj;

        return status == other.status && severity == other.severity
                && message.equals(other.message)
                && Objects.equals(actualValue, other.actualValue);
    }
}

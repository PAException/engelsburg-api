package io.github.paexception.engelsburginfrastructure.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Result<T> {

    private T result;
    private Error error;
    private String extra;

    public Object getHttpResponse() {
        return this.isErrorPresent() ? this.getError().copyWithExtra(this.getExtra()) : this.getResult();
    }

    public boolean isResultPresent() {
        return this.getResult() != null;
    }

    public boolean isExtraPresent() {
        return this.getExtra() != null;
    }

    public boolean isErrorPresent() {
        return this.getError() != null;
    }

    public boolean isResultNotPresent() {
        return !this.isResultPresent();
    }

    public boolean isExtraNotPresent() {
        return !this.isExtraPresent();
    }

    public boolean isErrorNotPresent() {
        return !this.isErrorPresent();
    }

    public boolean isEmpty() {
        return this.getResult() == null && this.getError() == null && this.getExtra() == null;
    }

    public boolean isNotEmpty() {
        return !this.isEmpty();
    }

    public static <T> Result<T> empty() {
        return new Result<>();
    }

    public static <T> Result<T> of(T result) {
        return of(result, null);
    }

    public static <T> Result<T> of(T result, String extra) {
        Result<T> instance = new Result<>();
        instance.setResult(result);
        if (extra != null) instance.setExtra(extra);
        return instance;
    }

    public static <T> Result<T> of(Error error) {
        return of(error, null);
    }

    public static <T> Result<T> of(Error error, String extra) {
        Result<T> instance = new Result<>();
        instance.setError(error);
        if (extra != null) instance.setExtra(extra);
        return instance;
    }

    /**
     * Maps the the given instance to an instance with other generics.
     *
     * @param <T> The new generics
     * @param instance The instance to map
     * @return the given instance
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Result<T> ret(Result instance) {
        return instance;
    }

}

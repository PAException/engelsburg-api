package io.github.paexception.engelsburg.api.util;

import io.github.paexception.engelsburg.api.EngelsburgAPI;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class to return on endpoints
 * It can handle any type as well as errors and format them properly as a http response
 * @param <T>
 */
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Result<T> {

    private static MessageDigest digest;
    private T result;
    private Error error;
    private String extra;

    /**
     * Convert Result into a HttpResponse
     * @return ResponseEntity for spring
     */
    public ResponseEntity<Object> getHttpResponse() {
        Object response = this.isErrorPresent() ? this.getError().copyWithExtra(this.getExtra()).getBody() : this.getResult();

        return ResponseEntity.status(this.isErrorPresent() ? this.error.getStatus() : HttpStatus.OK.value())
                .header("Hash", hash(response)).body(response);
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

    /**
     * Hash function for header on response
     * @param o The object o to hash
     * @return hash of o
     */
    public static String hash(Object o) {
        try {
            if (digest == null) digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            EngelsburgAPI.getLOGGER().error("Couldn't find defined Algorithm");
        }

        return bytesToHex(digest.digest(o.toString().getBytes()));
    }

    /**
     * Function to convert a bytearray into a hex string
     * @param hash bytearray to convert to hex string
     * @return hex string
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

}

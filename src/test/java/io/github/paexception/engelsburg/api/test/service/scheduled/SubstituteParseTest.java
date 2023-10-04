package io.github.paexception.engelsburg.api.test.service.scheduled;

import io.github.paexception.engelsburg.api.service.scheduled.SubstituteUpdateService;
import org.junit.jupiter.api.Test;

public class SubstituteParseTest {

    @Test
    public void testSubstituteParse() {
        new SubstituteUpdateService(null, null, null).updateSubstitutes();
    }

}

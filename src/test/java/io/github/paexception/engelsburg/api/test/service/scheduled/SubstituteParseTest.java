package io.github.paexception.engelsburg.api.test.service.scheduled;

import io.github.paexception.engelsburg.api.endpoint.dto.SubstituteDTO;
import io.github.paexception.engelsburg.api.service.scheduled.SubstituteUpdateService;

import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.Objects;

public class SubstituteParseTest {

    @Test
    public void testSubstituteParse() {
        new SubstituteUpdateService(null, null, null).updateSubstitutes();
    }
    
    @Test
    public void substituteDTOEqualTest() {
        //Correct 5a - 10e
        SubstituteDTO dto1 = new SubstituteDTO(
                new Date(169879761),
                "10c",
                2,
                "M",
                "BSU",
                "GAR",
                "Vertretung",
                "Mo-21.2. / 4",
                "H301",
                "Aufg. vorhanden"
        );
        SubstituteDTO dto2 = new SubstituteDTO(
                new Date(169879761),
                "10c",
                2,
                "D",
                "GRB",
                "KLE",
                "Betreuung",
                "asg",
                "dshssdssh",
                "dshgdhsen"
        );

        assert Objects.equals(dto1, dto2);

        //Different lesson 5a - 10e
        SubstituteDTO dto3 = new SubstituteDTO(
                new Date(169879761),
                "10a",
                2,
                "M",
                "BSU",
                "GAR",
                "Vertretung",
                "Mo-21.2. / 4",
                "H301",
                "Aufg. vorhanden"
        );
        SubstituteDTO dto4 = new SubstituteDTO(
                new Date(169879761),
                "10c",
                3,
                "D",
                "GRB",
                "KLE",
                "Betreuung",
                "asg",
                "dshssdssh",
                "dshgdhsen"
        );

        assert !Objects.equals(dto3, dto4);

        //Different class
        SubstituteDTO dto5 = new SubstituteDTO(
                new Date(169879761),
                "Q3",
                2,
                "M",
                "BSU",
                "GAR",
                "Vertretung",
                "Mo-21.2. / 4",
                "H301",
                "Aufg. vorhanden"
        );
        SubstituteDTO dto6 = new SubstituteDTO(
                new Date(169879761),
                "10c",
                2,
                "D",
                "GRB",
                "KLE",
                "Betreuung",
                "asg",
                "dshssdssh",
                "dshgdhsen"
        );

        assert !Objects.equals(dto5, dto6);

        //Different date
        SubstituteDTO dto7 = new SubstituteDTO(
                new Date(169761),
                "10c",
                2,
                "M",
                "BSU",
                "GAR",
                "Vertretung",
                "Mo-21.2. / 4",
                "H301",
                "Aufg. vorhanden"
        );
        SubstituteDTO dto8 = new SubstituteDTO(
                new Date(169879761),
                "10c",
                2,
                "D",
                "GRB",
                "KLE",
                "Betreuung",
                "asg",
                "dshssdssh",
                "dshgdhsen"
        );

        assert !Objects.equals(dto7, dto8);

        //Different teacher E1 - Q4
        SubstituteDTO dto9 = new SubstituteDTO(
                new Date(169761),
                "Q3",
                2,
                "M",
                "BSU",
                "GAR",
                "Vertretung",
                "Mo-21.2. / 4",
                "H301",
                "Aufg. vorhanden"
        );
        SubstituteDTO dto10 = new SubstituteDTO(
                new Date(169879761),
                "Q3",
                2,
                "D",
                "GRB",
                "KLE",
                "Betreuung",
                "asg",
                "dshssdssh",
                "dshgdhsen"
        );

        assert !Objects.equals(dto9, dto10);

        //Correct teacher E1 - Q4
        SubstituteDTO dto11 = new SubstituteDTO(
                new Date(169761),
                "Q3",
                2,
                "M",
                "BSU",
                "GAR",
                "Vertretung",
                "Mo-21.2. / 4",
                "H301",
                "Aufg. vorhanden"
        );
        SubstituteDTO dto12 = new SubstituteDTO(
                new Date(169879761),
                "Q3",
                2,
                "D",
                "BSU",
                "KLE",
                "Betreuung",
                "asg",
                "dshssdssh",
                "dshgdhsen"
        );

        assert !Objects.equals(dto11, dto12);
    }
}

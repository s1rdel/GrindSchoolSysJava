package com.example.grindschool.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SessionBuilderTest {

    private final Subject subject = new Subject();
    private final Tutor tutor = new Tutor();
    private final LocalDate date = LocalDate.of(2030, 6, 15);
    private final LocalTime time = LocalTime.of(14, 30);

    @Test
    void buildsSessionWithOnePlaceAndOpenStatusByDefault() {
        Session session = validBuilder().build();

        assertAll(
                () -> assertSame(subject, session.getSubject()),
                () -> assertSame(tutor, session.getTutor()),
                () -> assertEquals(date, session.getDate()),
                () -> assertEquals(time, session.getTime()),
                () -> assertEquals(1, session.getTotalCapacity()),
                () -> assertEquals(SessionStatus.OPEN, session.getStatus())
        );
    }

    @Test
    void usesExplicitStatus() {
        Session session = validBuilder().status(SessionStatus.CANCELLED).build();

        assertEquals(SessionStatus.CANCELLED, session.getStatus());
    }

    @ParameterizedTest(name = "rejects capacity {0}")
    @ValueSource(ints = {0, -1})
    void rejectsNonPositiveCapacity(int capacity) {
        SessionBuilder builder = validBuilder().capacity(capacity);

        assertThrows(IllegalStateException.class, builder::build);
    }

    @ParameterizedTest(name = "rejects missing {0}")
    @MethodSource("requiredFields")
    void rejectsMissingRequiredField(String field, Consumer<SessionBuilder> clearField) {
        SessionBuilder builder = validBuilder();
        clearField.accept(builder);

        assertThrows(IllegalStateException.class, builder::build);
    }

    private static Stream<Arguments> requiredFields() {
        return Stream.of(
                requiredField("subject", builder -> builder.subject(null)),
                requiredField("tutor", builder -> builder.tutor(null)),
                requiredField("date", builder -> builder.date(null)),
                requiredField("time", builder -> builder.time(null)),
                requiredField("status", builder -> builder.status(null))
        );
    }

    private static Arguments requiredField(String name, Consumer<SessionBuilder> clearField) {
        return Arguments.of(name, clearField);
    }

    private SessionBuilder validBuilder() {
        return new SessionBuilder()
                .subject(subject)
                .tutor(tutor)
                .date(date)
                .time(time)
                .capacity(1);
    }
}
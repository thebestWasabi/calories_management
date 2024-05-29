package ru.javawebinar.topjava.model;

import java.time.LocalDateTime;

public class UserMealWithExcess {

    private final LocalDateTime dateTime;
    private final String description;
    private final int calories;
    private final boolean excess;

    public UserMealWithExcess(final LocalDateTime dateTime, final String description,
                              final int calories, final boolean excess) {
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
        this.excess = excess;
    }

    @Override
    public String toString() {
        return "UserMealWithExcess {dateTime=%s, description='%s', calories=%d, excess=%s}"
                .formatted(dateTime, description, calories, excess);
    }
}

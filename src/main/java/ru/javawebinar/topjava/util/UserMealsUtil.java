package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserMealsUtil {

    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        final var mealsTo = filteredByCyclesV2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        final var mealWithOptional = cyclesWithOptional(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealWithOptional.ifPresent(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(final List<UserMeal> meals,
                                                            final LocalTime startTime, final LocalTime endTime,
                                                            final int caloriesPerDay
    ) {
        final Map<LocalDate, Integer> caloriesForDay = new HashMap<>();
        for (final UserMeal meal : meals) {
            final var localDate = meal.getDateTime().toLocalDate();
            caloriesForDay.put(localDate, caloriesForDay.getOrDefault(localDate, 0) + meal.getCalories());
        }

        final List<UserMealWithExcess> result = new ArrayList<>();
        for (final UserMeal meal : meals) {
            final var dateTime = meal.getDateTime();
            final var localTime = dateTime.toLocalTime();

            if (TimeUtil.isBetweenHalfOpen(localTime, startTime, endTime)) {
                final var localDate = dateTime.toLocalDate();
                final var excessResult = caloriesForDay.get(localDate) >= caloriesPerDay;

                result.add(new UserMealWithExcess(dateTime, meal.getDescription(), meal.getCalories(), excessResult));
            }
        }

        return result;
    }

    // Написал этот метод чисто для Д/З, через метод merge
    public static List<UserMealWithExcess> filteredByCyclesV2(final List<UserMeal> meals,
                                                              final LocalTime startTime, final LocalTime endTime,
                                                              final int caloriesPerDay
    ) {
        final Map<LocalDate, Integer> caloriesForDay = new HashMap<>();
        for (final UserMeal meal : meals) {
            final LocalDateTime dateTime = meal.getDateTime();
            final LocalDate localDate = dateTime.toLocalDate();
            final int calories = meal.getCalories();
            caloriesForDay.merge(localDate, calories, Integer::sum);
        }

        final List<UserMealWithExcess> result = new ArrayList<>();
        for (final UserMeal meal : meals) {
            final LocalDateTime dateTime = meal.getDateTime();
            final LocalTime localTime = dateTime.toLocalTime();

            if (TimeUtil.isBetweenHalfOpen(localTime, startTime, endTime)) {
                final LocalDate localDate = dateTime.toLocalDate();
                final boolean excessResult = caloriesForDay.get(localDate) >= caloriesPerDay;

                result.add(new UserMealWithExcess(dateTime, meal.getDescription(), meal.getCalories(), excessResult));
            }
        }

        return result;
    }

    public static Optional<List<UserMealWithExcess>> cyclesWithOptional(final List<UserMeal> meals,
                                                                        final LocalTime startTime, final LocalTime endTime,
                                                                        final int caloriesPerDay
    ) {
        List<UserMealWithExcess> result = filteredByCycles(meals, startTime, endTime, caloriesPerDay);
        return Optional.of(result);
    }

    public static List<UserMealWithExcess> filteredByStreams(final List<UserMeal> meals,
                                                             final LocalTime startTime, final LocalTime endTime,
                                                             final int caloriesPerDay
    ) {
        // Подсчет калорий за каждый день
        final var caloriesForDay = meals.stream()
                .collect(Collectors.groupingBy(meal ->
                        meal.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories)));

        // Фильтрация и преобразование в UserMealWithExcess
        return meals.stream()
                .filter(meal -> {
                    final var localTime = meal.getDateTime().toLocalTime();
                    return TimeUtil.isBetweenHalfOpen(localTime, startTime, endTime);
                })
                .map(meal -> {
                    final var localDate = meal.getDateTime().toLocalDate();
                    final var excessResult = caloriesForDay.get(localDate) >= caloriesPerDay;
                    return new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excessResult);
                })
                .collect(Collectors.toList());
    }

    public static Optional<List<UserMealWithExcess>> StreamsWithOptional(final List<UserMeal> meals,
                                                                         final LocalTime startTime, final LocalTime endTime,
                                                                         final int caloriesPerDay
    ) {
        // Получение отфильтрованного списка
        final var result = filteredByStreams(meals, startTime, endTime, caloriesPerDay);
        return result.isEmpty() ? Optional.empty() : Optional.of(result);
    }
}

package ru.java.project.metro;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class TicketCounter {

    private final Map<LocalDate, Integer> incomeByDate;

    public TicketCounter() {
        this.incomeByDate = new HashMap<>();
    }

    public void addIncome(LocalDate date, int amount) {
        incomeByDate.merge(date, amount, Integer::sum);
    }

    public int getIncomeByDate(LocalDate date) {
        return incomeByDate.getOrDefault(date, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TicketCounter that = (TicketCounter) o;
        return Objects.equals(incomeByDate, that.incomeByDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(incomeByDate);
    }
}

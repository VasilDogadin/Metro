package ru.java.project.metro;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class Station {

    private final String name;
    private final Station previousStation;
    private Station nextStation;
    private final Duration durationToNextStation;
    private final Line line;
    private final List<String> interchangeLines;
    private final Metro metro;
    private TicketCounter ticketCounter;
    private final Map<String, LocalDate> seasonTickets = new HashMap<>();

    public Station(String name, Station previousStation, Station nextStation, Duration durationToNextStation,
                   Line line, List<String> interchangeLines, Metro metro) {
        this.name = name;
        this.previousStation = previousStation;
        this.nextStation = nextStation;
        this.durationToNextStation = durationToNextStation;
        this.line = line;
        this.interchangeLines = interchangeLines;
        this.metro = metro;
    }

    public void addInterchangeLine(String line) {
        interchangeLines.add(line);
    }

    public String sellSeasonTicket(LocalDate saleDate) {
        String ticketNumber = generateTicketNumber();
        LocalDate endDate = saleDate.plusMonths(1);
        seasonTickets.put(ticketNumber, endDate);
        return ticketNumber;
    }

    public boolean isSeasonTicketValid(String ticketNumber, LocalDate currentDate) {
        LocalDate endDate = seasonTickets.get(ticketNumber);
        return endDate != null && !currentDate.isAfter(endDate);
    }

    public void extendSeasonTicket(String ticketNumber, LocalDate purchaseDate) {
        LocalDate endDate = seasonTickets.get(ticketNumber);
        if (endDate != null) {
            endDate = endDate.plusMonths(1);
            seasonTickets.put(ticketNumber, endDate);
            metro.addIncome(purchaseDate, 3000);
        }
    }

    private String generateTicketNumber() {
        AtomicInteger counter = new AtomicInteger(0);
        return String.format("a%04d", counter.getAndIncrement());
    }

    public String getName() {
        return name;
    }

    public Station getNextStation() {
        return nextStation;
    }

    public void setNextStation(Station nextStation) {
        this.nextStation = nextStation;
    }

    public List<String> getInterchangeLines() {
        return interchangeLines;
    }

    public Station getPreviousStation() {
        return previousStation;
    }

    public Line getLine() {
        return line;
    }

    public TicketCounter getTicketCounter() {
        return ticketCounter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Station station = (Station) o;
        return Objects.equals(name, station.name)
                && Objects.equals(previousStation, station.previousStation)
                && Objects.equals(nextStation, station.nextStation)
                && Objects.equals(durationToNextStation, station.durationToNextStation)
                && Objects.equals(line, station.line)
                && Objects.equals(interchangeLines, station.interchangeLines)
                && Objects.equals(metro, station.metro)
                && Objects.equals(ticketCounter, station.ticketCounter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, previousStation, nextStation, durationToNextStation, line, interchangeLines, metro,
                ticketCounter);
    }

    @Override
    public String toString() {
        return "Station{name='" + name + "', changeLines=" + interchangeLines + "}";
    }
}

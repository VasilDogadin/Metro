package ru.java.project.metro;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

public final class Metro {

    private final String city;
    private final List<Line> lines;
    private final Map<LocalDate, Integer> incomeByDate = new HashMap<>();

    public Metro(String city, List<Line> lines) {
        this.city = city;
        this.lines = lines;
    }

    public void createNewLine(String color) {
        if (lines.stream().anyMatch(line -> line.getColor().equals(color))) {
            throw new RuntimeException("Линия с таким цветом уже существует.");
        }
        lines.add(new Line(color, this));
    }

    public void createFirstStation(String color, String stationName, List<String> interchangeStations) {
        Line targetLine = getLineByColor(color);
        if (targetLine.getStations().stream().anyMatch(station -> station.getName().equals(stationName))) {
            throw new RuntimeException("Станция с таким именем уже существует в данной линии.");
        }

        if (!targetLine.getStations().isEmpty()) {
            throw new RuntimeException("Внутри линии уже есть станции.");
        }
        targetLine.getStations().add(new Station(stationName, null, null, Duration.ZERO,
                targetLine, interchangeStations, this));
    }

    public void createTerminalStation(String color, String stationName, Duration durationToStation,
                                      List<String> interchangeStations) {
        Line targetLine = getLineByColor(color);
        Station previousStation = targetLine.getStations().stream().reduce((first, second) -> second)
                .orElseThrow(() -> new RuntimeException("Предыдущая станция не существует."));

        if (previousStation.getNextStation() != null) {
            throw new RuntimeException("Предыдущая станция уже имеет следующую станцию.");
        }
        if (durationToStation.isNegative() || durationToStation.isZero()) {
            throw new RuntimeException("Время перегона должно быть положительным.");
        }
        if (targetLine.getStations().stream().anyMatch(station -> station.getName().equals(stationName))) {
            throw new RuntimeException("Станция с таким именем уже существует в данной линии.");
        }

        Station terminalStation = new Station(stationName, previousStation, null, durationToStation,
                targetLine, interchangeStations, this);
        previousStation.setNextStation(terminalStation);
        targetLine.addStation(terminalStation);
    }

    private Line getLineByColor(String color) {
        return lines.stream()
                .filter(line -> line.getColor().equals(color))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Линия с таким цветом не найдена."));
    }

    public void checkStationExists(String stationName) {
        if (lines.stream().allMatch(line -> line.getStations()
                .stream().noneMatch(station -> station.getName().equals(stationName)))) {
            throw new RuntimeException("Станции с именем " + stationName + " не существует в данной линии.");
        }
    }

    public Station findInterchangeStation(String lineFrom, String lineTo) {
        Optional<Station> interchangeStation = lines.stream()
                .filter(line -> line.getColor().equals(lineFrom))
                .flatMap(line -> line.getStations().stream())
                .filter(station -> station.getInterchangeLines() != null
                        && station.getInterchangeLines().contains(lineTo))
                .findFirst();

        interchangeStation.ifPresent(station -> {
            if (station != null) {
                station.addInterchangeLine(lineTo);
            }
        });

        return interchangeStation.orElse(null);
    }

    public long countStationsBetween(Station start, Station end) {
        return Stream.iterate(start, Objects::nonNull, Station::getNextStation)
                .takeWhile(station -> !station.equals(end))
                .count();
    }

    public long countStationsBetweenReverse(Station start, Station end) {
        return Stream.iterate(start, Objects::nonNull, Station::getPreviousStation)
                .takeWhile(station -> !station.equals(end))
                .count();
    }

    public long countStationsBetweenOnSameLine(Station start, Station end) {
        long forwardCount = Stream.iterate(start, Objects::nonNull, Station::getNextStation)
                .takeWhile(station -> !station.equals(end))
                .count();
        if (forwardCount != -1) {
            return forwardCount;
        }

        long reverseCount = Stream.iterate(start, Objects::nonNull, Station::getPreviousStation)
                .takeWhile(station -> !station.equals(end))
                .count();
        if (reverseCount != -1) {
            return reverseCount;
        }
        throw new RuntimeException("Нет пути из станции " + start.getName() + " в " + end.getName());
    }

    public int countStations(String startStationName, String endStationName) {
        Station startStation = findStationByName(startStationName);
        Station endStation = findStationByName(endStationName);

        if (startStation == null || endStation == null) {
            throw new RuntimeException("Одна из станций не найдена.");
        }

        if (startStation.equals(endStation)) {
            throw new RuntimeException("Начальная и конечная станции совпадают.");
        }

        if (startStation.getLine() == endStation.getLine()) {
            return (int) countStationsBetweenOnSameLine(startStation, endStation);
        } else {
            Station interchangeStation = findInterchangeStation(startStation.getLine().getColor(),
                    endStation.getLine().getColor());

            long countToInterchange = Stream.iterate(startStation, Objects::nonNull, Station::getNextStation)
                    .takeWhile(station -> !station.equals(interchangeStation))
                    .count();

            long countFromInterchange = Stream.iterate(interchangeStation, Objects::nonNull, Station::getNextStation)
                    .takeWhile(station -> !station.equals(endStation))
                    .count();

            return (int) (countToInterchange + countFromInterchange);
        }
    }

    public void sellTicket(LocalDate purchaseDate, String startStationName, String endStationName) {
        Station startStation = findStationByName(startStationName);
        Station endStation = findStationByName(endStationName);

        if (startStation == null || endStation == null) {
            throw new RuntimeException("Одна из станций не найдена.");
        }

        if (startStation.equals(endStation)) {
            throw new RuntimeException("Начальная и конечная станции совпадают.");
        }

        int numberOfStations = countStations(startStationName, endStationName);
        int ticketPrice = numberOfStations * 5 + 20;

        startStation.getTicketCounter().addIncome(purchaseDate, ticketPrice);
    }

    private Station findStationByName(String name) {
        return lines.stream()
                .flatMap(line -> line.getStations().stream())
                .filter(station -> station.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public boolean isSeasonTicketValid(String ticketNumber, LocalDate currentDate) {
        return lines.stream()
                .flatMap(line -> line.getStations().stream())
                .anyMatch(station -> station.isSeasonTicketValid(ticketNumber, currentDate));
    }

    public void extendSeasonTicket(String ticketNumber, LocalDate purchaseDate) {
        lines.forEach(line -> line.getStations()
                .forEach(station -> station.extendSeasonTicket(ticketNumber, purchaseDate)));
    }

    public void printIncomeByDate() {
        incomeByDate.forEach((date, income) -> System.out.println(date + " - " + income));
    }

    public void addIncome(LocalDate date, int amount) {
        incomeByDate.merge(date, amount, Integer::sum);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Metro metro = (Metro) o;
        return Objects.equals(lines, metro.lines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lines);
    }

    @Override
    public String toString() {
        return "Metro{city='" + city + "', lines=" + lines + "}";
    }
}

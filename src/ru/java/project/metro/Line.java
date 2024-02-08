package ru.java.project.metro;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Line {

    private final String color;
    private final List<Station> stations;
    private final Metro metro;

    public Line(String color, Metro metro) {
        this.color = color;
        this.stations = new ArrayList<>();
        this.metro = metro;
    }

    public void addStation(Station station) {
        stations.add(station);
    }

    public Station getLastStation() {
        return stations.isEmpty() ? null : stations.get(stations.size() - 1);
    }

    public String getColor() {
        return color;
    }

    public List<Station> getStations() {
        return stations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(color, line.color)
                && Objects.equals(stations, line.stations)
                && Objects.equals(metro, line.metro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, stations, metro);
    }

    @Override
    public String toString() {
        return "Line{color='" + color + "', stations=" + stations + "}";
    }
}

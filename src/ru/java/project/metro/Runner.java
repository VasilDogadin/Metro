package ru.java.project.metro;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Runner {

    public static void main(String[] args) {

        Metro permMetro = new Metro("Пермь", new ArrayList<>());

        permMetro.createNewLine("Красная");
        permMetro.createNewLine("Синяя");

        permMetro.createFirstStation("Красная", "Спортивная", null);
        permMetro.createTerminalStation("Красная", "Медведковская", Duration.ofMinutes(2).plusSeconds(21), null);
        permMetro.createTerminalStation("Красная", "Молодежная", Duration.ofMinutes(1).plusSeconds(58), null);
        permMetro.createTerminalStation("Красная", "Пермь 1", Duration.ofMinutes(3), List.of("Синяя"));
        permMetro.createTerminalStation("Красная", "Пермь 2", Duration.ofMinutes(2).plusSeconds(10), null);
        permMetro.createTerminalStation("Красная", "Дворец Культуры", Duration.ofMinutes(4).plusSeconds(26), null);
        //permMetro.checkStationExists("Дворец Культур");

        permMetro.createFirstStation("Синяя", "Пацанская", null);
        permMetro.createTerminalStation("Синяя", "Улица Кирова", Duration.ofMinutes(1).plusSeconds(30), null);
        permMetro.createTerminalStation("Синяя", "Тяжмаш", Duration.ofMinutes(1).plusSeconds(47), List.of("Красная"));
        permMetro.createTerminalStation("Синяя", "Нижнекамская", Duration.ofMinutes(3).plusSeconds(19), null);
        permMetro.createTerminalStation("Синяя", "Соборная", Duration.ofMinutes(1).plusSeconds(48), null);
        System.out.println(permMetro);
    }
}

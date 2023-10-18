package ru.school.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeTrackingDTO {

    @CsvBindByName(column = "id")
    @CsvBindByPosition(position = 0)
    private Long id;

    @CsvBindByName(column = "peer")
    @CsvBindByPosition(position = 1)
    private String peer;

    @CsvDate(value = "yyyy-MM-dd")
    @CsvBindByName(column = "date")
    @CsvBindByPosition(position = 2)
    private LocalDate date;

    @CsvDate(value = "HH:mm:ss")
    @CsvBindByName(column = "time")
    @CsvBindByPosition(position = 3)
    private LocalTime time;

    @CsvBindByName(column = "state")
    @CsvBindByPosition(position = 4)
    private Long state;

}

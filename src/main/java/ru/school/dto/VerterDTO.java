package ru.school.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerterDTO {

    @CsvBindByName(column = "id")
    @CsvBindByPosition(position = 0)
    private Long id;

    @CsvBindByName(column = "check_id")
    @CsvBindByPosition(position = 1)
    private Long check_id;


    @CsvBindByName(column = "state")
    @CsvBindByPosition(position = 2)
    private String status;

    @CsvDate(value = "HH:mm:ss")
    @CsvBindByName(column = "time")
    @CsvBindByPosition(position = 3)
    private LocalTime time;

}

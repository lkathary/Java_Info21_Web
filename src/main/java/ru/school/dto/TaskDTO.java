package ru.school.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvNumber;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    @CsvBindByName(column = "title")
    @CsvBindByPosition(position = 0)
    private String title;

    @CsvBindByName(column = "parent_task")
    @CsvBindByPosition(position = 1)
    private String parentTask;

    @CsvNumber("#")
    @CsvBindByName(column = "max_xp")
    @CsvBindByPosition(position = 2)
    private Long maxExperience;

}

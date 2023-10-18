package ru.school.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class XpDTO {

    @CsvBindByName(column = "id")
    @CsvBindByPosition(position = 0)
    private Long id;

    @CsvBindByName(column = "check_id")
    @CsvBindByPosition(position = 1)
    private Long check_id;

    @CsvBindByName(column = "xp_amount")
    @CsvBindByPosition(position = 2)
    private Long amount;

}

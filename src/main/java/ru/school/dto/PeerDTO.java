package ru.school.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeerDTO {

    @CsvBindByName(column = "nickname")
    @CsvBindByPosition(position = 0)
    private String nickname;

    @CsvDate(value = "yyyy-MM-dd")
    @CsvBindByName(column = "birthday")
    @CsvBindByPosition(position = 1)
    private LocalDate birthday;

}

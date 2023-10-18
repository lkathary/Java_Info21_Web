package ru.school.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDTO {

    @CsvBindByName(column = "id")
    @CsvBindByPosition(position = 0)
    private Long id;

    @CsvBindByName(column = "peer")
    @CsvBindByPosition(position = 1)
    private String peer;

    @CsvBindByName(column = "peer2")
    @CsvBindByPosition(position = 2)
    private String recommendedPeer;

}

package ru.school.model.entity;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "peers")
public class Peer {

    @Id
    @Column(name = "nickname")
    @CsvBindByName(column = "nickname")
    private String nickname;

    @Column(name = "birthday")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @CsvDate(value = "yyyy-MM-dd")
    @CsvBindByName(column = "birthday")
    private LocalDate birthday;
}

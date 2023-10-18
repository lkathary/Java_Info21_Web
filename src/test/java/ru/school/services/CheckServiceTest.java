package ru.school.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.school.model.entity.Check;
import ru.school.repositories.CheckRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckServiceTest {

    @InjectMocks
    private CheckService checkService;
    @Mock
    CheckRepository checkRepository;


    @Test
    void getAllIds() {
        when(checkRepository.findAllIdsAsc()).thenReturn(Collections.singletonList(5L));

        List<Long> checks = checkService.getAllIds();
        assertThat(checks).hasSize(1);
        assertThat(checks.get(0)).isEqualTo(5L);
    }

    @Test
    void getChecks() {

        Check check = new Check();
        check.setId(1L);

        when(checkRepository.findAll()).thenReturn(Collections.singletonList(check));

        List<Check> checks = checkService.getAllChecks();
        assertThat(checks).hasSize(1);
        assertThat(checks.iterator().next()).isSameAs(check);
    }

    @Test
    void findById() {

        Check check = new Check();
        check.setId(3L);

        when(checkRepository.findById(3L)).thenReturn(Optional.of(check));

        Optional<Check> optionalCheck = checkService.findById(3L);
        assertThat(optionalCheck.isPresent()).isTrue();
        assertThat(optionalCheck.get()).isSameAs(check);

        optionalCheck = checkService.findById(5L);
        assertThat(optionalCheck.isPresent()).isFalse();
    }
}
package ru.school.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.school.dao.ManagerDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExecuteQueryService {

    private final ManagerDAO managerDAO;

    public Map<String, List<Object>> executeQuery(String SQL) throws SQLException {
        return managerDAO.executeQuery(SQL);
    }

    public Map<String, List<Object>> executeFunction(String SQL, Object... args) throws SQLException {
        return managerDAO.callFunction(SQL, args);
    }

    public Map<String, List<Object>> executeProcedure(String SQL, Object... args) throws SQLException {
        return managerDAO.callProcedure(SQL, args);
    }
}

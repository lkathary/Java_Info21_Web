package ru.school.controllers;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.school.csv.FileImportExport;
import ru.school.services.ExecuteQueryService;

import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static ru.school.dao.ManagerDAO.CALL;

@Controller
@RequiredArgsConstructor
public class SqlController {

    private final ExecuteQueryService executeQueryService;
    private Map<String, List<Object>> result;

    @PostMapping("/sqlQuery")
    public String executeQuery(String query, @NotNull Model model) throws SQLException {
        result = executeQueryService.executeQuery(query);
        model.addAttribute("result", result);
        return "sqlQuery";
    }

    @PostMapping("/func")
    public String executeCall(int id, @NotNull Model model,
                              @RequestParam(value = "arg", required = false) Object... args) throws SQLException {
        result = executeQueryService.executeFunction(CALL.get(id), args);
        model.addAttribute("result", result);
        return "results";
    }

    @PostMapping("/func/3")
    public String executeF3(int id, @NotNull Model model,
                            @RequestParam(value = "arg", required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate arg) throws SQLException {
        return executeCall(id, model, arg);
    }

    @PostMapping("/proc")
    public String executeProc(int id, @NotNull Model model,
                              @RequestParam(value = "arg", required = false) Object... args) throws SQLException {
        result = executeQueryService.executeProcedure(CALL.get(id), args);
        model.addAttribute("result", result);
        return "results";
    }

    @RequestMapping(path = "/unload")
    public void unloadFile(HttpServletResponse servletResponse) {
        FileImportExport.resultExportCsv(servletResponse, result);
    }
}

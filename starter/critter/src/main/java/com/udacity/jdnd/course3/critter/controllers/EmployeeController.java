package com.udacity.jdnd.course3.critter.controllers;

import com.udacity.jdnd.course3.critter.dtos.EmployeeDTO;
import com.udacity.jdnd.course3.critter.dtos.EmployeeRequestDTO;
import com.udacity.jdnd.course3.critter.services.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/user")
public class EmployeeController {

    @Autowired
    private IEmployeeService employeeService;
    @PostMapping("/employee")
    public EmployeeDTO saveEmployee(@RequestBody EmployeeDTO employeeDTO) {
        return this.employeeService.save(employeeDTO);
    }

    @GetMapping("/employee/{employeeId}")
    public EmployeeDTO getEmployee(@PathVariable Long employeeId) {
        return this.employeeService.findById(employeeId);
    }

    @PutMapping("/employee/{employeeId}")
    public void setAvailability(@RequestBody Set<DayOfWeek> daysAvailable, @PathVariable Long employeeId) {
        EmployeeDTO employeeDTO = this.employeeService.findById(employeeId);
        if (Objects.nonNull(employeeDTO)) {
            employeeDTO.setDaysAvailable(daysAvailable);
            this.employeeService.save(employeeDTO);
        }
    }

    @GetMapping("/employee/availability")
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO employeeDTO) {
        return this.employeeService.findEmployeesForService(employeeDTO);
    }
}

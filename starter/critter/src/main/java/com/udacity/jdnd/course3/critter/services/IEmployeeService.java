package com.udacity.jdnd.course3.critter.services;

import com.udacity.jdnd.course3.critter.dtos.EmployeeDTO;
import com.udacity.jdnd.course3.critter.dtos.EmployeeRequestDTO;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

public interface IEmployeeService {
    EmployeeDTO save(EmployeeDTO employeeDTO);
    EmployeeDTO findById(Long employeeId);
    List<EmployeeDTO> findEmployeesForService(EmployeeRequestDTO employeeRequestDTO);
}

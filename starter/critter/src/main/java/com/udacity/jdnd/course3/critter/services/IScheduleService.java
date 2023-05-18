package com.udacity.jdnd.course3.critter.services;

import com.udacity.jdnd.course3.critter.dtos.ScheduleDTO;

import java.util.List;

public interface IScheduleService {
    ScheduleDTO save(ScheduleDTO scheduleDTO);
    List<ScheduleDTO> findAll();
    List<ScheduleDTO> findByPetId(Long petId);
    List<ScheduleDTO> findByEmployeeId(Long employeeId);
    List<ScheduleDTO> findByCustomerId(Long customerId);
}

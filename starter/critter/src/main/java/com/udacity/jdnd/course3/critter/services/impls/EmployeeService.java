package com.udacity.jdnd.course3.critter.services.impls;

import com.udacity.jdnd.course3.critter.dtos.EmployeeDTO;
import com.udacity.jdnd.course3.critter.dtos.EmployeeRequestDTO;
import com.udacity.jdnd.course3.critter.entities.EmployeeEntity;
import com.udacity.jdnd.course3.critter.repositories.EmployeeRepository;
import com.udacity.jdnd.course3.critter.services.IEmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeService implements IEmployeeService {
    private static final Logger LOGGER = Logger.getLogger(EmployeeService.class.getName());
    
    @Autowired
    private EmployeeRepository employeeRepository;

    private Object populate(Object source) {
        try {
            if (source instanceof EmployeeDTO) {
                EmployeeDTO dto = (EmployeeDTO) source;
                EmployeeEntity entity = EmployeeEntity.class.newInstance();
                BeanUtils.copyProperties(dto, entity);
                return entity;
            }
            if (source instanceof EmployeeEntity) {
                EmployeeEntity entity = (EmployeeEntity) source;
                EmployeeDTO dto = EmployeeDTO.class.newInstance();
                BeanUtils.copyProperties(entity, dto);
                return dto;
            }
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
        return null;
    }
    
    @Override
    public EmployeeDTO save(EmployeeDTO employeeDTO) {
        LOGGER.info("[save] Execute -> EmployeeService");
        EmployeeEntity entity = (EmployeeEntity) populate(employeeDTO);
        EmployeeDTO dto = null;
        if (Objects.nonNull(entity)) {
            entity = this.employeeRepository.save(entity);
            if (Objects.nonNull(entity.getId())) {
                dto = (EmployeeDTO) populate(entity);
            }
        }
        return dto;
    }

    @Override
    public EmployeeDTO findById(Long employeeId) {
        LOGGER.info("[findById] Execute -> EmployeeService");
        Optional<EmployeeEntity> entity = this.employeeRepository.findById(employeeId);
        EmployeeDTO dto = null;
        if (entity.isPresent()) {
            dto = (EmployeeDTO) populate(entity.get());
        }
        return dto;
    }

    @Override
    public List<EmployeeDTO> findEmployeesForService(EmployeeRequestDTO employeeRequestDTO) {
        LOGGER.info("[findEmployeesForService] Execute -> EmployeeService");
        List<EmployeeDTO> dtos = new ArrayList<>();
        List<EmployeeEntity> entities = this.employeeRepository
                .findByDaysAvailableContaining(employeeRequestDTO.getDate().getDayOfWeek());
        if (!CollectionUtils.isEmpty(entities)) {
            dtos = entities.stream().filter(e -> e.getSkills().containsAll(employeeRequestDTO.getSkills()))
                    .map(e -> (EmployeeDTO) populate(e)).collect(Collectors.toList());
        }
        return dtos;
    }
}

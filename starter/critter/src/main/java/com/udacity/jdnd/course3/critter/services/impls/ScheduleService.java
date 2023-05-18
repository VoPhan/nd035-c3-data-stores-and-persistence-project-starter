package com.udacity.jdnd.course3.critter.services.impls;

import com.udacity.jdnd.course3.critter.dtos.ScheduleDTO;
import com.udacity.jdnd.course3.critter.entities.EmployeeEntity;
import com.udacity.jdnd.course3.critter.entities.PetEntity;
import com.udacity.jdnd.course3.critter.entities.ScheduleEntity;
import com.udacity.jdnd.course3.critter.repositories.EmployeeRepository;
import com.udacity.jdnd.course3.critter.repositories.PetRepository;
import com.udacity.jdnd.course3.critter.repositories.ScheduleRepository;
import com.udacity.jdnd.course3.critter.services.IScheduleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScheduleService implements IScheduleService {
    private static final Logger LOGGER = Logger.getLogger(ScheduleService.class.getName());
    
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Object populate(Object source) {
        try {
            if (source instanceof ScheduleDTO) {
                ScheduleDTO dto = (ScheduleDTO) source;
                ScheduleEntity entity = ScheduleEntity.class.newInstance();
                BeanUtils.copyProperties(dto, entity);
                return entity;
            }
            if (source instanceof ScheduleEntity) {
                ScheduleEntity entity = (ScheduleEntity) source;
                ScheduleDTO dto = ScheduleDTO.class.newInstance();
                BeanUtils.copyProperties(entity, dto);
                if (!CollectionUtils.isEmpty(entity.getPets())) {
                    dto.setPetIds(entity.getPets().stream().map(PetEntity::getId).collect(Collectors.toList()));
                }
                if (!CollectionUtils.isEmpty(entity.getEmployees())) {
                    dto.setEmployeeIds(entity.getEmployees().stream().map(EmployeeEntity::getId).collect(Collectors.toList()));
                }
                return dto;
            }
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
        return null;
    }
    
    @Override
    public ScheduleDTO save(ScheduleDTO scheduleDTO) {
        LOGGER.info("[save] Execute -> ScheduleService");
        ScheduleDTO dto = null;
        ScheduleEntity entity = (ScheduleEntity) populate(scheduleDTO);
        if (Objects.nonNull(entity)) {
            List<PetEntity> petEntities = new ArrayList<>();
            if (!CollectionUtils.isEmpty(scheduleDTO.getPetIds())) {
                petEntities = this.petRepository.findAllById(scheduleDTO.getPetIds());
            }
            entity.setPets(petEntities);
            List<EmployeeEntity> employeeEntities = new ArrayList<>();
            if (!CollectionUtils.isEmpty(scheduleDTO.getEmployeeIds())) {
                employeeEntities = this.employeeRepository.findAllById(scheduleDTO.getEmployeeIds());
            }
            entity.setEmployees(employeeEntities);
            entity = this.scheduleRepository.save(entity);
            if (Objects.nonNull(entity.getId())) {
                dto = (ScheduleDTO) populate(entity);
            }
        }
        return dto;
    }

    @Override
    public List<ScheduleDTO> findAll() {
        LOGGER.info("[findAll] Execute -> ScheduleService");
        List<ScheduleDTO> dtos = new ArrayList<>();
        List<ScheduleEntity> entities = this.scheduleRepository.findAll();
        if (!CollectionUtils.isEmpty(entities)) {
            dtos = entities.stream().map(e -> (ScheduleDTO) populate(e)).collect(Collectors.toList());
        }
        return dtos;
    }

    @Override
    public List<ScheduleDTO> findByPetId(Long petId) {
        LOGGER.info("[findByPetId] Execute -> ScheduleService");
        List<ScheduleDTO> dtos = new ArrayList<>();
        List<ScheduleEntity> entities = this.scheduleRepository.findByPetsContaining(new PetEntity(petId));
        if (!CollectionUtils.isEmpty(entities)) {
            dtos = entities.stream().map(e -> (ScheduleDTO) populate(e)).collect(Collectors.toList());
        }
        return dtos;
    }

    @Override
    public List<ScheduleDTO> findByEmployeeId(Long employeeId) {
        LOGGER.info("[findByEmployeeId] Execute -> ScheduleService");
        List<ScheduleDTO> dtos = new ArrayList<>();
        List<ScheduleEntity> entities = this.scheduleRepository.findByEmployeesContaining(new EmployeeEntity(employeeId));
        if (!CollectionUtils.isEmpty(entities)) {
            dtos = entities.stream().map(e -> (ScheduleDTO) populate(e)).collect(Collectors.toList());
        }
        return dtos;
    }

    @Override
    public List<ScheduleDTO> findByCustomerId(Long customerId) {
        LOGGER.info("[findByCustomerId] Execute -> ScheduleService");
        List<ScheduleDTO> dtos = new ArrayList<>();
        List<ScheduleEntity> entities = new ArrayList<>();
        List<PetEntity> petEntities = this.petRepository.findByCustomerId(customerId);
        if (!CollectionUtils.isEmpty(petEntities)) {
            petEntities.forEach(e -> {
                entities.addAll(this.scheduleRepository.findByPetsContaining(new PetEntity(e.getId())));
            });
        }
        if (!CollectionUtils.isEmpty(entities)) {
            dtos = entities.stream().map(e -> (ScheduleDTO) populate(e)).collect(Collectors.toList());
        }
        return dtos;
    }
}

package com.udacity.jdnd.course3.critter.services.impls;

import com.udacity.jdnd.course3.critter.dtos.CustomerDTO;
import com.udacity.jdnd.course3.critter.dtos.PetDTO;
import com.udacity.jdnd.course3.critter.entities.CustomerEntity;
import com.udacity.jdnd.course3.critter.entities.PetEntity;
import com.udacity.jdnd.course3.critter.repositories.CustomerRepository;
import com.udacity.jdnd.course3.critter.repositories.PetRepository;
import com.udacity.jdnd.course3.critter.services.IPetService;
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
public class PetService implements IPetService {
    private static final Logger LOGGER = Logger.getLogger(PetService.class.getName());

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PetRepository petRepository;

    private Object populate(Object source) {
        try {
            if (source instanceof PetDTO) {
                PetDTO dto = (PetDTO) source;
                PetEntity entity = PetEntity.class.newInstance();
                BeanUtils.copyProperties(dto, entity);
                return entity;
            }
            if (source instanceof PetEntity) {
                PetEntity entity = (PetEntity) source;
                PetDTO dto = PetDTO.class.newInstance();
                BeanUtils.copyProperties(entity, dto);
                if (Objects.nonNull(entity.getCustomer())) {
                    dto.setOwnerId(entity.getCustomer().getId());
                } else {
                    dto.setOwnerId(null);
                }
                return dto;
            }
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
        return null;
    }

    @Override
    public PetDTO save(PetDTO petDTO) {
        LOGGER.info("[save] Execute -> PetService");
        PetEntity entity = (PetEntity) populate(petDTO);
        PetDTO dto = null;
        if (Objects.nonNull(entity)) {
            CustomerEntity customerEntity = null;
            Optional<CustomerEntity> optionalCustomer = this.customerRepository.findById(petDTO.getOwnerId());
            if (optionalCustomer.isPresent()) {
                customerEntity = optionalCustomer.get();
            }
            entity.setCustomer(customerEntity);
            entity = this.petRepository.save(entity);
            if (Objects.nonNull(customerEntity)) {
                customerEntity.getPets().add(entity);
                this.customerRepository.save(customerEntity);
            }
            if (Objects.nonNull(entity.getId())) {
                dto = (PetDTO) populate(entity);
            }
        }
        return dto;
    }

    @Override
    public List<PetDTO> findAll() {
        LOGGER.info("[save] Execute -> PetService");
        List<PetDTO> dtos = new ArrayList<>();
        List<PetEntity> entities = this.petRepository.findAll();
        if (!CollectionUtils.isEmpty(entities)) {
            dtos = entities.stream().map(e -> (PetDTO) populate(e)).collect(Collectors.toList());
        }
        return dtos;
    }

    @Override
    public List<PetDTO> findByCustomerId(Long customerId) {
        LOGGER.info("[save] Execute -> PetService");
        List<PetDTO> dtos = new ArrayList<>();
        List<PetEntity> entities = this.petRepository.findByCustomerId(customerId);
        if (!CollectionUtils.isEmpty(entities)) {
            dtos = entities.stream().map(e -> (PetDTO) populate(e)).collect(Collectors.toList());
        }
        return dtos;
    }

    @Override
    public PetDTO findById(Long petId) {
        LOGGER.info("[save] Execute -> PetService");
        Optional<PetEntity> entity = this.petRepository.findById(petId);
        PetDTO dto = null;
        if (entity.isPresent()) {
            dto = (PetDTO) populate (entity.get());
        }
        return dto;
    }
}

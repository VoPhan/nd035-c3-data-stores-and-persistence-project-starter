package com.udacity.jdnd.course3.critter.services.impls;

import com.udacity.jdnd.course3.critter.dtos.CustomerDTO;
import com.udacity.jdnd.course3.critter.entities.CustomerEntity;
import com.udacity.jdnd.course3.critter.entities.PetEntity;
import com.udacity.jdnd.course3.critter.repositories.CustomerRepository;
import com.udacity.jdnd.course3.critter.repositories.PetRepository;
import com.udacity.jdnd.course3.critter.services.ICustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerService implements ICustomerService {
    private static final Logger LOGGER = Logger.getLogger(CustomerService.class.getName());

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PetRepository petRepository;

    private Object populate(Object source) {
        try {
            if (source instanceof CustomerDTO) {
                CustomerDTO dto = (CustomerDTO) source;
                CustomerEntity entity = CustomerEntity.class.newInstance();
                BeanUtils.copyProperties(dto, entity);
                return entity;
            }
            if (source instanceof CustomerEntity) {
                CustomerEntity entity = (CustomerEntity) source;
                CustomerDTO dto = CustomerDTO.class.newInstance();
                BeanUtils.copyProperties(entity, dto);
                if (!CollectionUtils.isEmpty(entity.getPets())) {
                    dto.setPetIds(entity.getPets().stream().map(PetEntity::getId).collect(Collectors.toList()));
                }
                return dto;
            }
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
        return null;
    }
    @Override
    public CustomerDTO save(CustomerDTO customerDTO) {
        LOGGER.info("[save] Execute -> CustomerService");
        CustomerDTO dto = null;
        CustomerEntity entity = (CustomerEntity) populate(customerDTO);
        if (Objects.nonNull(entity)) {
            List<PetEntity> pets = new ArrayList<>();
            if (!CollectionUtils.isEmpty(customerDTO.getPetIds())) {
                pets = this.petRepository.findAllById(customerDTO.getPetIds());
            }
            entity.setPets(pets);
            CustomerEntity finalEntity = entity;
            pets.forEach(p -> p.setCustomer(finalEntity));
            entity = this.customerRepository.save(entity);
            if (Objects.nonNull(entity.getId())) {
                dto = (CustomerDTO) populate (entity);
            }
        }
        return dto;
    }

    @Override
    public List<CustomerDTO> findAll() {
        LOGGER.info("[findAll] Execute -> CustomerService");
        List<CustomerDTO> dtos = new ArrayList<>();
        List<CustomerEntity> entities = this.customerRepository.findAll();
        if (!CollectionUtils.isEmpty(entities)) {
            dtos = entities.stream().map(e -> (CustomerDTO) populate(e)).collect(Collectors.toList());
        }
        return dtos;
    }

    @Override
    public CustomerDTO findByPetId(Long petId) {
        LOGGER.info("[findByPetId] Execute -> CustomerService");
        CustomerEntity entity = this.customerRepository.findByPetsId(petId);
        CustomerDTO dto = null;
        if (Objects.nonNull(entity.getId())) {
            dto = (CustomerDTO) populate(entity);
        }
        return dto;
    }
}

package com.udacity.jdnd.course3.critter.services;

import com.udacity.jdnd.course3.critter.dtos.CustomerDTO;

import java.util.List;

public interface ICustomerService {
    CustomerDTO save(CustomerDTO customerDTO);
    List<CustomerDTO> findAll();
    CustomerDTO findByPetId(Long petId);
}

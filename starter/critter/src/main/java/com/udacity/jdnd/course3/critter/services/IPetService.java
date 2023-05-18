package com.udacity.jdnd.course3.critter.services;

import com.udacity.jdnd.course3.critter.dtos.PetDTO;

import java.util.List;

public interface IPetService {
    PetDTO save(PetDTO petDTO);
    List<PetDTO> findAll();
    List<PetDTO> findByCustomerId(Long customerId);
    PetDTO findById(Long petId);
}

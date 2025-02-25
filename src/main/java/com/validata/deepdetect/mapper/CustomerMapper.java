package com.validata.deepdetect.mapper;

import com.validata.deepdetect.dto.CustomerRequest;
import com.validata.deepdetect.model.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer toEntity(CustomerRequest customerRequest);
}

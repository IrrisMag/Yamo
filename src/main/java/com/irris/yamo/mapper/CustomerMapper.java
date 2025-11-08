package com.irris.yamo.mapper;

import com.irris.yamo.dtos.CustomerDto;
import com.irris.yamo.entities.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerDto toDto(Customer customer) {
        if (customer == null) {
            return null;
        }

        return CustomerDto.builder()
                .id(customer.getId())
                .name(customer.getFullName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .profileImageUrl(customer.getProfileImageUrl())
                .gender(customer.getGender())
                .customerType(customer.getCustomerType())
                .companyName(customer.getCompanyName())
                .nui(customer.getNui())
                .rccm(customer.getRccm())
                .customerSegment(customer.getCustomerSegment())
                .tags(customer.getTags())
                .customerCredit(customer.getCustomerCredit())
                .customerDebt(customer.getCustomerDebt())
                .customerBalance(customer.getCustomerBalance())
                .build();
    }
}

package com.vietcrm.customer.application;

import com.vietcrm.customer.api.CustomerRequest;
import com.vietcrm.customer.domain.Customer;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CustomerApplicationService {
    private final Map<String, Customer> customers = new ConcurrentHashMap<>();

    public Customer create(CustomerRequest request) {
        Customer customer = new Customer(newId(), request.tenantId(), Instant.now(), request.name(), normalize(request.email()), normalize(request.phone()));
        customers.put(customer.id(), customer);
        return customer;
    }

    public List<Customer> list(String tenantId, String keyword) {
        String normalizedKeyword = normalize(keyword);
        return customers.values().stream()
            .filter(customer -> tenantId == null || tenantId.equals(customer.tenantId()))
            .filter(customer -> normalizedKeyword == null || contains(customer.name(), normalizedKeyword) || contains(customer.email(), normalizedKeyword) || contains(customer.phone(), normalizedKeyword))
            .sorted(Comparator.comparing(Customer::createdAt).reversed())
            .toList();
    }

    public Customer get(String id) {
        Customer customer = customers.get(id);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + id);
        }
        return customer;
    }

    public Customer update(String id, CustomerRequest request) {
        Customer existing = get(id);
        Customer updated = new Customer(existing.id(), existing.tenantId(), existing.createdAt(), request.name(), normalize(request.email()), normalize(request.phone()));
        customers.put(updated.id(), updated);
        return updated;
    }

    public void delete(String id) {
        customers.remove(id);
    }

    private static boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase();
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }
}

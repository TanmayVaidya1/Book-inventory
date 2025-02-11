package com.pinnacle.books.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    // Create a new role
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    // Get all roles
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // Get role by ID
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    // Update role
    public Role updateRole(Long id, Role roleDetails) {
        Role role = roleRepository.findById(id).orElseThrow(() -> 
            new RuntimeException("Role not found with id: " + id));
        role.setRoleName(roleDetails.getRoleName());
        return roleRepository.save(role);
    }

    // Delete role
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> 
            new RuntimeException("Role not found with id: " + id));
        roleRepository.delete(role);
    }
}

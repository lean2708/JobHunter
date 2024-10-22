package anb52.jobhunter.service;

import anb52.jobhunter.domain.Permission;
import anb52.jobhunter.domain.Role;
import anb52.jobhunter.domain.response.ResultPaginationDTO;
import anb52.jobhunter.repository.PermissionRepository;
import anb52.jobhunter.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    @Autowired
    private final RoleRepository roleRepository;
    @Autowired
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean existsByName(Role role){
        return this.roleRepository.existsByName(role.getName());
    }

    public Role create(Role role){
        // check permission co ton tai khong
        if(role.getPermissions() != null){
            // lay danh sach permission_id
            List<Long> listPermissionID = new ArrayList<>();
            for(Permission permission : role.getPermissions()){
                listPermissionID.add(permission.getId());
            }
            // Lay ds Permission tu ds Permission_id
            List<Permission> permissionList = this.permissionRepository.findByIdIn(listPermissionID);
            role.setPermissions(permissionList);
        }
        return this.roleRepository.save(role);
    }

    public Role fetchById(long id){
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if(roleOptional.isPresent()){
            return roleOptional.get();
        }
        return null;
    }

    public ResultPaginationDTO fetchAllRole(Specification<Role> spec, Pageable pageable){
        Page<Role> page = this.roleRepository.findAll(spec, pageable);
        List<Role> listRole = page.getContent();

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(page.getNumber() + 1);
        mt.setPageSize(page.getSize());

        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());
        rs.setMeta(mt);

        rs.setResult(listRole);

        return rs;
    }

    public Role update(Role role){
        Role roleDB = fetchById(role.getId());
        // check permission
        if(role.getPermissions() != null){
            // lay danh sach permission_id
            List<Long> listPermissionID = new ArrayList<>();
            for(Permission permission : role.getPermissions()){
                listPermissionID.add(permission.getId());
            }
            // Lay ds Permission tu ds Permission_id
            List<Permission> permissionList = this.permissionRepository.findByIdIn(listPermissionID);
            role.setPermissions(permissionList);
        }
        // update role
        roleDB.setName(role.getName());
        roleDB.setDescription(role.getDescription());
        roleDB.setActive(role.isActive());
        roleDB.setPermissions(role.getPermissions());

        roleDB = roleRepository.save(roleDB);

        return roleDB;
    }

    public void delete(long id){
        this.roleRepository.deleteById(id);
    }
}

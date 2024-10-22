package anb52.jobhunter.service;

import anb52.jobhunter.domain.Permission;
import anb52.jobhunter.domain.Role;
import anb52.jobhunter.domain.response.ResultPaginationDTO;
import anb52.jobhunter.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionService {
     @Autowired
     private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission permission){
         return permissionRepository.existsByModuleAndApiPathAndMethod(
                 permission.getModule(),
                 permission.getApiPath(),
                 permission.getMethod()
         );
     }

     public Permission create(Permission permission){
         return this.permissionRepository.save(permission);
     }

     public Permission fetchPermissionById(long id){
         Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
         if(permissionOptional.isPresent()){
             return permissionOptional.get();
         }
         return null;
     }

     public ResultPaginationDTO fetchAllPermission(Specification<Permission> spec, Pageable pageable){
         Page<Permission> permissionPage = this.permissionRepository.findAll(spec,pageable);
         List<Permission> permissionList = permissionPage.getContent();

         ResultPaginationDTO rs = new ResultPaginationDTO();
         ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

         mt.setPage(permissionPage.getNumber() + 1);
         mt.setPageSize(permissionPage.getSize());

         mt.setPages(permissionPage.getTotalPages());
         mt.setTotal(permissionPage.getTotalElements());
         rs.setMeta(mt);

         rs.setResult(permissionList);

         return rs;
     }

    public Permission update(Permission permission){
        Permission permissionDB = fetchPermissionById(permission.getId());
        if(permissionDB != null){
            permissionDB.setName(permission.getName());
            permissionDB.setApiPath(permission.getApiPath());
            permissionDB.setMethod(permission.getMethod());
            permissionDB.setModule(permission.getModule());

            //update
            permissionDB = this.permissionRepository.save(permissionDB);
            return permissionDB;
        }
        return null;
    }

     public void delete(long id){
        // delete permission_role
         Permission permission = fetchPermissionById(id);
         List<Role> listRole = permission.getRoles();
         for(Role role : listRole){
             role.getPermissions().remove(permission);
         }

         // delete permisison
         this.permissionRepository.delete(permission);
     }

    public boolean isSameName(Permission permission) {
        Permission permissionDB = this.fetchPermissionById(permission.getId());
        if(permissionDB != null){
            if(permissionDB.getName().equals(permission.getName())){
                return true;
            }
        }
        return false;
    }
}

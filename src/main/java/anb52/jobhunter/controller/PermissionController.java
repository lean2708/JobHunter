package anb52.jobhunter.controller;

import anb52.jobhunter.domain.Permission;
import anb52.jobhunter.domain.response.ResultPaginationDTO;
import anb52.jobhunter.service.PermissionService;
import anb52.jobhunter.util.annotation.ApiMessage;
import anb52.jobhunter.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    @Autowired
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) throws IdInvalidException {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> create(@Valid @RequestBody Permission permission) throws IdInvalidException {
        // check exist by module, apiPath, method
        if(permissionService.isPermissionExist(permission)){
            throw new IdInvalidException("Permission đã tồn tại");
        }
        // create a new permission
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.create(permission));
    }

    @GetMapping("/permissions")
    @ApiMessage("Get All permission with pagination")
    public ResponseEntity<ResultPaginationDTO> fetchAllPermission(@Filter Specification<Permission> spec, Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(this.permissionService.fetchAllPermission(spec, pageable));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> update(@Valid @RequestBody Permission permission) throws IdInvalidException {
        Permission permissionDB = this.permissionService.fetchPermissionById(permission.getId());
        if(permissionDB == null){
            throw new IdInvalidException("Permission với id = " + permission.getId() + "không tồn tại");
        }
        // check exist by module, apiPath, method
        if(permissionService.isPermissionExist(permission)){
            // check name
            if(this.permissionService.isSameName(permission)){
                throw new IdInvalidException("Permission đã tồn tại");
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.permissionService.update(permission));
    }


    @DeleteMapping("/permissions/{id}")
    @ApiMessage("delte a permission")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Permission permissionDB = this.permissionService.fetchPermissionById(id);
        if(permissionDB == null){
            throw new IdInvalidException("Permission với id = " + id + "không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}

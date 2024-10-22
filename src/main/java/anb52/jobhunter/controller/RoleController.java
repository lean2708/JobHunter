package anb52.jobhunter.controller;

import anb52.jobhunter.domain.Role;
import anb52.jobhunter.domain.response.ResultPaginationDTO;
import anb52.jobhunter.service.RoleService;
import anb52.jobhunter.util.annotation.ApiMessage;
import anb52.jobhunter.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    @Autowired
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("create a role")
    public ResponseEntity<Role> create(@RequestBody Role role) throws IdInvalidException {
        if (this.roleService.existsByName(role)){
            throw new IdInvalidException("Role với name = " + role.getName() + " đã tồn tại.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(role));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("fetch role by id")
    public ResponseEntity<Role> fetchRoleById(@PathVariable("id") long id) throws IdInvalidException {
       Role role = this.roleService.fetchById(id);
       if(role == null){
           throw new IdInvalidException("Resume với id = " + id + " không tồn tại");
       }
       return ResponseEntity.status(HttpStatus.OK).body(role);
    }


    @GetMapping("/roles")
    @ApiMessage("fetch all role with pagination")
    public ResponseEntity<ResultPaginationDTO> fetchAllRole(@Filter Specification<Role> spec, Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.fetchAllRole(spec, pageable));
    }

    @PutMapping("/roles")
    @ApiMessage("update a role")
    public ResponseEntity<Role> update(@RequestBody Role role) throws IdInvalidException {
        Role newRole = this.roleService.fetchById(role.getId());
        if(newRole == null){
            throw new IdInvalidException("Role với id = " + role.getId() + "không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.update(role));
    }


    @DeleteMapping("/roles/{id}")
    @ApiMessage("delete a role")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        // check id
        if(this.roleService.fetchById(id) == null){
            throw new IdInvalidException("Role với id = " + id + "không tồn tại");
        }
        this.roleService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}

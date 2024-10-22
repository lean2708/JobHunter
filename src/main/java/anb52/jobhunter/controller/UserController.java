package anb52.jobhunter.controller;


import anb52.jobhunter.domain.User;
import anb52.jobhunter.domain.response.ResCreateUserDTO;
import anb52.jobhunter.domain.response.ResUserDTO;
import anb52.jobhunter.domain.response.ResUpdateUserDTO;
import anb52.jobhunter.domain.response.ResultPaginationDTO;
import anb52.jobhunter.service.RoleService;
import anb52.jobhunter.service.UserService;
import anb52.jobhunter.util.annotation.ApiMessage;
import anb52.jobhunter.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

public UserController(UserService userService, PasswordEncoder passwordEncoder){
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
}
    @PostMapping ("/users")
    @ApiMessage("create a new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@RequestBody User postManUser) throws IdInvalidException {
        if(userService.isEmailExist(postManUser)){
            throw new IdInvalidException("Email " + postManUser.getEmail() + " đã tồn tại, vui lòng sử dụng email khác");
        }

        String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
        postManUser.setPassword(hashPassword);

        User user = this.userService.handleCreateUser(postManUser);

        ResCreateUserDTO resCreateUserDTO = userService.convertToResCreateUserDTO(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(resCreateUserDTO);
    }



    @GetMapping("/users/{id}")
    @ApiMessage("fetch user by id")
    public ResponseEntity<ResUserDTO> fetchUser(@PathVariable("id") long id) throws IdInvalidException {
        User newUser = userService.fetchUser(id);
        if(newUser == null){
            throw new IdInvalidException("Id truyền lên không tồn tại");
        }
        ResUserDTO resUserDTO = userService.convertToResGetUserDTO(newUser);
        return ResponseEntity.status(HttpStatus.OK).body(resUserDTO);
    }

    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> fetchAllUser(
            @Filter Specification spec, Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(
                this.userService.fetchAllUser(spec, pageable));
    }


    @PutMapping("/users")
    @ApiMessage("update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws IdInvalidException {
        User updateUser = userService.updateUser(user);
        if(updateUser == null){
            throw new IdInvalidException("Id truyền lên không tồn tại");
        }
        ResUpdateUserDTO resUpdateUserDTO = userService.convertToResUpdateUserDTO(updateUser);
        return ResponseEntity.status(HttpStatus.OK).body(resUpdateUserDTO);
    }


    @DeleteMapping("/users/{id}")
    @ApiMessage("delete a user")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        User newUser = userService.fetchUser(id);
        if(newUser == null){
            throw new IdInvalidException("Id truyền lên không tồn tại");
        }
        this.userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}

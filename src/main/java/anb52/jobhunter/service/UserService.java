package anb52.jobhunter.service;


import anb52.jobhunter.domain.Company;
import anb52.jobhunter.domain.Role;
import anb52.jobhunter.domain.User;
import anb52.jobhunter.domain.response.ResCreateUserDTO;
import anb52.jobhunter.domain.response.ResUpdateUserDTO;
import anb52.jobhunter.domain.response.ResUserDTO;
import anb52.jobhunter.domain.response.ResultPaginationDTO;
import anb52.jobhunter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private final RoleService roleService;

public UserService(UserRepository userRepository, RoleService roleService){
    this.userRepository = userRepository;
    this.roleService = roleService;
}


    public ResUserDTO convertToResGetUserDTO(User user){
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.CompanyUser companyUser = new ResUserDTO.CompanyUser();
        ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setGender(user.getGender());
        res.setAge(user.getAge());
        res.setUpdateAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());

        // check company
        if(user.getCompany() != null){
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            res.setCompany(companyUser);
        }

        // check role
        if(user.getRole() != null){
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            res.setRole(roleUser);
        }

        return res;
    }
public ResCreateUserDTO convertToResCreateUserDTO(User user){
    ResCreateUserDTO resCreateUserDTO = new ResCreateUserDTO();
    ResCreateUserDTO.CompanyUser companyUser = new ResCreateUserDTO.CompanyUser();

    resCreateUserDTO.setId(user.getId());
    resCreateUserDTO.setName(user.getName());
    resCreateUserDTO.setEmail(user.getEmail());
    resCreateUserDTO.setAge(user.getAge());
    resCreateUserDTO.setGender(user.getGender());
    resCreateUserDTO.setAddress(user.getAddress());
    resCreateUserDTO.setCreatedAt(user.getCreatedAt());

    if(user.getCompany() != null){
        companyUser.setId(user.getCompany().getId());
        companyUser.setName(user.getCompany().getName());
        resCreateUserDTO.setCompany(companyUser);
    }

    return resCreateUserDTO;
}

public ResUpdateUserDTO convertToResUpdateUserDTO(User user){
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser companyUser = new ResUpdateUserDTO.CompanyUser();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setGender(user.getGender());
        res.setAge(user.getAge());
        res.setUpdateAt(user.getUpdatedAt());

    if(user.getCompany() != null){
        companyUser.setId(user.getCompany().getId());
        companyUser.setName(user.getCompany().getName());
        res.setCompany(companyUser);
    }

        return res;
}


public User handleCreateUser(User user){
        // check company
        if(user.getCompany() != null){
            Optional<Company> companyOptional = this.companyService.findById(user.getCompany().getId());
            user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
        }
        // check role
        if(user.getRole() != null){
            Role role = this.roleService.fetchById(user.getRole().getId());
            user.setRole(role != null ? role : null);
        }
        return userRepository.save(user);
}

public User fetchUser(long id){
    Optional<User> userOptional = userRepository.findById(id);
    if(userOptional.isPresent()){
        return userOptional.get();
    }
    return null;
}

public ResultPaginationDTO fetchAllUser(Specification<User> specification, Pageable pageable){
    Page<User> pageUser = this.userRepository.findAll(specification, pageable);
    List<User> listUser = pageUser.getContent();

    ResultPaginationDTO rs = new ResultPaginationDTO();
    ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

    mt.setPage(pageable.getPageNumber()+ 1);
    mt.setPageSize(pageable.getPageSize());

    mt.setPages(pageUser.getTotalPages());
    mt.setTotal(pageUser.getTotalElements());

    rs.setMeta(mt);

    // result
    ArrayList<ResUserDTO> listResUserDTO = new ArrayList<>();
    for(User user : listUser){
        ResUserDTO resUserDTO = convertToResGetUserDTO(user);
        listResUserDTO.add(resUserDTO);
    }
    rs.setResult(listResUserDTO);

    return rs;
}


public User updateUser(User reqUser){
    User userDB = fetchUser(reqUser.getId());
    if(userDB != null){
        userDB.setName(reqUser.getName());
        userDB.setAge(reqUser.getAge());
        userDB.setGender(reqUser.getGender());
        userDB.setAddress(reqUser.getAddress());

        // check company
        if(userDB.getCompany() != null){
            Optional<Company> companyOptional = companyService.findById(reqUser.getCompany().getId());
            userDB.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
        }

        // check role
        if(reqUser.getRole() != null){
            Role role = this.roleService.fetchById(reqUser.getRole().getId());
            userDB.setRole(role != null ? role : null);
        }

        // update
        userDB = this.userRepository.save(userDB);
    }
    return userDB;
}
public void deleteUser(long id){
    this.userRepository.deleteById(id);
}
public User handleGetUserByUsername(String username){
    return this.userRepository.findByEmail(username);
}

public boolean isEmailExist(User user){
    if(userRepository.existsByEmail(user.getEmail())){
        return true;
    }
    return false;
}


// Cap nhap token cho user
public void updateUserToken(String token, String email){
    User currentUser = this.handleGetUserByUsername(email);
    if(currentUser != null){
        currentUser.setRefreshToken(token);
        this.userRepository.save(currentUser);
    }
}
public User getUserByRefreshTokenAndEmail(String token, String email){
    return this.userRepository.findByRefreshTokenAndEmail(token, email);
}

}

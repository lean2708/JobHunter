package anb52.jobhunter.controller;


import anb52.jobhunter.domain.User;
import anb52.jobhunter.domain.request.ReqLoginDTO;
import anb52.jobhunter.domain.response.ResCreateUserDTO;
import anb52.jobhunter.domain.response.ResLoginDTO;
import anb52.jobhunter.service.UserService;
import anb52.jobhunter.util.annotation.ApiMessage;
import anb52.jobhunter.util.error.IdInvalidException;
import anb52.jobhunter.util.error.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
private AuthenticationManagerBuilder authenticationManagerBuilder;
private SecurityUtil securityUtil;

private UserService userService;

@Value("${anb52.jwt.refresh-token-validity-in-seconds}")
private long refreshTokenExpiration;

private final PasswordEncoder passwordEncoder;

public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil, UserService userService, PasswordEncoder passwordEncoder){
    this.authenticationManagerBuilder = authenticationManagerBuilder;
    this.securityUtil = securityUtil;
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
}


    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO){
    //  Valid để kích hoạt kiểm tra đối tượng
        //Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

//xác thực người dùng bằng authenticate sẽ gọi UserDetailsService để lấy thông tin người dùng từ DB để ss với username và password đã mã hóa
// => cần viết hàm loadUserByUsername
        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        //Lưu data vào Security Context khi login thành công
        // Set thong tin nguoi dung dang nhap vao context (co the su dung sau nay)
        SecurityContextHolder.getContext().setAuthentication(authentication);

       ResLoginDTO res = new ResLoginDTO();
       // Bổ sung thông tin cho user
        User currentUserDB = this.userService.handleGetUserByUsername(loginDTO.getUsername());

        if(currentUserDB != null){
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(), currentUserDB.getEmail(),
                    currentUserDB.getName(), currentUserDB.getRole()
            );
            res.setUser(userLogin);
        }

        //Create access token
        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res);

       res.setAccessToken(access_token);

        // Create refresh token
        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);

        //set cookies, them refresh token vao cookie
        ResponseCookie resCookies = ResponseCookie.from("refresh_token", refresh_token)
                .httpOnly(true) // chi cho phep server cua chung ta truy cap
                .secure(true) // cookie chi duoc su dung https (thay vi http)
                .path("/") // cookie se duoc su dung trong moi mien
                .maxAge(refreshTokenExpiration) // thoi gian song max (s)
                .build();

        // update user
        this.userService.updateUserToken(refresh_token, loginDTO.getUsername());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString()) // them cookie vao phan hoi
                .body(res);
    }

    @GetMapping("/auth/account")
    @ApiMessage("fetch account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount(){
       String email = SecurityUtil.getCurrentUserLogin().isPresent() ?
               SecurityUtil.getCurrentUserLogin().get() : "";

        User currentUserDB = this.userService.handleGetUserByUsername(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();

        if(currentUserDB != null){
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());
            userLogin.setRole(currentUserDB.getRole());

            userGetAccount.setUser(userLogin);
        }
       return ResponseEntity.status(HttpStatus.OK).body(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get User by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token
    ) throws IdInvalidException {
       if(refresh_token.equals("abc")){
           throw new IdInvalidException("Bạn không có Refresh Token ở Cookie");
       }
        // Check refresh token hop le
        Jwt decodedToken = this.securityUtil.checkValidToken(refresh_token);
        String email = decodedToken.getSubject();

        // Check user by token + email
        User currentUSer = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if(currentUSer == null){
            throw new IdInvalidException("Refresh Token không hợp lệ");
        }
        ResLoginDTO res = new ResLoginDTO();
        // Bổ sung thông tin cho user
        User currentUserDB = this.userService.handleGetUserByUsername(email);

        if(currentUserDB != null){
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(), currentUserDB.getEmail(),
                    currentUserDB.getName(), currentUserDB.getRole());
            res.setUser(userLogin);
        }

        //Create access token moi
        String access_token = this.securityUtil.createAccessToken(email, res);

        res.setAccessToken(access_token);

        // Create refresh token moi
        String new_refresh_token = this.securityUtil.createRefreshToken(email, res);

        //set cookies, them refresh token vao cookie
        ResponseCookie resCookies = ResponseCookie.from("refresh_token", new_refresh_token)
                .httpOnly(true) // chi cho phep server cua chung ta truy cap
                .secure(true) // cookie chi duoc su dung https (thay vi http)
                .path("/") // cookie se duoc su dung trong moi mien
                .maxAge(refreshTokenExpiration) // thoi gian song max (s)
                .build();

        // update user
        this.userService.updateUserToken(new_refresh_token,email);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString()) // them cookie vao phan hoi
                .body(res);
    }


    @PostMapping("/auth/logout")
    @ApiMessage("Logout User")
    public ResponseEntity<Void> deleteRefreshToken() throws IdInvalidException {
         String email = SecurityUtil.getCurrentUserLogin().isPresent()
                 ? SecurityUtil.getCurrentUserLogin().get() : "";
         if(email.equals("")){
             throw new IdInvalidException("Access Token không hợp lệ");
         }

         // Update refresh token  = null
        this.userService.updateUserToken(null, email);

         // Remove refresh token cookie
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true) // chi cho phep server cua chung ta truy cap
                .secure(true) // cookie chi duoc su dung https (thay vi http)
                .path("/") // cookie se duoc su dung trong moi mien
                .maxAge(0) // thoi gian song max (s)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString()) // them cookie vao phan hoi
                .body(null);
    }

    @PostMapping("/auth/register")
    @ApiMessage("Register a new user")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User postManUser) throws IdInvalidException {
         boolean isEmailExist = this.userService.isEmailExist(postManUser);
         if(isEmailExist){
             throw new IdInvalidException("Email " + postManUser.getEmail() + " đã tồn tại, vui lòng sử dụng email khác");
         }
         // Ma hoa mat khau
         String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
         postManUser.setPassword(hashPassword);

         User newUser = this.userService.handleCreateUser(postManUser);
         return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(postManUser));
    }
}

package anb52.jobhunter.config;

import anb52.jobhunter.domain.Permission;
import anb52.jobhunter.domain.Role;
import anb52.jobhunter.domain.User;
import anb52.jobhunter.service.UserService;
import anb52.jobhunter.util.error.IdInvalidException;
import anb52.jobhunter.util.error.PermissionException;
import anb52.jobhunter.util.error.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

// Phân quyền trước khi chạy vào controller
public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired UserService userService;

    @Override
    @Transactional    // Tao session đăng nhận khi thao tác với Interceptor
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response, Object handler)
            throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();
        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + requestURI);

        // check permission
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get() : "";
        if(email != null && !email.isEmpty()){
            User user = this.userService.handleGetUserByUsername(email);
            if(user != null){
                Role role = user.getRole();
                if(role != null){
                    List<Permission> listPermisisons = role.getPermissions();
                    // check path va method
                    boolean isAllow = listPermisisons.stream().anyMatch(item->
                            item.getApiPath().equals(path)
                                    && item.getMethod().equals(httpMethod));

                    if(isAllow == false){
                        throw new PermissionException("Bạn không có quyền truy cập endpoint này.");
                    }
                }else{
                    throw new PermissionException("Bạn không có quyền truy cập endpoint này.");
                }
            }
        }

        return true;
    }
}

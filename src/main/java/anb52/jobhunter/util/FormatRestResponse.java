package anb52.jobhunter.util;


import anb52.jobhunter.domain.response.RestResponse;
import anb52.jobhunter.util.annotation.ApiMessage;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }


    // Can thiệp vào phản hồi truoc khi tra ve
    @Override
    public Object beforeBodyWrite(
                                  Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
//      Lấy ra HttpServletResponse thực sự để có thể truy xuất và thao tác với các thuộc tính của HTTP response gốc
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();

        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(status);

        if(body instanceof String || body instanceof Resource){// KT nếu body là String
                return body;
        }

        String path = request.getURI().getPath();
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
            return body;
        }

        if(status >= 400){// KT status
            return body;
        }
        else{ // KT status < 400(thành công)
        res.setData(body);
            //Lấy thông tin từ annotation @ApiMessage
            ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
            // Nếu annotation tồn tại trên method, lấy message từ annotation
            res.setMessage(message != null ? message.value() : "CALL API SUCCESS");
        }
        return res;
    }
}

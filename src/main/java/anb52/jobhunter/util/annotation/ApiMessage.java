package anb52.jobhunter.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // Chỉ định annotation này dùng cho method
@Retention(RetentionPolicy.RUNTIME) // Annotation sẽ được giữ lại tại runtime
// Custom annotation
public @interface ApiMessage {
    String value();
}
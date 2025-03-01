package tw.com.ispan.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//將 JsonWebTokenInterceptor 註冊到 Spring MVC 的攔截器鏈中
@Configuration
public class JwtConfig implements WebMvcConfigurer {
    @Autowired
    private JsonWebTokenInterceptor jsonWebTokenInterceptor;

    // .addInterceptor指定需要攔截的路徑，也可用多個來區分不同功能模組(商城、寵物..)
    // .excludePathPatterns("/api/auth/**"); // 排除不需要攔截的路徑
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jsonWebTokenInterceptor)
                .addPathPatterns("/api/RescueCase/**", "/api/Case/**", "/api/Case/follow/add") // RescueCase底下的增刪修均限制為會員使用，/Case底下的上傳圖檔、追蹤均限制為會員使用
                .addPathPatterns("/api/line/**") // line綁定相關功能
                .addPathPatterns("/api/validateToken") // 進行前端token時效、持有驗證
                .excludePathPatterns("/api/RescueCase/search/**", "/api/pet/**", "/api/RescueCase/getLocations/**",
                        "/api/casePicture/**", "/api/RescueCase/rescueProgress/{caseId}") // 查詢功能為非會員也能使用
                .excludePathPatterns("/api/RescueCase/analysis") // 管理員後台
                .excludePathPatterns("/api/line/authorize", "/api/line/callback") // line登入相關功能
                .excludePathPatterns("/"); // 排除首頁相關頁面
    }
}

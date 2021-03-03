package top.shaozuo.simple.user.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import top.shaozuo.simple.user.domain.User;
import top.shaozuo.simple.user.service.UserService;
import top.shaozuo.simple.user.service.impl.UserServiceImpl;
import top.shaozuo.web.mvc.PageController;

/**
 * 注册成功跳转页面
 * 
 * @author shaozuo
 *
 */
@Path("/user")
public class SuccessController implements PageController {

    private UserService userService = new UserServiceImpl();

    @GET
    @Path("/success")
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
            throws Throwable {
        String email = request.getParameter("email");
        User result = userService.queryUserByEmail(email);
        if (result == null) {
            User user = new User();
            user.setName(request.getParameter("name"));
            user.setEmail(email);
            user.setPassword(request.getParameter("password"));
            user.setPhoneNumber(request.getParameter("phoneNumber"));
            userService.register(user);
            result = userService.queryUserByEmail(email);
        }
        request.setAttribute("result", result);
        return "success.jsp";
    }

}

package top.shaozuo.simple.user.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import top.shaozuo.web.mvc.PageController;

/**
 * 返回注册页面
 * 
 * @author shaozuo
 *
 */
@Path("/user")
public class RegisterController implements PageController {

    @GET
    @Path("/register")
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
            throws Throwable {
        System.out.println("reg");
        return "register.jsp";
    }

}

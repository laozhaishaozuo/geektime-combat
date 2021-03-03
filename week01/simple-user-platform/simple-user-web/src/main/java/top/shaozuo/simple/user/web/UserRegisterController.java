package top.shaozuo.simple.user.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import top.shaozuo.web.mvc.PageController;

@Path("/user")
public class UserRegisterController implements PageController {

    @GET
    @Path("/register")
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
            throws Throwable {
        return "register.jsp";
    }
}

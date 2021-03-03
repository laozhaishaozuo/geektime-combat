package top.shaozuo.simple.user.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import top.shaozuo.web.mvc.PageController;

@Path("/user")
public class UserSuccessController implements PageController {

    @POST
    @Path("/success")
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
            throws Throwable {
        System.out.println(request.getParameter("username"));
        return "success.jsp";
    }
}

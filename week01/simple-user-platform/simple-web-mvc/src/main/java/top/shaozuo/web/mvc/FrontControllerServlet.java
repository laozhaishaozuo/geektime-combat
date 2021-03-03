package top.shaozuo.web.mvc;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.substringAfter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;

import org.apache.commons.lang.StringUtils;

public class FrontControllerServlet extends HttpServlet {

    /**
     * 请求路径和 Controller 的映射关系缓存
     */
    private Map<String, Controller> controllersMapping = new HashMap<>();

    /**
     * 请求路径和 {@link HandlerMethodInfo} 映射关系缓存
     */
    private Map<String, HandlerMethodInfo> handleMethodInfoMapping = new HashMap<>();

    /**
     * 
     */
    private static final long serialVersionUID = 5112199155822802836L;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        initHandleMethods();
    }

    private void initHandleMethods() {
        //目前一个Controller只负责解析一个路径
        for (Controller controller : ServiceLoader.load(Controller.class)) {
            Class<?> controllerClass = controller.getClass();
            Path pathFromClass = controllerClass.getAnnotation(Path.class);
            String requestPath = pathFromClass.value();
            Method[] publicMethods = controllerClass.getMethods();
            // 处理方法支持的 HTTP 方法集合
            for (Method method : publicMethods) {
                Set<String> supportedHttpMethods = findSupportedHttpMethods(method);
                Path pathFromMethod = method.getAnnotation(Path.class);
                if (pathFromMethod != null) {
                    requestPath += pathFromMethod.value();
                    handleMethodInfoMapping.put(requestPath,
                            new HandlerMethodInfo(requestPath, method, supportedHttpMethods));
                    controllersMapping.put(requestPath, controller);
                }
            }
        }

    }

    /**
     * 获取处理方法中标注的 HTTP方法集合
     *
     * @param method
     *            处理方法
     * @return
     */
    private Set<String> findSupportedHttpMethods(Method method) {
        Set<String> supportedHttpMethods = new LinkedHashSet<>();
        for (Annotation annotationFromMethod : method.getAnnotations()) {
            HttpMethod httpMethod = annotationFromMethod.annotationType()
                    .getAnnotation(HttpMethod.class);
            if (httpMethod != null) {
                supportedHttpMethods.add(httpMethod.value());
            }
        }

        if (supportedHttpMethods.isEmpty()) {
            supportedHttpMethods.addAll(asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT,
                    HttpMethod.DELETE, HttpMethod.HEAD, HttpMethod.OPTIONS));
        }

        return supportedHttpMethods;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 获取请求路径
        String requestMappingPath = resolveMappingPath(req);
        // 映射到 Controller
        Controller controller = controllersMapping.get(requestMappingPath);

        if (controller != null) {

            HandlerMethodInfo handlerMethodInfo = handleMethodInfoMapping.get(requestMappingPath);

            try {
                if (handlerMethodInfo != null) {

                    String httpMethod = req.getMethod();

                    if (!handlerMethodInfo.getSupportedHttpMethods().contains(httpMethod)) {
                        // HTTP 方法不支持
                        resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                        return;
                    }

                    if (controller instanceof PageController) {
                        PageController pageController = PageController.class.cast(controller);
                        String viewPath = pageController.execute(req, resp);
                        // 页面请求 forward
                        // request -> RequestDispatcher forward
                        // RequestDispatcher requestDispatcher = request.getRequestDispatcher(viewPath);
                        // ServletContext -> RequestDispatcher forward
                        // ServletContext -> RequestDispatcher 必须以 "/" 开头
                        ServletContext servletContext = req.getServletContext();
                        if (!viewPath.startsWith("/")) {
                            viewPath = "/" + viewPath;
                        }
                        RequestDispatcher requestDispatcher = servletContext
                                .getRequestDispatcher(viewPath);
                        requestDispatcher.forward(req, resp);
                        return;
                    }
                }
            } catch (Throwable throwable) {
                if (throwable.getCause() instanceof IOException) {
                    throw (IOException) throwable.getCause();
                } else {
                    throw new ServletException(throwable.getCause());
                }
            }
        }
    }

    private String resolveMappingPath(HttpServletRequest req) {
        // 建立映射关系
        // requestURI = /a/hello/world
        String requestURI = req.getRequestURI();
        // contextPath  = /a or "/" or ""
        String servletContextPath = req.getContextPath();
        String prefixPath = servletContextPath;
        System.out.println(requestURI);
        // 映射路径（子路径）
        String requestMappingPath = substringAfter(requestURI,
                StringUtils.replace(prefixPath, "//", "/"));
        return requestMappingPath;
    }

}

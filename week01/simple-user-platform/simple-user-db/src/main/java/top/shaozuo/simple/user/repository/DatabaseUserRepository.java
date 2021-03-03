package top.shaozuo.simple.user.repository;

import static org.apache.commons.lang.ClassUtils.wrapperToPrimitive;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import top.shaozuo.simple.user.domain.User;
import top.shaozuo.simple.user.sql.DBConnectionManager;

public class DatabaseUserRepository implements UserRepository {

    private static Logger logger = Logger.getLogger(DatabaseUserRepository.class.getName());

    /**
     * 通用处理方式
     */
    private static Consumer<Throwable> COMMON_EXCEPTION_HANDLER = e -> logger.log(Level.SEVERE,
            e.getMessage());

    public static final String INSERT_USER_DML_SQL = "INSERT INTO users(name,password,email,phoneNumber) VALUES "
            + "(?,?,?,?)";

    public static final String QUERY_ALL_USERS_DML_SQL = "SELECT id,name,password,email,phoneNumber FROM users";

    private Connection getConnection() {
        try {
            return DBConnectionManager.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void releaseConnection(Connection connection) {
        DBConnectionManager.releaseConnection(connection);
    }

    @Override
    public boolean save(User user) {
        return execute(INSERT_USER_DML_SQL, e -> {
        }, user.getName(), user.getPassword(), user.getEmail(), user.getPhoneNumber());
    }

    @Override
    public boolean deleteById(Long userId) {
        return false;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public User getById(Long userId) {
        return null;
    }

    @Override
    public User getByNameAndPassword(String userName, String password) {
        return executeQuery(
                "SELECT id,name,password,email,phoneNumber FROM users WHERE name=? and password=?",
                resultSet -> {
                    // TODO
                    return new User();
                }, COMMON_EXCEPTION_HANDLER, userName, password);
    }

    @Override
    public User getByEmail(String email) {
        return executeQuery("SELECT id,name,password,email,phoneNumber FROM users WHERE email=? ",
                resultSet -> {
                    if (resultSet.next()) {

                        BeanInfo userBeanInfo = Introspector.getBeanInfo(User.class, Object.class);
                        User user = new User();
                        for (PropertyDescriptor propertyDescriptor : userBeanInfo
                                .getPropertyDescriptors()) {
                            String fieldName = propertyDescriptor.getName();
                            Class fieldType = propertyDescriptor.getPropertyType();
                            String methodName = resultSetMethodMappings.get(fieldType);
                            // 可能存在映射关系（不过此处是相等的）
                            String columnLabel = mapColumnLabel(fieldName);
                            Method resultSetMethod = ResultSet.class.getMethod(methodName,
                                    String.class);
                            // 通过放射调用 getXXX(String) 方法
                            Object resultValue = resultSetMethod.invoke(resultSet, columnLabel);
                            // 获取 User 类 Setter方法
                            // PropertyDescriptor ReadMethod 等于 Getter 方法
                            // PropertyDescriptor WriteMethod 等于 Setter 方法
                            Method setterMethodFromUser = propertyDescriptor.getWriteMethod();
                            // 以 id 为例，  user.setId(resultSet.getLong("id"));
                            setterMethodFromUser.invoke(user, resultValue);
                        }
                        return user;
                    } else {
                        return null;
                    }
                }, COMMON_EXCEPTION_HANDLER, email);
    }

    @Override
    public Collection<User> getAll() {
        return executeQuery("SELECT id,name,password,email,phoneNumber FROM users", resultSet -> {
            // BeanInfo -> IntrospectionException
            BeanInfo userBeanInfo = Introspector.getBeanInfo(User.class, Object.class);
            List<User> users = new ArrayList<>();
            while (resultSet.next()) { // 如果存在并且游标滚动 // SQLException
                User user = new User();
                for (PropertyDescriptor propertyDescriptor : userBeanInfo
                        .getPropertyDescriptors()) {
                    String fieldName = propertyDescriptor.getName();
                    Class<?> fieldType = propertyDescriptor.getPropertyType();
                    String methodName = resultSetMethodMappings.get(fieldType);
                    // 可能存在映射关系（不过此处是相等的）
                    String columnLabel = mapColumnLabel(fieldName);
                    Method resultSetMethod = ResultSet.class.getMethod(methodName, String.class);
                    // 通过放射调用 getXXX(String) 方法
                    Object resultValue = resultSetMethod.invoke(resultSet, columnLabel);
                    // 获取 User 类 Setter方法
                    // PropertyDescriptor ReadMethod 等于 Getter 方法
                    // PropertyDescriptor WriteMethod 等于 Setter 方法
                    Method setterMethodFromUser = propertyDescriptor.getWriteMethod();
                    // 以 id 为例，  user.setId(resultSet.getLong("id"));
                    setterMethodFromUser.invoke(user, resultValue);
                }
            }
            return users;
        }, e -> {
            // 异常处理
        });
    }

    /**
     * @param sql
     * @param function
     * @param <T>
     * @param exceptionHandler
     * @param args
     * @return
     */
    protected <T> T executeQuery(String sql, ThrowableFunction<ResultSet, T> function,
            Consumer<Throwable> exceptionHandler, Object... args) {
        Connection connection = getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                Class argType = arg.getClass();

                Class wrapperType = wrapperToPrimitive(argType);

                if (wrapperType == null) {
                    wrapperType = argType;
                }

                // Boolean -> boolean
                String methodName = preparedStatementMethodMappings.get(argType);
                Method method = PreparedStatement.class.getMethod(methodName, int.class,
                        wrapperType);
                method.invoke(preparedStatement, i + 1, arg);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            // 返回一个 POJO List -> ResultSet -> POJO List
            // ResultSet -> T
            return function.apply(resultSet);
        } catch (Throwable e) {
            exceptionHandler.accept(e);
        }
        return null;
    }

    /**
     * @param sql
     * @param function
     * @param <T>
     * @param exceptionHandler
     * @param args
     * @return
     */
    protected boolean execute(String sql, Consumer<Throwable> exceptionHandler, Object... args) {
        Connection connection = getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                Class argType = arg.getClass();

                Class<?> wrapperType = wrapperToPrimitive(argType);

                if (wrapperType == null) {
                    wrapperType = argType;
                }

                // Boolean -> boolean
                String methodName = preparedStatementMethodMappings.get(argType);
                Method method = PreparedStatement.class.getMethod(methodName, int.class,
                        wrapperType);
                method.invoke(preparedStatement, i + 1, arg);
            }
            return preparedStatement.execute();
        } catch (Throwable e) {
            exceptionHandler.accept(e);
        } finally {
            releaseConnection(connection);
        }
        return false;
    }

    private static String mapColumnLabel(String fieldName) {
        return fieldName;
    }

    /**
     * 数据类型与 ResultSet 方法名映射
     */
    static Map<Class, String> resultSetMethodMappings = new HashMap<>();

    static Map<Class, String> preparedStatementMethodMappings = new HashMap<>();

    static {
        resultSetMethodMappings.put(Long.class, "getLong");
        resultSetMethodMappings.put(int.class, "getIng");
        resultSetMethodMappings.put(String.class, "getString");

        preparedStatementMethodMappings.put(Long.class, "setLong"); // long
        preparedStatementMethodMappings.put(String.class, "setString"); //

    }

}

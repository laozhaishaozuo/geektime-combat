package top.shaozuo.simple.user.repository;

import java.util.Collection;

import top.shaozuo.simple.user.domain.User;

public interface UserRepository {
    boolean save(User user);

    boolean deleteById(Long userId);

    boolean update(User user);

    User getById(Long userId);

    User getByNameAndPassword(String userName, String password);

    Collection<User> getAll();

    User getByEmail(String email);
}

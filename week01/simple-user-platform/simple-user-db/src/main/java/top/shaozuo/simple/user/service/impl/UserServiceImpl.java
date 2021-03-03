package top.shaozuo.simple.user.service.impl;

import top.shaozuo.simple.user.domain.User;
import top.shaozuo.simple.user.repository.DatabaseUserRepository;
import top.shaozuo.simple.user.repository.UserRepository;
import top.shaozuo.simple.user.service.UserService;

public class UserServiceImpl implements UserService {

    private UserRepository userRepository = new DatabaseUserRepository();

    @Override
    public boolean register(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean deregister(User user) {
        return userRepository.deleteById(user.getId());
    }

    @Override
    public boolean update(User user) {
        return userRepository.save(user);
    }

    @Override
    public User queryUserById(Long id) {
        return userRepository.getById(id);
    }

    @Override
    public User queryUserByNameAndPassword(String name, String password) {
        return userRepository.getByNameAndPassword(name, password);
    }

    @Override
    public User queryUserByEmail(String email) {
        return userRepository.getByEmail(email);
    }

}

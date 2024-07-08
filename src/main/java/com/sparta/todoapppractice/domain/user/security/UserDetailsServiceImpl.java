package com.sparta.todoapppractice.domain.user.security;

import com.sparta.todoapppractice.domain.user.entity.User;
import com.sparta.todoapppractice.domain.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        User user = userRepository.findByUserId(userId).orElseThrow( () -> new UsernameNotFoundException("아이디, 비밀번호를 확인해주세요."));

        return new UserDetailsImpl(user);
    }

}

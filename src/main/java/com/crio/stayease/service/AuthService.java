package com.crio.stayease.service;

import com.crio.stayease.dto.LoginDto;
import com.crio.stayease.dto.LoginResponseDto;
import com.crio.stayease.dto.SignupDto;
import com.crio.stayease.dto.UserDto;
import com.crio.stayease.entity.User;
import com.crio.stayease.entity.enums.Role;
import com.crio.stayease.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public UserDto signup(SignupDto signupDto){
        Optional<User> user=userRepository.findByEmail(signupDto.getEmail());
        if(user.isPresent()){
            throw new BadCredentialsException("User with email "+signupDto.getEmail()+" already exists!");
        }
        if(signupDto.getRole()==null){
            signupDto.setRole(Role.CUSTOMER);
        }
        User tosave=modelMapper.map(signupDto,User.class);
        tosave.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        tosave=userRepository.save(tosave);
        return modelMapper.map(tosave,UserDto.class);
    }

    public LoginResponseDto login(LoginDto loginDto){
        Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword()));
        User user=(User)authentication.getPrincipal();
        String accessToken= jwtService.generateAccessToken(user);
        String refreshToken= jwtService.generateRefreshToken(user);

        return new LoginResponseDto(user.getId(),accessToken,refreshToken);
    }

    public LoginResponseDto refreshToken(String refreshToken){
        Long userId=jwtService.getUserIdFromToken(refreshToken);
        User user=userService.findUserById(userId);
        String accessToken= jwtService.generateAccessToken(user);
        return new LoginResponseDto(userId,accessToken,refreshToken);
    }
}

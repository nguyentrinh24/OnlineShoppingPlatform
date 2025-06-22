package com.project.shopapp.services.Token;

import com.project.shopapp.models.Token;
import com.project.shopapp.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public interface ITokenService {

    Token addToken(User user, String token);
}

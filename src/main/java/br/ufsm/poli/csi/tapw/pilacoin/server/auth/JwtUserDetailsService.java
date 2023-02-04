package br.ufsm.poli.csi.tapw.pilacoin.server.auth;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String emailLogin) throws UsernameNotFoundException {
        return new User(emailLogin, new BCryptPasswordEncoder().encode("teste"), new ArrayList<>());
    }

    /*private List<GrantedAuthority> getGrantedAuthorities(Eletroposto eletroposto) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (eletroposto.getAdmin() != null && eletroposto.getAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("USER"));
        }
        return authorities;
    }*/


}

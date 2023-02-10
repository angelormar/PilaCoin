package br.ufsm.poli.csi.tapw.pilacoin.server.auth;

import br.ufsm.poli.csi.tapw.pilacoin.server.model.Usuario;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String emailLogin) throws UsernameNotFoundException {
        //Usuario userbd = new Usuario();
        SessionFactory factory = new Configuration().configure().buildSessionFactory();

        // Open a Hibernate session
        Session session = factory.openSession();

        // Create a Criteria object
        Criteria criteria = session.createCriteria(Usuario.class);

        // Add a restriction to retrieve only the user with a specific name
        criteria.add(Restrictions.eq("nome", emailLogin));

        // Get the list of users that meet the criteria
        List<Usuario> users = criteria.list();

        // Close the Hibernate session
        session.close();
        return new User(emailLogin, new BCryptPasswordEncoder().encode(users.get(0).getSenha()), new ArrayList<>());
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

package br.ufsm.poli.csi.tapw.pilacoin.server.controller;

import br.ufsm.poli.csi.tapw.pilacoin.server.auth.JwtRequest;
import br.ufsm.poli.csi.tapw.pilacoin.server.auth.JwtResponse;
import br.ufsm.poli.csi.tapw.pilacoin.server.auth.JwtTokenUtil;
import br.ufsm.poli.csi.tapw.pilacoin.server.auth.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin(origins = "*")
//@Api("Autenticação")
public class AuthController {


    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService userDetailsService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    //@ApiOperation("Autentica e retorna um token válido a partir do usernamene e senha do integrador.")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token, false));
    }

    /*@RequestMapping(value = "/revalidate", method = RequestMethod.POST)
    @ApiOperation("Retorna um novo token válido. Só é acessível por clientes que já possuem um token válido, " +
            "permitindo assim revalidar o token antigo.")
    public ResponseEntity<?> revalidateToken() throws Exception {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) token.getPrincipal();
        authenticate(userDetails.getUsername(), userDetails.getPassword());
        Eletroposto eletroposto = eletropostoRepository.findEletropostoByEmailLogin(userDetails.getUsername());
        final String newtoken = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(newtoken, eletroposto.getAdmin() != null && eletroposto.getAdmin()));
    }

    @RequestMapping(value = "/changePasswd", method = RequestMethod.POST)
    @ApiOperation("Altera a senha de integrador.")
    public ResponseEntity<?> revalidateToken(@RequestParam String oldPasswd,
                                             @RequestParam String newPasswd)
            throws Exception {
        String user = getUser();
        Eletroposto eletroposto = eletropostoRepository.findEletropostoByEmailLogin(user);
        if (eletroposto == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if (!new BCryptPasswordEncoder().matches(oldPasswd, eletroposto.getSenha())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "senha antiga inválida.");
        eletroposto.setSenha(new BCryptPasswordEncoder().encode(newPasswd));
        eletropostoRepository.save(eletroposto);
        return ResponseEntity.ok("password changed");
    }

    private String getUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }*/

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

}

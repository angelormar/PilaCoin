package br.ufsm.poli.csi.tapw.pilacoin.server.repositories;

import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PilaCoinRepositoriy extends JpaRepository<PilaCoin, Long> {
}

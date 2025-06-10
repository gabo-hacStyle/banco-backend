package gabs.infrastructure.repository.springdata;

import gabs.infrastructure.repository.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataTransactionRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findBySourceProduct_IdOrTargetProduct_Id(Long sourceId, Long targetId);

}

package kz.dossier.repositoryDossier;

import kz.dossier.models.dossier.Universities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Date;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UniversitiesRepo extends JpaRepository<Universities, Integer> {
    @Query(value = "select * from public.study where iin = ?1", nativeQuery = true)
    List<Universities> getByIIN(String iin);

    @Query(value = "select iin from public.study where study_code = ?1 and end_date = ?2 and spec_name = ?3", nativeQuery = true)
    Page<String> getUniMatesByIinEndDate(String study_code, Date end_date, String spec_name,Pageable pageable);
    @Query(value = "select iin from public.study where study_code = ?1 and start_date = ?2 and spec_name = ?3", nativeQuery = true)
    Page<String> getUniMatesByIinStartDate(String study_code, Date end_date, String spec_name,Pageable pageable);
}

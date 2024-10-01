package kz.dossier.repositoryDossier;

import kz.dossier.models.dossier.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Date;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SchoolRepo extends JpaRepository<School, Integer> {
    @Query(value = "select * from public.school where iin = ?1", nativeQuery = true)
    List<School> getByIIN(String iin);
    
    @Query(value = "select iin from public.school where school_code = ?1 and end_date = ?2 and grade = ?3", nativeQuery = true)
    Page<String> getSchoolMatesByIin(String school_code, Date end_date, String grade,Pageable pageable);

}

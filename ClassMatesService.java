package kz.dossier.service;

import kz.dossier.repositoryDossier.SchoolRepo;
import kz.dossier.repositoryDossier.MvFlRepo;

import kz.dossier.repositoryDossier.UniversitiesRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import kz.dossier.models.dossier.SearchResultModelFL;
import kz.dossier.models.dossier.MvFl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.text.ParseException;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ClassMatesService {

    private final SchoolRepo schoolRepo;
    private final UniversitiesRepo universitiesRepo;
    private final MyService myService;
    private final MvFlRepo mv_FlRepo;

    public Page<SearchResultModelFL> getSchoolMates(String school_code, String end_date, String grade,Pageable pageable) throws ParseException{
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date end_date_formatted = dateFormatter.parse(end_date);
        
        Page<String> iin = schoolRepo.getSchoolMatesByIin(school_code, end_date_formatted, grade,pageable);

        List<MvFl> fls = new ArrayList<>();
                for (String ad : iin) {
                    Optional<MvFl> fl = mv_FlRepo.getByIin(ad);
                    if (fl.isPresent()) {
                        fls.add(fl.get());
                    }
                }
        
        List<SearchResultModelFL> result = myService.findWithPhoto(fls);
        return new PageImpl<>(result,pageable,iin.getTotalElements());
    }
    public Page<SearchResultModelFL> getUniMatesStartDate(String study_code, String date, String spec_name,Pageable pageable) throws ParseException{
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date_formatted = dateFormatter.parse(date);
        
        Page<String> iin = universitiesRepo.getUniMatesByIinStartDate(study_code, date_formatted, spec_name,pageable);

        List<MvFl> fls = new ArrayList<>();
                for (String ad : iin) {
                    Optional<MvFl> fl = mv_FlRepo.getByIin(ad);
                    if (fl.isPresent()) {
                        fls.add(fl.get());
                    }
                }
        
        List<SearchResultModelFL> result = myService.findWithPhoto(fls);
        return new PageImpl<>(result,pageable,iin.getTotalElements());
    }

    public Page<SearchResultModelFL> getUniMatesEndDate(String study_code, String date, String spec_name,Pageable pageable) throws ParseException{
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date_formatted = dateFormatter.parse(date);
        
        Page<String> iin = universitiesRepo.getUniMatesByIinEndDate(study_code, date_formatted, spec_name,pageable);

        List<MvFl> fls = new ArrayList<>();
                for (String ad : iin) {
                    Optional<MvFl> fl = mv_FlRepo.getByIin(ad);
                    if (fl.isPresent()) {
                        fls.add(fl.get());
                    }
                }
        
        List<SearchResultModelFL> result = myService.findWithPhoto(fls);
        return new PageImpl<>(result,pageable,iin.getTotalElements());
    }
}
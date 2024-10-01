package kz.dossier.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.dossier.dto.dossier.*;
import kz.dossier.models.dossier.*;
import kz.dossier.models.risk.*;
import kz.dossier.repositoryDossier.*;
import kz.dossier.extractor.Mv_fl_extractor;
import kz.dossier.security.jwt.JwtUtils;
import kz.dossier.security.models.Log;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialBlob;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MyService {
    private final QoldauRepo QoldauRepo;
    private final JdbcTemplate jdbcTemplate;
    private final CommodityProducerRepo commodityProducerRepo;
    private final TIpEntityRepo TIpEntityRepo;
    private final BankrotRepo bankrotRepo;
    private final MshRepo mshRepo;
    private final OpgRepo opgRepo;
    private final BlockEsfRepo block_esfRepo;
    private final TaxOutEntityRepo taxOutEntityRepo;
    private final CriminalsRepo criminalsRepo;
    private final FlPensionContrRepo flPensionContrRepo;
    private final MzEntityRepo mzEntityRepo;
    private final FlPensionMiniRepo flPensionMiniRepo;
    private final MilitaryAccounting2Repo MilitaryAccounting2Repo;
    private final MvUlFounderFlRepo mvUlFounderFlRepo;
    private final IpgoEmailEntityRepo IpgoEmailEntityRepo;
    private final MvUlFounderUlRepo mvUlFounderUlRepo;
    private final MvUlLeaderEntityRepo mvUlLeaderEntityRepo;
    private final AdvocateListEntityRepo advocateListEntityRepo;
    private final AuditorsListEntityRepo auditorsListEntityRepo;
    private final BailiffListEntityRepo bailiffListEntityRepo;
    private final AccountantListEntityRepo accountantListEntityRepo;
    private final NewPhotoRepo newPhotoRepo;
    private final MvAutoFlRepo mvAutoFlRepo;
    private final FpgTempEntityRepo fpgTempEntityRepo;
    private final MvFlRepo mv_FlRepo;
    private final OmnRepo omn_repos;
    private final OrphansRepo orphans_repo;
    private final AdmRepo admRepo;
    private final EquipmentRepo equipment_repo;
    private final MvRnOldRepo mv_rn_oldRepo;
    private final DormantRepo dormantRepo;
    private final FlRelativesRepository fl_relativesRepository;
    private final RegAddressFlRepo regAddressFlRepo;
    private final PdlRepo pdlReposotory;
    private final MvUlRepo mv_ul_repo;
    private final MvIinDocRepo mvIinDocRepo;
    private final UniversitiesRepo uniRepo;
    private final NdsEntityRepo ndsEntityRepo;
    private final SchoolRepo schoolRepo;
    private final FlContactsRepo flContactsRepo;
    private final MvIinDocRepo mv_iin_docRepo;
    private final FlRiskServiceImpl flRiskService;
    private final DirectorFounderService directorFounderService;
    private final MvUlLeaderRepository mvUlLeaderRepository;
    private final RegAddressUlEntityRepo regAddressUlEntityRepo;
    private final DismissalRepo dismissalRepo;
    private final ImmoralLifestlyeRepo immoral_lifestlyeRepo;
    private final BeneficiariesListRepo beneficiariesListRepo;
    private final ConvictsAbroadRepo convictsAbroadRepo;
    private final DrugAddictsRepo drugAddictsRepo;
    private final IncapacitatedRepo incapacitatedRepo;
    private final KuisRepo kuisRepo;
    private final MvFlAddressRepository mvFlAddressRepository;
    private final IndividualEntrepreneurRepo individualEntrepreneurRepo;
    private final IpgoEmailEntityRepo ipgoEmailEntityRepo;
    private final RegistrationTempRepository registrationTempRepository;
    private final LawyersRepo lawyersRepo;
    private final ChangeFioRepo changeFioRepo;
    private final KxRepo kxRepo;
    private final AutoTransportRepo autoTransportRepo;
    private final AviaTransportRepo aviaTransportRepo;
    private final TrainsRepo trainsRepo;
    private final WaterTransportRepo waterTransportRepo;
    private final AutoPostanovkaRepo autoPostanovkaRepo;
    private final AutoSnyatieRepo autoSnyatieRepo;
    private final QoldauRepo qoldauRepo;

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    public String getNameByIin(String iin) {
        try {
            String fls = mv_FlRepo.getNameByIIN(iin);
            return fls;
        } catch (Exception e) {
            return null;
        }
    }


    protected MvFlWithPhotoDto tryAddPhotoToDto(MvFlWithPhotoDto fl, String IIN) {
        try {
            List<PhotoDb> photos = new ArrayList<>();
            photos = newPhotoRepo.findAllByIinv(IIN);
            List<PhotoDb> photoDbs = new ArrayList<>();
            for(PhotoDb photoDb1: photos){
                photoDbs.add(photoDb1);
                fl.setPhotoDbs(photoDbs);
            }
            return fl;
        } catch (Exception e) {
            logger.error("Error: ", e);
        }
        return fl;
    }

    public ULGeneralInfoDTO getUlGeneral(String bin){
        int total = 6;
        int actual = 0;
        ULGeneralInfoDTO ulGeneralInfoDTO = new ULGeneralInfoDTO();
        try {
            ulGeneralInfoDTO.setMvUlList(mv_ul_repo.getUlByBin(bin));
            actual++;
        }catch (Exception e){
            logger.error("Error: ", e);
        }try {
            ulGeneralInfoDTO.setRegAddressUlEntity(regAddressUlEntityRepo.findByBin(bin));
            actual++;

        }catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            ulGeneralInfoDTO.setCommodityProducers(commodityProducerRepo.getiin_binByIIN(bin));
            actual++;

        }catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            ulGeneralInfoDTO.setFlContacts(flContactsRepo.findAllByIin(bin));
            actual++;

        }catch (Exception e){
            logger.error("Error: ", e);
        }try {
            ulGeneralInfoDTO.setAccountantListEntities(accountantListEntityRepo.getUsersByLikeBIN(bin));
            actual++;

        }catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            ulGeneralInfoDTO.setPdls(pdlReposotory.getByBin(bin));
            actual++;

        }catch (Exception e){
            logger.error("Error: ", e);
        }
        Double percentage = (double) (actual * 100 / total);

        return ulGeneralInfoDTO;

    }

    @Transactional(timeout = 25)
    public RnFlSameAddressDto getByAddressUsingIin(String iin) {
        List<MvFlAddress> address;
        try {
            address = mvFlAddressRepository.getMvFlAddressByIIN(iin);
        } catch (Exception e) {
            logger.error("Error occurred while fetching getByAddressUsingIin by iin: {}", iin, e);
            return null;
        }
        if (address != null) {
            AddressInfo addressInfo = new AddressInfo();
            if (address.size() > 0) {
                addressInfo.setRka(address.get(0).getRka());
                addressInfo.setRegion(address.get(0).getRegion() == null ?  null : address.get(0).getRegion());
                addressInfo.setBuilding(address.get(0).getBuilding() == null ? null : address.get(0).getBuilding());
                addressInfo.setKorpus(address.get(0).getCorpus() == null ? null : address.get(0).getCorpus());
                addressInfo.setApartment_number(address.get(0).getFlat() == null ? null : address.get(0).getFlat());
                addressInfo.setDistrict(address.get(0).getDistrict() == null ? null : address.get(0).getDistrict());
                addressInfo.setStreet(address.get(0).getStreet() == null ? null : address.get(0).getStreet());
            }
            String query = "select * from imp_knb_fl.mv_fl_address where ";
            query = query + "\"IIN\" != '" + iin + "'";
            if (addressInfo.getDistrict() != null) {
                query = query + " and \"DISTRICT\" = '" + addressInfo.getDistrict()+ "'";
            }
            if (addressInfo.getRegion() != null) {
                query = query + " and \"REGION\" = '" + addressInfo.getRegion() + "'";
            }
            if (addressInfo.getStreet() != null) {
                query = query + " and \"STREET\" = '" + addressInfo.getStreet()+ "'";
            }
            if (addressInfo.getBuilding() != null) {
                query = query + " and \"BUILDING\" = '" + addressInfo.getBuilding()+ "'";
            }
            if (addressInfo.getKorpus() != null) {
                query = query + " and \"CORPUS\" = '" + addressInfo.getKorpus()+ "'";
            }
            if (addressInfo.getApartment_number() != null) {
                query = query + " and \"FLAT\" = '" + addressInfo.getApartment_number()+ "'";
            }
            List<Map<String, Object>> resultt = new ArrayList<>();
            resultt = jdbcTemplate.queryForList(query);
            ObjectMapper objectMapper = new ObjectMapper();
            List<MvFlAddress> mvFlAddresses = new ArrayList<>();
            for(Map<String,Object> fls: resultt){
                MvFlAddress mvFlAddress = objectMapper.convertValue(fls, MvFlAddress.class);
                mvFlAddresses.add(mvFlAddress);
            }
                List<MvFl> fls = new ArrayList<>();
                for (MvFlAddress ad : mvFlAddresses) {
                    try {
                        Optional<MvFl> fl = mv_FlRepo.getByIin(ad.getIin());
                        if (fl.isPresent()) {
                            fls.add(fl.get());
                        }
                    } catch (Exception e) {
                        MvFl obj = new MvFl();
                        obj.setIin(ad.getIin());
                        obj.setLast_name(ad.getFio());
                        fls.add(obj);
                    }
                }
            RnFlSameAddressDto rnFlSameAddressDto = new RnFlSameAddressDto();
            List<SearchResultModelFL> result = findWithoutPhoto(fls);
            List<MvRnOld> mvRnOlds = mv_rn_oldRepo.getUsersByLikerka_code(addressInfo.getRka());
            List<RnOwnerDto> ownerDtos = new ArrayList<>();
            for(MvRnOld mvRnOld: mvRnOlds){
                RnOwnerDto rnOwnerDto = new RnOwnerDto();
                rnOwnerDto.setOwner_iin(mvRnOld.getOwner_iin_bin());
                rnOwnerDto.setOwner_info(mvRnOld.getOwner_iin_bin() + " - " + mvRnOld.getOwner_full_name() + ", " + mvRnOld.getRegister_emergence_rights_rus());
                ownerDtos.add(rnOwnerDto);
            }

            if (!result.isEmpty()) {
                rnFlSameAddressDto.setSearchResultModelFLList(result);
            }
            if (!mvRnOlds.isEmpty()) {
                rnFlSameAddressDto.setRnOwnerDtos(ownerDtos);
            }
            if (rnFlSameAddressDto.getRnOwnerDtos() == null && rnFlSameAddressDto.getSearchResultModelFLList() == null) {
                return null;
            } else {
                return rnFlSameAddressDto;
            }
        }
        return null;
    }
 
    public UlCardDTO getUlCard(String bin) {
        UlCardDTO ulCardDTO = new UlCardDTO();
        try {
            Optional<MvUl> ul = mv_ul_repo.getUlByBin(bin);

            if (ul.isPresent()) {
                ulCardDTO.setBin(bin);
                ulCardDTO.setName(ul.get().getFull_name_rus());
                ulCardDTO.setStatus(ul.get().getUl_status());
                ulCardDTO.setRegDate(ul.get().getOrg_reg_date());
            }

            return ulCardDTO;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return ulCardDTO;
        }
    }



    public List<FlRelativiesDTO> getRelativesInfo(String iin){
        List<Object[]> flRelativesObj;
        flRelativesObj = fl_relativesRepository.findAllByIin(iin);
        List<FlRelativiesDTO> flRelativesDtoList = new ArrayList<>();
        for (Object[] relatives:flRelativesObj) {
            FlRelativiesDTO dto = new FlRelativiesDTO();
            //--Фио
            dto.setParent_fio(relatives[3] +" "+relatives[4] +" " +relatives[5]);
            if(relatives[8]!=null){ //--Круг
                dto.setLevel(String.valueOf(relatives[8]));
            }
            if(relatives[8].toString().equals("1")){ //--Статус родственника
                if(relatives[0]!=null){
                    dto.setRelative_type(relatives[0].toString());
                }
            } else if(relatives[8].toString().equals("2")) {
                if(relatives[0]!=null) {
                    dto.setRelative_type(relatives[0] +" ("+relatives[19]+")");
                }
            } else if (relatives[8].toString().equals("3")){
                String relation="";
                if(relatives[19]!=null){
                    relation=" ("+relatives[19]+")";
                }
                if(relatives[0]!=null) {
                    dto.setRelative_type(relatives[0] +relation);
                }
            } else {
                if(relatives[0]!=null){
                    dto.setRelative_type(String.valueOf(relatives[0]));
                }
            }

            //--Дата рождения
            if(relatives[6]!=null) {
                if(relatives[6].toString().length()==10){
                    try{
                        dto.setParent_birth_date(String.valueOf(LocalDate.parse(relatives[6].toString())));
                    }catch (Exception e){
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                            LocalDate localDateTime = LocalDate.parse(relatives[6].toString(), formatter);
                            dto.setParent_birth_date(String.valueOf(localDateTime));
                        }catch (Exception ex){
                        }
                    }

                } else if(relatives[6].toString().length()==22){
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd:hh:mm:ss a");
                        LocalDateTime localDateTime = LocalDateTime.parse(relatives[6].toString(), formatter);
                        LocalDate localDate = localDateTime.toLocalDate();
                        dto.setParent_birth_date(String.valueOf(localDate));
                    }catch (Exception e){
                    }
                } else if(relatives[6].toString().length()==24){
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[yyyy/MM/dd:hh:mm:ss a]");
                        LocalDateTime localDateTime = LocalDateTime.parse(relatives[6].toString().substring(1,23), formatter);
                        LocalDate localDate = localDateTime.toLocalDate();
                        dto.setBirth_date(String.valueOf(localDate));
                    }catch (Exception e){
                    }
                }
            }

            //--Дата регистрация брака
            if(relatives[10]!=null) {
                if(relatives[10].toString().length()==10){
                    try{
                        dto.setMarriage_reg_date(String.valueOf(LocalDate.parse(relatives[10].toString())));
                    }catch (Exception e){
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                            LocalDate localDateTime = LocalDate.parse(relatives[10].toString(), formatter);
                            dto.setMarriage_reg_date(String.valueOf(localDateTime));
                        }catch (Exception ex){
                        }
                    }
                } else if(relatives[10].toString().length()==22){
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd:hh:mm:ss a");
                        LocalDateTime localDateTime = LocalDateTime.parse(relatives[10].toString(), formatter);
                        LocalDate localDate = localDateTime.toLocalDate();
                        dto.setMarriage_reg_date(String.valueOf(localDate));
                    }catch (Exception e){
                    }
                } else if(relatives[10].toString().length()==24){
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[yyyy/MM/dd:hh:mm:ss a]");
                        LocalDateTime localDateTime = LocalDateTime.parse(relatives[10].toString().substring(1,23), formatter);
                        LocalDate localDate = localDateTime.toLocalDate();
                        dto.setMarriage_reg_date(String.valueOf(localDate));
                    }catch (Exception e){
                    }
                }
            }

            //--Дата Рассторжения брака
            if(relatives[11]!=null && !relatives[11].toString().equals("(null)")) {
                if(relatives[11].toString().length()==10){
                    try{
                        dto.setMarriage_divorce_date(String.valueOf(LocalDate.parse(relatives[11].toString())));
                    }catch (Exception e){
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                            LocalDate localDateTime = LocalDate.parse(relatives[11].toString(), formatter);
                            dto.setMarriage_divorce_date(String.valueOf(localDateTime));
                        }catch (Exception ex){
                        }
                    }
                } else if(relatives[11].toString().length()==22) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd:hh:mm:ss a");
                        LocalDateTime localDateTime = LocalDateTime.parse(relatives[11].toString(), formatter);
                        LocalDate localDate = localDateTime.toLocalDate();
                        dto.setMarriage_divorce_date(String.valueOf(localDate));
                    } catch (Exception e){
                    }
                } else if(relatives[11].toString().length()==24) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[yyyy/MM/dd:hh:mm:ss a]");
                        LocalDateTime localDateTime = LocalDateTime.parse(relatives[11].toString().substring(1,23), formatter);
                        LocalDate localDate = localDateTime.toLocalDate();
                        dto.setMarriage_divorce_date(String.valueOf(localDate));
                    }catch (Exception e){
                    }
                }
            }

            //--ИИН родственника
            if(relatives[2]!=null && !relatives[2].toString().equals("---")) {
                dto.setParent_iin(relatives[2].toString());
            }

            if(!dto.getParent_fio().equals("null null null")){
                boolean isRel = true;

                if(!flRelativesDtoList.isEmpty()){
                    for(FlRelativiesDTO rel: flRelativesDtoList){
                        if((rel.getParent_iin()!=null && dto.getParent_iin()!=null) && rel.getParent_iin().equals(dto.getParent_iin())){
                            isRel=false;

                            break;
                        }

                        if((rel.getParent_fio()!=null && dto.getParent_fio()!=null) && ( rel.getParent_fio().equals(dto.getParent_fio()) )){
                            isRel=false;
                            break;
                        }

                    }
                }

                if(isRel){
                    flRelativesDtoList.add(dto);

                }
            }
        }
        return flRelativesDtoList;
    }
    public List<SearchResultModelUl> searchUlByName(String name) {
        List<MvUl> mvUls = mv_ul_repo.getUlsByName(name.replace("$", "%"));
        List<SearchResultModelUl> list = new ArrayList<>();
        for (MvUl l: mvUls) {
            SearchResultModelUl res = new SearchResultModelUl();
            res.setBin(l.getBin());
            res.setName(l.getShort_name());
            list.add(res);
        }
        return list;
    }

    public List<SearchResultModelFL> getWIthAddFields(HashMap<String, String> req) {
        List<MvAutoFl> list = new ArrayList<>();
        if (req.get("vin") != "") {
            list =  mvAutoFlRepo.findBYVIN(req.get("vin"));
        }
        if (list.size() < 1) {
            String sql = createAdditionSQL(req);
            List<MvFl> fls = jdbcTemplate.query(sql, new Mv_fl_extractor());
            List<SearchResultModelFL> result = findWithPhoto(fls);
            return result;
        } else {
            List<MvFl> fls = new ArrayList<>();
            for (MvAutoFl i: list) {
                try {
                    MvFl r = mv_FlRepo.getUserByIin(i.getIin());
                    fls.add(r);
                } catch (Exception e) {
                }
            }
            List<SearchResultModelFL> result = findWithPhoto(fls);
            return result;
        }
    }

//    public FlRelatives getFlRelativesInfo()

    @Transactional(timeout = 25)
    public List<SearchResultModelUl> getByAddress(String bin) {
        RegAddressUlEntity addressUlEntity = regAddressUlEntityRepo.findByBin(bin);
        List<RegAddressUlEntity> units = regAddressUlEntityRepo.getByAddress(addressUlEntity.getRegAddrRegionRu(), addressUlEntity.getRegAddrDistrictRu(), addressUlEntity.getRegAddrStreetRu(), addressUlEntity.getRegAddrBuildingNum(),bin );
//        List<RegAddressUlEntity> units = regAddressUlEntityRepo.getByFullAddress(addressUlEntity.getRegAddrRegionRu(), addressUlEntity.getRegAddrDistrictRu(), addressUlEntity.getRegAddrLocalityRu(), addressUlEntity.getRegAddrStreetRu(), addressUlEntity.getRegAddrBuildingNum(), bin);

        List<SearchResultModelUl> list = new ArrayList<>();
        for (RegAddressUlEntity l: units) {
            if (l.getActive()) {
                Optional<MvUl> ul = mv_ul_repo.getUlByBin(l.getBin());
                if (ul.isPresent()) {
                    SearchResultModelUl res = new SearchResultModelUl();
                    res.setBin(ul.get().getBin());
                    res.setName(ul.get().getShort_name());
                    res.setRegion(addressUlEntity.getRegAddrRegionRu() + " " + addressUlEntity.getRegAddrDistrictRu() + " " + addressUlEntity.getRegAddrStreetRu() + " " + addressUlEntity.getRegAddrBuildingNum());
                    list.add(res);
                }
            }
        }
        return list;
    }

    private String createAdditionSQL(HashMap<String, String> req) {
        String sql = "select * from ser.mv_fl where first_name like '" + req.get("i").replace('$', '%') + "' and  patronymic like '" + req.get("o").replace('$', '%') + "' and last_name like '" + req.get("f").replace('$', '%') + "' ";
        if (req.get("dateFrom") != "") {
            sql = sql + "AND toDate(birth_date, 'YYYY-MM-DD') > toDate('" + req.get("dateFrom") + "', 'YYYY-MM-DD') ";
        }
        if (req.get("dateTo") != "") {
            sql = sql + "AND toDate(birth_date, 'YYYY-MM-DD') < toDate('" + req.get("dateTo") + "', 'YYYY-MM-DD') ";
        }
        if (req.get("gender") != "") {
            sql = sql + "AND gender = '" + req.get("gender") + "' ";
        }
        if (req.get("nation") != "") {
            sql = sql + "AND nationality_ru_name = '" + req.get("nation").toUpperCase() + "' ";
        }
        if (req.get("city") != "") {
            sql = sql + "AND district = '" + req.get("city").toUpperCase() + "' ";
        }
        if (req.get("country") != "") {
            sql = sql + "AND citizenship_ru_name = '" + req.get("country").toUpperCase() + "' ";
        }
        if (req.get("region") != "") {
            sql = sql + "AND region = '" + req.get("region").toUpperCase() + "' ";
        }
        if (req.get("region") != "") {
            sql = sql + "AND region = '" + req.get("region").toUpperCase() + "' ";
        }
        return sql;
    }
    public Page<SearchResultModelFL> getByIIN_photo(String IIN,Pageable pageable) {
        Page<MvFl> fls = mv_FlRepo.getUsersByLikePage(IIN,pageable);
        List<SearchResultModelFL> result = findWithPhoto(fls.getContent());
        return new PageImpl<>(result, pageable, fls.getTotalElements());
    }
    public List<SearchResultModelFL> getByDocNumber_photo(String doc_number) {
        String iin = mvIinDocRepo.getIinByDoc_Number(doc_number);
        List<MvFl> fls = mv_FlRepo.getUsersByLike(iin);
        List<SearchResultModelFL> result = findWithPhoto(fls);
        return result;
    }

    public Page<SearchResultModelFL> getByPhone(String phone,Pageable pageable) {
        Page<String> iin = flContactsRepo.getByPhoneNumber(phone,pageable);
        List<MvFl> fls = new ArrayList<>();
        for (String ii: iin.getContent()) {
            MvFl person = mv_FlRepo.getUserByIin(ii);
            fls.add(person);
        }
        List<SearchResultModelFL> result = findWithPhoto(fls);
        return new PageImpl<>(result,pageable,iin.getTotalElements());
    }
    public Page<SearchResultModelFL> getByEmail(String email,Pageable pageable) {
        Page<String> iin = flContactsRepo.getByEmailPage(email,pageable);
        List<MvFl> fls = new ArrayList<>();
        for (String ii: iin.getContent()) {
            MvFl person = mv_FlRepo.getUserByIin(ii);
            fls.add(person);
        }
        List<SearchResultModelFL> result = findWithPhoto(fls);
        return new PageImpl<>(result, pageable, iin.getTotalElements());
    }
    public Page<SearchResultModelFL> getByVinFl(String vin,Pageable pageable) {
        Page<String> iin = mvAutoFlRepo.getByVinPage(vin,pageable);
        List<MvFl> fls = new ArrayList<>();
        for (String ii: iin.getContent()) {
            MvFl person = mv_FlRepo.getUserByIin(ii);
            fls.add(person);
        }
        try {
            List<SearchResultModelFL> result = findWithPhoto(fls);
            return new PageImpl<>(result, pageable, iin.getTotalElements());

        }catch (Exception e){
            logger.error("Error: ", e);
        }
        return null;
    }
    public List<SearchResultModelUl> getByVinUl(String vin) {
        String VIN_upper = vin.toUpperCase();
        List<String> iin = mvAutoFlRepo.getByVin(VIN_upper);
        List<SearchResultModelUl> list = new ArrayList<>();
        if (iin.size() > 0) {
            List<MvUl> mvUls = mv_ul_repo.getUsersByLike(iin.get(0));
            for (MvUl l: mvUls) {
                SearchResultModelUl res = new SearchResultModelUl();
                res.setBin(l.getBin());
                res.setName(l.getFull_name_rus());
                list.add(res);
            }
            return list;
        } else {
            return list;
        }

    }

    public List<SearchResultModelFL> getByDoc_photo(String doc) {
        Optional<String> fls = mv_iin_docRepo.getIinByDocN(doc);
        List<MvFl> fls1 = new ArrayList<>();
        if (fls.isPresent()) {
            fls1 = mv_FlRepo.getUsersByLike(fls.get());
        } else {
            return new ArrayList<>();
        }
        // for(MvIinDoc flss : fls){
        //     System.out.println(flss.getIin());
        //     fls1 = mv_FlRepo.getUsersByLike(flss.getIin());
        // }
        List<SearchResultModelFL> result = findWithPhoto(fls1);
        return result;
    }

    public Page<SearchResultModelFL> getByFIO_photo(String i, String o, String f, Pageable pageable) {
        Page<MvFl> fls = mv_FlRepo.getUsersByFIOPage(i, o, f,pageable);

        List<SearchResultModelFL> result = findWithPhoto(fls.getContent());
        return new PageImpl<>(result, pageable, fls.getTotalElements());
    }
    protected List<SearchResultModelFL> findWithPhoto(List<MvFl> fls) {
        List<SearchResultModelFL> result = new ArrayList<>();
        for (MvFl fl: fls) {
            SearchResultModelFL person = new SearchResultModelFL();
            person.setFirst_name(fl.getFirst_name());
            person.setLast_name(fl.getLast_name());
            person.setPatronymic(fl.getPatronymic());
            person.setIin(fl.getIin());
            tryAddPhoto(person, fl.getIin());

            result.add(person);
        }
        return result;
    }

//    private Page<SearchResultModelFL> findWithPhotoPage(List<MvFl> fls) {
//        List<SearchResultModelFL> result = new ArrayList<>();
//        for (MvFl fl: fls) {
//            SearchResultModelFL person = new SearchResultModelFL();
//            person.setFirst_name(fl.getFirst_name());
//            person.setLast_name(fl.getLast_name());
//            person.setPatronymic(fl.getPatronymic());
//            person.setIin(fl.getIin());
//            tryAddPhoto(person, fl.getIin());
//
//            result.add(person);
//        }
//        return result;
//    }
    protected List<SearchResultModelFL> findWithoutPhoto(List<MvFl> fls) {
        List<SearchResultModelFL> result = new ArrayList<>();
        for (MvFl fl: fls) {
            SearchResultModelFL person = new SearchResultModelFL();
            person.setFirst_name(fl.getFirst_name());
            person.setLast_name(fl.getLast_name());
            person.setPatronymic(fl.getPatronymic());
            person.setIin(fl.getIin());

            result.add(person);
        }
        return result;
    }

    private SearchResultModelFL tryAddPhoto(SearchResultModelFL fl, String IIN) {
        try {
            Optional<PhotoDb> flRawPhoto = newPhotoRepo.findById(IIN);
            fl.setPhoto(flRawPhoto.get().getPhoto());
            return fl;
        } catch (Exception e) {
            logger.error("Error: ", e);
        }
        return fl;
    }
    private NodesFL tryAddPhoto(NodesFL node, String IIN) {
        try {
            List<PhotoDb> photos = new ArrayList<>();
            photos = newPhotoRepo.findAllByIinv(IIN);
            List<PhotoDb> photoDbs = new ArrayList<>();
            for(PhotoDb photoDb1: photos){
                photoDbs.add(photoDb1);
                node.setPhotoDbf(photoDbs);
            }
            return node;
        } catch (Exception e) {
            logger.error("Error: ", e);
        }
        return node;
    }
    private FlFirstRowDto tryAddPhoto(FlFirstRowDto fl, String IIN) {
        try {
            List<PhotoDb> photos = new ArrayList<>();
            photos = newPhotoRepo.findAllByIinv(IIN);
            List<PhotoDb> photoDbs = new ArrayList<>();
            for(PhotoDb photoDb1: photos){
                photoDbs.add(photoDb1);
                fl.setPhotoDbf(photoDbs);
            }
            return fl;
        } catch (Exception e) {
            logger.error("Error: ", e);
        }
        return fl;
    }
    private Children tryAddPhoto(Children hierarchy, String IIN) {
        try {
            List<PhotoDb> photos = new ArrayList<>();
            photos = newPhotoRepo.findAllByIinv(IIN);
            List<PhotoDb> photoDbs = new ArrayList<>();
            for(PhotoDb photoDb1: photos){
                photoDbs.add(photoDb1);
                hierarchy.image = Arrays.toString(photoDbs.get(0).getPhoto());
            }
            return hierarchy;
        } catch (Exception e) {
            logger.error("Error: ", e);
        }
        return hierarchy;
    }
    private Map<String, Object> getPropertyMap(Object obj) {
        Map<String, Object> properties = new HashMap<>();

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field: fields) {
            try {
                Object value = field.get(obj);
                properties.put(field.getName(), value);
            } catch (IllegalAccessException e){
//                e.printStackTrace();
            }
        }
        return properties;
    }

    //General info by iin
    public GeneralInfoDTO generalInfoByIin(String iin) {
        GeneralInfoDTO generalInfoDTO = new GeneralInfoDTO();
        int total = 13;
        int actual = 0;
        try {
            List<FlContacts> contacts = flContactsRepo.findAllByIin(iin);
            if (contacts != null) {
                actual++;
                generalInfoDTO.setContacts(contacts);
            }
        } catch (Exception e) {
            logger.error("Error: ", e);
        }
        try {
            List<AccountantListEntity> accountantListEntities = accountantListEntityRepo.getUsersByLike(iin);
            if (accountantListEntities != null) {
                actual++;

                generalInfoDTO.setAccountantListEntities(accountantListEntities);
                try {
                    for(AccountantListEntity accountantListEntity: accountantListEntities){
                        accountantListEntity.setBinName(mv_ul_repo.getNameByBin(accountantListEntity.getBin()));
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            logger.error("Error: ", e);
        }
        try {
            List<AdvocateListEntity> advocateListEntities = advocateListEntityRepo.getUsersByLike(iin);
            if (advocateListEntities != null) {
                actual++;

                generalInfoDTO.setAdvocateListEntities(advocateListEntities);

            }
        } catch (Exception e) {
            logger.error("Error: ", e);
        }
        try {
            List<IpgoEmailEntity> ipgoEmailEntities = ipgoEmailEntityRepo.getUsersByLike(iin);
            if (ipgoEmailEntities != null) {
                actual++;

                generalInfoDTO.setIpgoEmailEntities(ipgoEmailEntities);

            }
        } catch (Exception e) {
            logger.error("Error: ", e);
        }
        try {
            List<AuditorsListEntity> auditorsListEntities = auditorsListEntityRepo.getUsersByLike(iin);
            if (auditorsListEntities != null) {
                actual++;

                generalInfoDTO.setAuditorsListEntities(auditorsListEntities);
            }
        } catch (Exception e) {
            logger.error("Error: ", e);
        }
        try {
            List<BailiffListEntity> bailiffListEntities = bailiffListEntityRepo.getUsersByLike(iin);
            if (bailiffListEntities != null) {
                actual++;

                generalInfoDTO.setBailiffListEntities(bailiffListEntities);
            }
        } catch (Exception e) {
            logger.error("Error: ", e);
        }
        try {
            List<MvUlFounderFl> mvUlFounderFls = mvUlFounderFlRepo.getUsersByLikeIIN(iin);
            if (mvUlFounderFls != null) {
                actual++;

                generalInfoDTO.setMvUlFounderFls(mvUlFounderFls);
            }
        } catch (Exception e) {
            logger.error("Error: ", e);
            logger.error("Error: ", e);
        }
        try {
            List<RegAddressFl> address = regAddressFlRepo.getByPermanentIin(iin);
            if(address != null) {
                AddressInfo addressInfo = new AddressInfo();
                if (address.size() > 0) {
                    actual++;

                    addressInfo.setRegion(address.get(0).getRegion());
                    addressInfo.setDistrict(address.get(0).getDistrict());
                    addressInfo.setCity(address.get(0).getCity());
                    addressInfo.setStreet(address.get(0).getStreet());
                    addressInfo.setBuilding(address.get(0).getBuilding());
                    addressInfo.setKorpus(address.get(0).getKorpus());
                    addressInfo.setApartment_number(address.get(0).getApartment_number());
                }

                List<RegAddressFl> units = regAddressFlRepo.getByAddress(addressInfo.getRegion(), addressInfo.getDistrict(), addressInfo.getCity(), addressInfo.getStreet(), addressInfo.getBuilding(), addressInfo.getKorpus(), addressInfo.getApartment_number());
                List<MvFl> fls = new ArrayList<>();
                for (RegAddressFl ad : units) {
                    Optional<MvFl> fl = mv_FlRepo.getByIin(ad.getIin());
                    if (fl.isPresent()) {
                        fls.add(fl.get());
                    }
                }
                List<SearchResultModelFL> result = findWithoutPhoto(fls);
                generalInfoDTO.setSameAddressFls(result);
            }
        } catch (Exception e) {
            logger.error("Error: ", e);
        }
        try {
            List<Lawyers> lawyers = lawyersRepo.getByIin(iin);

            if (lawyers != null) {
                actual++;

                generalInfoDTO.setLawyers(lawyers);
            }
        } catch (Exception e) {
            logger.error("Error: ", e);
        }
        try {
//            List<ChangeFioDTO> changeFioDTOS = new ArrayList<>();
            List<ChangeFio> changeFio = changeFioRepo.getByIin(iin);
//            changeFio.forEach(x -> {
//                ChangeFioDTO obj = new ChangeFioDTO();
//                obj.setDateOfChange(x.getTo_date());
//                String name = "";
//                if (x.getSurname_before() != null) {
//                    name = x.getSurname_before() + " ";
//                }
//                if (x.getName_before() != null) {
//                    name = x.getName_before() + " ";
//                }
//                if (x.getSecondname_before() != null) {
//                    name = x.getSecondname_before();
//                }
//                obj.setHistoricalFIO(name);
//                obj.setReasonOfChange(x.getRemarks() != null ? x.getRemarks() : "");
//            });
            generalInfoDTO.setChangeFio(changeFio);
        } catch (Exception e) {
            logger.error("Error: ", e);
        }
        try {
            List<Pdl> pdls = pdlReposotory.getByIIN(iin);
            actual++;

            generalInfoDTO.setPdls(pdls);
        } catch (Exception e) {
            logger.error("Error: ", e);
        }
        Double percentage = Double.valueOf(actual * 100 / total);
        generalInfoDTO.setPercent(percentage);
        return generalInfoDTO;
    }


    public List<PensionListDTO> getPensionDetails(String iin, String bin, String year) {
        List<PensionListDTO> pensions = new ArrayList<>();
        List<Map<String, Object>> fl_pension_contrss = new ArrayList<>();
        fl_pension_contrss = flPensionContrRepo.getAllByCompanies(iin,bin, Integer.parseInt(year));


        for (Map<String, Object> pen : fl_pension_contrss) {
            PensionListDTO pensionListEntity = new PensionListDTO();
            pensionListEntity.setBin(bin);
            pensionListEntity.setName((String)fl_pension_contrss.get(0).get("P_NAME"));
            pensionListEntity.setPeriod(pen.get("pay_month") !=null ? pen.get("pay_month").toString() : "");
            pensionListEntity.setSum010((Double) pen.get("amount010") != null ? (double)pen.get("amount010") : 0.0);
            pensionListEntity.setSum012((Double)pen.get("amount012") != null ? (double)pen.get("amount012") : 0.0);

            pensions.add(pensionListEntity);
        }

        return pensions;
    }

    protected List<MvRnOld> setNamesByBin(List<MvRnOld> list) {
        for (MvRnOld a : list) {
            try {
                String name = mv_ul_repo.getNameByBin(a.getOwner_iin_bin());
                if (name != null) {
                    a.setOwner_full_name(name);
                }
            } catch (Exception e) {
                logger.error("Error: ", e);
                logger.error("Error occurred while fetching setNamesByBin by iin: {}", a.getOwner_iin_bin(), e);
            }
        }
        return list;
    }

    //Additional Info by iin
    public AdditionalInfoDTO additionalInfoByIin(String iin) {
        AdditionalInfoDTO additionalInfoDTO = new AdditionalInfoDTO();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<MilitaryAccounting2Entity> militaryAccounting2Entities = MilitaryAccounting2Repo.getUsersByLike(iin);
            List<MilitaryAccountingDTO> militaryAccountingDTOS = new ArrayList<>();
            if(!militaryAccounting2Entities.isEmpty() & militaryAccounting2Entities.size() > 0){
            try {
                for(MilitaryAccounting2Entity militaryAccounting2Entity: militaryAccounting2Entities){
                    MilitaryAccountingDTO militaryAccountingDTO = objectMapper.convertValue(militaryAccounting2Entity, MilitaryAccountingDTO.class);
                    militaryAccountingDTO.setBinName(mv_ul_repo.getNameByBin(militaryAccountingDTO.getBin()));
                    militaryAccountingDTOS.add(militaryAccountingDTO);
                }
                additionalInfoDTO.setMilitaryAccounting2Entities(militaryAccountingDTOS);
            } catch (Exception e) {
            }
            }
        } catch (Exception e){
        }
        try {
            List<KX> kxes = kxRepo.getKxIin(iin);
            if (kxes != null) {

                additionalInfoDTO.setKxes(kxes);
            }
        } catch (Exception e) {
            logger.error("Error: ", e);
        }
        try {
            List<MvUlLeaderEntity> mvUlLeaders = mvUlLeaderEntityRepo.getUsersByLikeIin(iin);
            if (mvUlLeaders != null) {

                additionalInfoDTO.setUl_leaderList(mvUlLeaders);
            }
        } catch (Exception e) {
            logger.error("Error: ", e);
        }try {
            List<IndividualEntrepreneur> individualEntrepreneurs = individualEntrepreneurRepo.getByIin(iin);

            additionalInfoDTO.setIndividualEntrepreneurs(individualEntrepreneurs);
        }catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            List<MvRnOld> mvRnOlds = mv_rn_oldRepo.getUsersByLike(iin);
            List<MvRnOld> list = setNamesByBin(mvRnOlds);
            additionalInfoDTO.setMvRnOlds(list);
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            List<MvAutoFl> myMv_auto_fl =  mvAutoFlRepo.getUsersByLike(iin);
            try {
                additionalInfoDTO.setMvAutoFls(myMv_auto_fl);
            } catch (Exception e) {
                System.out.println("mv_auto_fl Error: " + e);
            }

        } catch (Exception e){
            System.out.println("mv_auto_fl WRAP Error:" + e);
        }
        try {
            List<Equipment> myEquipment =  equipment_repo.getUsersByLike(iin);
            additionalInfoDTO.setEquipment(myEquipment);
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            List<Trains> trains =  trainsRepo.getByIIN(iin);
            additionalInfoDTO.setTrains(trains);
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            List<WaterTransport> waterTransports =  waterTransportRepo.getWaterByIin(iin);
            additionalInfoDTO.setWaterTransports(waterTransports);
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            List<AutoTransport> autoTransports =  autoTransportRepo.getAutoByIin(iin);
            additionalInfoDTO.setAutoTransports(autoTransports);
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            List<Equipment> equipment =  equipment_repo.getUsersByLike(iin);
            additionalInfoDTO.setEquipment(equipment);
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            additionalInfoDTO.setAutoPostanovkas(autoPostanovkaRepo.getAutoPostanovkaByIin(iin));
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            additionalInfoDTO.setAutoSnyaties(autoSnyatieRepo.getAutoSnyatieByIin(iin));
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            List<AviaTransport> aviaTransports =  aviaTransportRepo.getAviaByIin(iin);
            additionalInfoDTO.setAviaTransports(aviaTransports);
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            additionalInfoDTO.setUniversities(uniRepo.getByIIN(iin));
        } catch (Exception e){
            logger.error("Error: ", e);
        }

        try {
            additionalInfoDTO.setSchools(schoolRepo.getByIIN(iin));
        } catch (Exception e){
            logger.error("Error: ", e);
        }try {
            additionalInfoDTO.setCommodityProducers(commodityProducerRepo.getiin_binByIIN(iin));
        } catch (Exception e){
            logger.error("Error: ", e);
        }

        try {
            List<String> companyBins = flPensionContrRepo.getUsersByLikeCompany(iin);
        
            List<PensionListDTO> pensions = new ArrayList<>();
            List<PensionGroupDTO> result = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("#");
            df.setMaximumFractionDigits(0);
            for (String bin : companyBins) {
                List<Map<String, Object>> fl_pension_contrss = new ArrayList<>();
                fl_pension_contrss = flPensionContrRepo.getAllByCompanies(iin,bin);
                PensionGroupDTO obj = new PensionGroupDTO();
                List<PensionListDTO> group = new ArrayList<>();
                String name = "";
                if (fl_pension_contrss.get(0).get("P_NAME") != null) {
                    name = (String)fl_pension_contrss.get(0).get("P_NAME") + ", ";
                }
                if (bin != null) {
                    name = name + bin + ", период ";
                }
                List<String> distinctPayDates = fl_pension_contrss.stream()
                        .map(pension -> pension.get("pay_date").toString())
                        .distinct()
                        .collect(Collectors.toList());

                double knp010sum = 0.0;
                double knp012sum = 0.0;

                for (String year : distinctPayDates) {
                    if (year != null) {
                        name = name + year.replace(".0", "") + ", ";
                    }
                    PensionListDTO pensionListEntity = new PensionListDTO();
                    pensionListEntity.setBin(bin);
                    pensionListEntity.setName((String)fl_pension_contrss.get(0).get("P_NAME"));
                    pensionListEntity.setPeriod(year.replace(".0", ""));
                    try {
                        double knp010 = fl_pension_contrss.stream()
                            .filter(pension -> pension.get("pay_date").toString().equals(year) && pension.get("KNP").toString().equals("010"))
                            .mapToDouble(pension -> Double.parseDouble(pension.get("AMOUNT").toString()))
                            .sum();

                        pensionListEntity.setSum010(knp010);

                        knp010sum = knp010sum + knp010;

                    } catch (Exception e) {

                    }
                    try {
                        double knp012 = fl_pension_contrss.stream()
                        .filter(pension -> pension.get("pay_date").toString().equals(year) && pension.get("KNP").toString().equals("012"))
                        .mapToDouble(pension -> Double.parseDouble(pension.get("AMOUNT").toString()))
                        .sum();

                        pensionListEntity.setSum012(knp012);
                        knp012sum = knp012sum + knp012;
                    } catch (Exception e) {

                    }
                    pensions.add(pensionListEntity);
                    group.add(pensionListEntity);
                }
                name = name + "общая сумма КНП(010): " + df.format(knp010sum) + ", общая сумма КНП(012): " + df.format(knp012sum);
                obj.setName(name);
                obj.setList(group);
                result.add(obj);
            }

            additionalInfoDTO.setPensions(pensions);
            additionalInfoDTO.setNumber();
            additionalInfoDTO.setPensionsGrouped(result);
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        return additionalInfoDTO;
    }


    public FlRelativesLevelDto createHierarchyObject(String IIN) throws SQLException {
        List<MvFl> myMv_fl =  mv_FlRepo.getUsersByLike(IIN);
        Children hierarchy = new Children();
        hierarchy.name = myMv_fl.get(0).getIin();
        hierarchy.value = "MAIN";
        tryAddPhoto(hierarchy, IIN);
        List<FlRelativiesDTO> flRelativesDtos = new ArrayList<>();
        FlRelativesLevelDto nodes = new FlRelativesLevelDto();

        MvFl flRaw = mv_FlRepo.getUserByIin(IIN);

        if(flRaw.getIin()!=null){
            nodes.setIin(flRaw.getIin());
        }

        int mainQuintity = flRiskService.findFlRiskByIin(IIN).getQuantity();
        //--Основной ФЛ
        nodes.setName(IIN+", " + (flRaw.getLast_name()!=null?flRaw.getLast_name():"") +" " + (flRaw.getFirst_name()!=null?flRaw.getFirst_name():"") +" "
                + (flRaw.getPatronymic()!=null?flRaw.getPatronymic():"")+", Риски к-во: " + mainQuintity);

        if(mainQuintity!=0){
            nodes.setHaveRisk(true);
        } else{
            nodes.setHaveRisk(false);
        }
        int dirFounderQuantity = directorFounderService.getDirectorOrFounder(IIN).getQuantity();
        if(dirFounderQuantity!=0){
            nodes.setIsDirector(true);
        } else{
            nodes.setHaveRisk(false);
        }
        nodes.setFio((flRaw.getLast_name()!=null?flRaw.getLast_name():"") +" " + (flRaw.getFirst_name()!=null?flRaw.getFirst_name():"") +" "
                + (flRaw.getPatronymic()!=null?flRaw.getPatronymic():""));


        Optional<PhotoDb> flRawPhoto = newPhotoRepo.findById(IIN);

        if (flRawPhoto.isPresent()) {
            try {
                nodes.setPhoto(new SerialBlob(flRawPhoto.get().getPhoto()));
            } catch (SQLException e) {
            }
        }
        List<FlRelativesLevelDto> relativesNodes = new ArrayList<>();
        List<Object[]> flRelativesObj = fl_relativesRepository.findAllByIin(IIN);

        FlRelativiesDTO relativesDto = new FlRelativiesDTO();
        relativesDto.setParent_iin(nodes.getIin());
        relativesDto.setParent_fio(nodes.getFio());
        flRelativesDtos.add(relativesDto);

        //--1-Круг
        for (Object[] flRelObj:flRelativesObj.stream()
                .filter(objects -> objects[8].toString().equals("1"))
                .collect(Collectors.toList())){

            FlRelativesLevelDto firstLevel = new FlRelativesLevelDto();
            int firstLevelCnt = 0;
            int dirFounderQuantityfirstlvl = 0;
            firstLevel.setName(flRelObj[2]+", " + flRelObj[3] +" "+flRelObj[4] +" " +flRelObj[5] +", " + flRelObj[0]);

            firstLevel.setFio(flRelObj[3] +" "+flRelObj[4] +" " +flRelObj[5]);

            List<FlRelativesLevelDto> relativesNodesList = new ArrayList<>();

            if(flRelObj[2]!=null && !flRelObj[2].toString().equals("---")){

                if(flRelObj[2].toString().length()==12){
                    Optional<PhotoDb> flRawPhoto1Level = newPhotoRepo.findById(flRelObj[2].toString());
                    if (flRawPhoto1Level.isPresent()) {
                        try {
                            firstLevel.setPhoto(new SerialBlob(flRawPhoto1Level.get().getPhoto()));
                        } catch (SQLException e) {
                        }
                    }
                    firstLevelCnt = flRiskService.findFlRiskByIin(flRelObj[2].toString()).getQuantity();
                    dirFounderQuantityfirstlvl = directorFounderService.getDirectorOrFounder(flRelObj[2].toString()).getQuantity();

                }

                firstLevel.setIin(String.valueOf(flRelObj[2]));

                FlRelativiesDTO firstLvlDto = new FlRelativiesDTO();
                firstLvlDto.setParent_iin(firstLevel.getIin());
                firstLvlDto.setParent_fio(firstLevel.getFio());
                flRelativesDtos.add(firstLvlDto);

                //--2-Круг
                for (Object[] rel2Level:flRelativesObj.stream().filter(objects -> objects[8].toString().equals("2"))
                        .collect(Collectors.toList())){

                    if(flRelObj[2].toString().equals(rel2Level[1].toString())){
                        FlRelativesLevelDto secondLevel = new FlRelativesLevelDto();
                        List<FlRelativesLevelDto> relativesNodesList3Level = new ArrayList<>();
                        String relation="";
                        int secondLevelCnt =0;
                        int dirFounderQuantitysecondlvl = 0;
                        if(rel2Level[19]!=null){
                            relation=" ("+rel2Level[19]+")";
                        }
                        secondLevel.setName(rel2Level[2]+", "+rel2Level[3] +" "+rel2Level[4] +" " +rel2Level[5] +", " +rel2Level[0] +relation);
                        secondLevel.setFio(rel2Level[3] +" "+rel2Level[4] +" " +rel2Level[5]);

                        if(rel2Level[2].toString().length()==12){
                            Optional<PhotoDb> flRawPhoto2Level = newPhotoRepo.findById(rel2Level[2].toString());
                            if (flRawPhoto2Level.isPresent()) {
                                try {
                                    secondLevel.setPhoto(new SerialBlob(flRawPhoto2Level.get().getPhoto()));
                                } catch (SQLException e) {
                                }
                            }
                            secondLevelCnt = flRiskService.findFlRiskByIin(rel2Level[2].toString()).getQuantity();
                            dirFounderQuantitysecondlvl = directorFounderService.getDirectorOrFounder(rel2Level[2].toString()).getQuantity();
                        }

                        if(secondLevelCnt!=0){
                            secondLevel.setHaveRisk(true);
                        } else{
                            secondLevel.setHaveRisk(false);
                        }
                        if(dirFounderQuantitysecondlvl!=0){
                            secondLevel.setIsDirector(true);
                        } else{
                            secondLevel.setIsDirector(false);
                        }
                        secondLevel.setName(secondLevel.getName() + ", Риски к-во: " + secondLevelCnt);


                        if(rel2Level[2]!=null && !rel2Level[2].toString().equals("---")){
                            secondLevel.setIin(String.valueOf(rel2Level[2]));

                            //--3 - Круг
                            for (Object[] rel3Level:flRelativesObj.stream().filter(objects -> objects[8].toString().equals("3"))
                                    .collect(Collectors.toList())){

                                if(rel2Level[2].toString().equals(rel3Level[1].toString())) {
                                    FlRelativesLevelDto thirdLevel = new FlRelativesLevelDto();
                                    String relation3Level="";
                                    if(rel3Level[19]!=null){
                                        relation3Level=" ("+rel3Level[19]+")";
                                    }
                                    int thirdLevelCnt =0;
                                    int dirFounderQuantitythirdlvl =0;
                                    thirdLevel.setFio(rel3Level[3] +" "+rel3Level[4] +" " +rel3Level[5]);

                                    if(rel3Level[2]!=null && !rel3Level[2].toString().equals("---")){
                                        thirdLevel.setIin(String.valueOf(rel3Level[2]));

                                        Optional<PhotoDb> flRawPhoto3Level = newPhotoRepo.findById(rel3Level[2].toString());
                                        if (flRawPhoto3Level.isPresent()) {
                                            try {
                                                thirdLevel.setPhoto(new SerialBlob(flRawPhoto3Level.get().getPhoto()));
                                            } catch (SQLException e) {
                                            }
                                        }

                                        thirdLevelCnt = flRiskService.findFlRiskByIin(rel3Level[2].toString()).getQuantity();
                                        dirFounderQuantitythirdlvl = directorFounderService.getDirectorOrFounder(rel3Level[2].toString()).getQuantity();
                                    }

                                    if(thirdLevelCnt!=0){
                                        thirdLevel.setHaveRisk(true);
                                    } else{
                                        thirdLevel.setHaveRisk(false);
                                    }
                                    if(dirFounderQuantitythirdlvl!=0){
                                        thirdLevel.setIsDirector(true);
                                    } else{
                                        thirdLevel.setIsDirector(false);
                                    }
                                    thirdLevel.setName(rel3Level[2]+", " + rel3Level[3] +" "+rel3Level[4] +" " +rel3Level[5] + ", " +rel3Level[0] + relation3Level +", Риски к-во: " + thirdLevelCnt);
                                    if(!thirdLevel.getFio().equals("null null null")){

                                        FlRelativiesDTO thirdLevelDto = new FlRelativiesDTO();
                                        thirdLevelDto.setParent_iin(thirdLevelDto.getIin());
                                        thirdLevelDto.setParent_fio(thirdLevelDto.getFio());
//                                           thirdLevelDto.setRelativeBirthDate(getBirthDate(relation3Level[6]));

                                        boolean isRel = true;

                                        if(!flRelativesDtos.isEmpty()){
                                            for(FlRelativiesDTO rel: flRelativesDtos){
                                                if((rel.getParent_iin()!=null && thirdLevelDto.getIin()!=null) && rel.getParent_iin().equals(thirdLevelDto.getIin())){
                                                    isRel=false;
                                                    break;
                                                }

                                                if((rel.getParent_fio()!=null && thirdLevelDto.getFio()!=null) && ( rel.getParent_fio().equals(thirdLevelDto.getFio()) )){
                                                    isRel=false;
                                                    break;
                                                }
                                            }
                                        }

                                        if(isRel){
                                            flRelativesDtos.add(thirdLevelDto);
                                            relativesNodesList3Level.add(thirdLevel);
                                        }
                                    }
                                }

                            }
                        }

                        secondLevel.setChildren(relativesNodesList3Level);
                        if(!secondLevel.getFio().equals("null null null")){

                            boolean isRel = true;

                            FlRelativiesDTO secondLvlDto = new FlRelativiesDTO();
                            secondLvlDto.setParent_iin(secondLevel.getIin());
                            secondLvlDto.setParent_fio(secondLevel.getFio());
//                            secondLvlDto.setBirthDate(getBirthDate(rel2Level[6]));
                            if(!flRelativesDtos.isEmpty()){
                                for(FlRelativiesDTO rel: flRelativesDtos){
                                    if((rel.getParent_iin()!=null && secondLevel.getIin()!=null) && rel.getParent_iin().equals(secondLevel.getIin())){
                                        isRel=false;
                                        break;
                                    }

                                    if((rel.getParent_fio()!=null && secondLevel.getFio()!=null) && ( rel.getParent_fio().equals(secondLevel.getFio()) )){
                                        isRel=false;
                                        break;
                                    }
                                }
                            }

                            if(isRel){
                                flRelativesDtos.add(secondLvlDto);
                                relativesNodesList.add(secondLevel);
                            }
                        }
                    }
                }
            }

            if(firstLevelCnt!=0){
                firstLevel.setHaveRisk(true);
            } else{
                firstLevel.setHaveRisk(false);
            }
            if(dirFounderQuantityfirstlvl!=0){
                firstLevel.setIsDirector(true);
            } else{
                firstLevel.setIsDirector(false);
            }
            firstLevel.setName(firstLevel.getName() + ", Риски к-во: " + firstLevelCnt);

            firstLevel.setChildren(relativesNodesList);

            if(!firstLevel.getFio().equals("null null null")){
                relativesNodes.add(firstLevel);
            }
        }
        nodes.setChildren(relativesNodes);
//            return nodes;
//        }
        return nodes;
    }

    public NodesFL getNode(String IIN){
        NodesFL myNode = new NodesFL();
        try {
            List<MvUlLeader> mv_ul_leaders =  mvUlLeaderRepository.findAllByIin(IIN);
            try {
                myNode.setUl_leaderList(mv_ul_leaders);
            } catch (Exception e) {
                System.out.println("mv_ul_leader Error: " + e);
            }
        } catch (Exception e){
            System.out.println("mv_ul_leader WRAP Error:" + e);
        }
        try {
            List<BeneficiariesList> beneficiariesLists =  beneficiariesListRepo.getBenByIIN(IIN);
            try {
                myNode.setBeneficiariesLists(beneficiariesLists);
            } catch (Exception e) {
                System.out.println("dismissals Error: " + e);
            }
        } catch (Exception e){
            System.out.println("dismissals WRAP Error:" + e);
        }
        try {
            List<ImmoralLifestyle> immoralLifestyles =  immoral_lifestlyeRepo.getImmoByIIN(IIN);
            try {
                myNode.setAmoral(immoralLifestyles);
            } catch (Exception e) {
                System.out.println("immoralLifestyles Error: " + e);
            }
        } catch (Exception e){
            System.out.println("immoralLifestyles WRAP Error:" + e);
        }try {
            List<DrugAddicts> drugAddicts =  drugAddictsRepo.getDrugAddictsByIIN(IIN);
            try {
                myNode.setDrugAddicts(drugAddicts);
            } catch (Exception e) {
                System.out.println("immoralLifestyles Error: " + e);
            }
        } catch (Exception e){
            System.out.println("immoralLifestyles WRAP Error:" + e);
        }try {
            List<Incapacitated> incapacitateds =  incapacitatedRepo.getIncapacitatedByIIN(IIN);
            try {
                myNode.setIncapacitateds(incapacitateds);
            } catch (Exception e) {
                System.out.println("incapacitateds Error: " + e);
            }
        } catch (Exception e){
            System.out.println("incapacitateds WRAP Error:" + e);
        }try {
            List<MvFlAddress> mvFlAddresses =  mvFlAddressRepository.getMvFlAddressByIIN(IIN);
            try {
                myNode.setMvFlAddresses(mvFlAddresses);
            } catch (Exception e) {
                System.out.println("mvFlAddresses Error: " + e);
            }
        } catch (Exception e){
            System.out.println("mvFlAddresses WRAP Error:" + e);
        }try {
            List<RegistrationTemp> registrationTemps =  registrationTempRepository.getRegAddressByIIN(IIN);
            try {
                myNode.setRegistrationTemps(registrationTemps);
            } catch (Exception e) {
                System.out.println("registrationTemps Error: " + e);
            }
        } catch (Exception e){
            System.out.println("registrationTemps WRAP Error:" + e);
        }try {
            List<Dismissal> dismissals =  dismissalRepo.getDismissalByIIN(IIN);
            try {
                myNode.setDismissals(dismissals);
            } catch (Exception e) {
                System.out.println("incapacitateds Error: " + e);
            }
        } catch (Exception e){
            System.out.println("incapacitateds WRAP Error:" + e);
        }try {
            List<ConvictsAbroad> convictsAbroads =  convictsAbroadRepo.getConvictsAbroadByIIN(IIN);
            try {
                myNode.setConvictsAbroads(convictsAbroads);
            } catch (Exception e) {
                System.out.println("immoralLifestyles Error: " + e);
            }
        } catch (Exception e){
            System.out.println("immoralLifestyles WRAP Error:" + e);
        }
        try {
            List<MvAutoFl> myMv_auto_fl =  mvAutoFlRepo.getUsersByLike(IIN);
            try {
                myNode.setMvAutoFls(myMv_auto_fl);
            } catch (Exception e) {
                System.out.println("mv_auto_fl Error: " + e);
            }
        } catch (Exception e){
            System.out.println("mv_auto_fl WRAP Error:" + e);
        }
        try {
            List<MvFl> myMv_fl =  mv_FlRepo.getUsersByLike(IIN);
            try {
                myNode.setMvFls(myMv_fl);
            } catch (Exception e) {
                System.out.println("mv_fl Error: " + e);
            }
        } catch (Exception e){
            System.out.println("mv_fl WRAP Error:" + e);
        }
        try {
            List<Kuis> kuis =  kuisRepo.getKuisByIIN(IIN);
            try {
                myNode.setKuis(kuis);
            } catch (Exception e) {
                System.out.println("kuis Error: " + e);
            }
        } catch (Exception e){
            System.out.println("kuis WRAP Error:" + e);
        }

        try {
            List<Orphans> myOrphans =  orphans_repo.getUsersByLike(IIN);
            try {
                myNode.setOrphans(myOrphans);
            } catch (Exception e) {
                System.out.println("orphans Error: " + e);
            }
        } catch (Exception e){
            System.out.println("orphans WRAP Error:" + e);
        }
        try {
            List<MzEntity> mzEntities =  mzEntityRepo.getMzByIIN(IIN);
            try {
                myNode.setMzEntities(mzEntities);
            } catch (Exception e) {
                System.out.println("orphans Error: " + e);
            }
        } catch (Exception e){
            System.out.println("orphans WRAP Error:" + e);
        }
        try {
            List<Dormant> myDormant =  dormantRepo.getUsersByLike(IIN);
            try {
                myNode.setDormants(myDormant);
            } catch (Exception e) {
                System.out.println("dormant Error: " + e);
            }
        } catch (Exception e){
            System.out.println("dormant WRAP Error:" + e);
        }
        try {
            List<MilitaryAccounting2Entity> militaryAccounting2Entities = MilitaryAccounting2Repo.getUsersByLike(IIN);
            try {
                myNode.setMilitaryAccounting2Entities(militaryAccounting2Entities);
            } catch (Exception e) {
                System.out.println("MilitaryAccounting2Entity Error: " + e);
            }
        } catch (Exception e){
            System.out.println("MilitaryAccounting2Entity WRAP Error:" + e);
        }
        try {
            List<MvRnOld> mvRnOlds = mv_rn_oldRepo.getUsersByLike(IIN);
            myNode.setMvRnOlds(mvRnOlds);
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            List<Equipment> myEquipment =  equipment_repo.getUsersByLike(IIN);
            myNode.setEquipment(myEquipment);
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            List<RegAddressFl> addressFls = regAddressFlRepo.getByIIN(IIN);
            myNode.setRegAddressFls(addressFls);
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            List<String> flPensionContrs = flPensionContrRepo.getUsersByLikeCompany(IIN);

            List<FlPensionFinal> flPensionFinals = new ArrayList<>();
            for(String flPension : flPensionContrs){
                FlPensionFinal flPensionFinal = new FlPensionFinal();
                List<Map<String, Object>> fl_pension_contrss = new ArrayList<>();
                fl_pension_contrss = flPensionContrRepo.getAllByCompanies(IIN,flPension);
                List<Map<String, Object>> r = flPensionContrRepo.findAmountOfAmountByKNP(IIN,flPension);
                List<String> fff = flPensionMiniRepo.getAllByCompaniesYear(IIN,flPension);
                flPensionFinal.setFlPensionMinis(fl_pension_contrss);
                flPensionFinal.setNakoplenya(r);
                flPensionFinal.setYears(fff);
                flPensionFinal.setCompanyBin(flPension);
                flPensionFinals.add(flPensionFinal);
//            System.out.println(findAmountOfAmountByKNPf);
            }
            myNode.setFlPensionContrs(flPensionFinals);
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            List<Msh> mshes = mshRepo.getUsersByLike(IIN);
            myNode.setMshes(mshes);
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            List<IpgoEmailEntity> ipgoEmailEntities = IpgoEmailEntityRepo.getUsersByLike(IIN);
            myNode.setIpgoEmailEntities(ipgoEmailEntities);
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            List<TIpEntity> TIpEntity = TIpEntityRepo.getUsersByLike(IIN);
            try {
                myNode.setTIpEntity(TIpEntity);
            } catch (Exception e) {
                System.out.println("TIpEntity Error: " + e);
            }
        } catch (Exception e){
            System.out.println("TIpEntity WRAP Error:" + e);
        }
        try {
            List<AccountantListEntity> accountantListEntities = accountantListEntityRepo.getUsersByLike(IIN);
            try {
                myNode.setAccountantListEntities(accountantListEntities);
            } catch (Exception e) {
                System.out.println("AccountantListEntity Error: " + e);
            }
        } catch (Exception e){
            System.out.println("AccountantListEntity WRAP Error:" + e);
        }
        try {
            List<AdvocateListEntity> advocateListEntities = advocateListEntityRepo.getUsersByLike(IIN);
            myNode.setAdvocateListEntities(advocateListEntities);
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            List<AuditorsListEntity> auditorsListEntities = auditorsListEntityRepo.getUsersByLike(IIN);
            myNode.setAuditorsListEntities(auditorsListEntities);
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            List<BailiffListEntity> bailiffListEntities = bailiffListEntityRepo.getUsersByLike(IIN);
            myNode.setBailiffListEntities(bailiffListEntities);
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            List<BlockEsf> blockEsfs = block_esfRepo.getblock_esfByIIN(IIN);
            myNode.setBlockEsfs(blockEsfs);
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            List<MvUlFounderFl> mvUlFounderFls = mvUlFounderFlRepo.getUsersByLikeIIN(IIN);
            myNode.setMvUlFounderFls(mvUlFounderFls);
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            List<NdsEntity> ndsEntities = ndsEntityRepo.getUsersByLike(IIN);
            myNode.setNdsEntities(ndsEntities);
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            List<CommodityProducer> commodityProducers = commodityProducerRepo.getiin_binByIIN(IIN);
            myNode.setCommodityProducers(commodityProducers);
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            myNode.setContacts(flContactsRepo.findAllByIin(IIN));
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            myNode.setPdls(pdlReposotory.getByIIN(IIN));
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            myNode.setMvIinDocs(mvIinDocRepo.getByIIN(IIN));
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            myNode.setUniversities(uniRepo.getByIIN(IIN));
        } catch (Exception e){
            logger.error("Error: ", e);
        }
        try {
            myNode.setSchools(schoolRepo.getByIIN(IIN));
        } catch (Exception e){
            logger.error("Error: ", e);
        }
//        try {
//            myNode.setMillitaryAccounts(militaryAccountRepo.findAllByIin(IIN));
//        } catch (Exception e){
//            logger.error("Error: ", e);
//        }
//        List<flPensionMini> flPensionContrs1 = new ArrayList<>();
//        List<String> CompanyNames = flPensionContrRepo.getUsersByLikeCompany(IIN);
        myNode = tryAddPhoto(myNode,IIN);
        return myNode;
    }




    public List<SearchResultModelUl> searchResultUl(String bin) {
        List<MvUl> mvUls = mv_ul_repo.getUsersByLike(bin);
        List<SearchResultModelUl> list = new ArrayList<>();
        for (MvUl l: mvUls) {
            SearchResultModelUl res = new SearchResultModelUl();
            res.setBin(l.getBin());
            res.setName(l.getShort_name());
            list.add(res);
        }

        return list;
    }
    public List<TaxOutEntity> taxOutEntities(String bin, PageRequest pageRequest){
        Page<TaxOutEntity> taxOutEntityPage = taxOutEntityRepo.getUsersByLike(bin,pageRequest);
        return taxOutEntityPage.getContent();
    }
    public List<Map<String, Object>> pensionEntityUl(String bin, String year, PageRequest pageRequest){
        Page<Map<String,Object>> pens = flPensionContrRepo.getPension(bin, year, pageRequest);
        return pens.getContent();
    }
    public List<Map<String,Object>> pensionEntityUl1(String bin, Double year, Integer page){
        Integer offset = page * 10;
        List<Map<String,Object>> pens = flPensionContrRepo.getPension1(bin, year, offset);
        return pens;
    }



    public NodesUL getNodeUL(String BIN) {
        NodesUL myNode = new NodesUL();
        try {
            List<MvUlFounderFl> mvUlFounderFls = mvUlFounderFlRepo.getUsersByLike(BIN);
            myNode.setMvUlFounderFls(mvUlFounderFls);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            List<Bankrot> bankrots = bankrotRepo.getbankrotByByIIN(BIN);
            myNode.setBankrots(bankrots);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            List<MvUl> mvUls = mv_ul_repo.getUsersByLike(BIN);
            myNode.setMvUls(mvUls);
            for(MvUl mv_ul : mvUls){
                mv_ul.setFull_name_rus(mv_ul.getShort_name());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            myNode.setFl_contacts(flContactsRepo.findAllByIin(BIN));
        } catch (Exception e) {
            logger.error("Error: ", e);
        }
        try {
            List<Adm> MyAdm = admRepo.getUsersByLikeBin(BIN);
            myNode.setAdms(MyAdm);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            List<Dormant> myDormant = dormantRepo.getUsersByLike(BIN);
            myNode.setDormants(myDormant);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            RegAddressUlEntity address = regAddressUlEntityRepo.findByBin(BIN);
            myNode.setRegAddressUlEntities(address);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            List<Equipment> myEquipment = equipment_repo.getUsersByLike(BIN);
            myNode.setEquipment(myEquipment);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            List<Omn> myOmns = omn_repos.getUsersByLikeIin_bin(BIN);
            myNode.setOmns(myOmns);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            List<Msh> mshes = mshRepo.getUsersByLike(BIN);
            myNode.setMshes(mshes);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            List<Criminals> criminals = criminalsRepo.getcriminalsByByIIN(BIN);
            myNode.setCriminals(criminals);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            List<BlockEsf> blockEsfs = block_esfRepo.getblock_esfByIIN(BIN);
            myNode.setBlockEsfs(blockEsfs);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            List<OpgEntity> opgEntities = opgRepo.getopgByIIN(BIN);
            myNode.setOpgEntities(opgEntities);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            List<AccountantListEntity> accountantListEntities = accountantListEntityRepo.getUsersByLikeBIN(BIN);
            myNode.setAccountantListEntities(accountantListEntities);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            List<NdsEntity> ndsEntities = ndsEntityRepo.getUsersByLike(BIN);
            myNode.setNdsEntities(ndsEntities);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            List<FpgTempEntity> fpgTempEntities = fpgTempEntityRepo.getUsersByLike(BIN);
            myNode.setFpgTempEntities(fpgTempEntities);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            List<Pdl> pdls = pdlReposotory.getByBin(BIN);
            myNode.setPdls(pdls);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            List<QoldauSubsidy> q = QoldauRepo.getByIIN(BIN);
            myNode.setQoldauSubsidy(q);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            List<CommodityProducer> commodityProducers = commodityProducerRepo.getiin_binByIIN(BIN);
            myNode.setCommodityProducers(commodityProducers);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        try {
            List<MvRnOld> mvRnOlds = mv_rn_oldRepo.getUsersByLike(BIN);
            myNode.setMvRnOlds(mvRnOlds);
        } catch (Exception e) {
            System.out.println("Error: " + e);}
            try {
                RegAddressUlEntity address = regAddressUlEntityRepo.findByBin(BIN);
                RegAddressUlEntity setRegUlNaOdnom = regAddressUlEntityRepo.regAddressNaOdnomMeste(address.getRegAddrRegionRu(), address.getRegAddrDistrictRu()
                        , address.getRegAddrLocalityRu(), address.getRegAddrStreetRu(), address.getRegAddrBuildingNum(), BIN);
                myNode.setRegUlNaOdnomMeste(setRegUlNaOdnom);
                System.out.println(address.getRegAddrRegionKz() + " " + address.getRegAddrDistrictKz() + " " + address.getRegAddrBuildingNum() );
            } catch (Exception e) {
                System.out.println("Error: " + e);

//         }try {
//             List<TaxOutEntity> taxOutEntitiest = taxOutEntityRepo.getUsersByLike(BIN);
//             myNode.setTaxOutEntities(taxOutEntitiest);
//         } catch (Exception e) {
//             System.out.println("Error: " + e);
            }
            try {
                List<MvAutoFl> mvAutoFls = mvAutoFlRepo.getUsersByLike(BIN);
                myNode.setMvAutoFls(mvAutoFls);
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }

            List<MvUlFounderUl> mvUlFounderUls = mvUlFounderUlRepo.getUsersByLike(BIN);
            List<SvedenyaObUchastnikovUlEntity> svedenyaObUchastnikovUlEntities = new ArrayList<>();
            for (MvUlFounderUl mvUlFUl : mvUlFounderUls) {
                SvedenyaObUchastnikovUlEntity svedenyaObUchastnikovUlEntity = new SvedenyaObUchastnikovUlEntity();
                svedenyaObUchastnikovUlEntity.setIin_bin(mvUlFUl.getFounderBin());
                svedenyaObUchastnikovUlEntity.setFIOorUlName(mvUlFUl.getFounderNameRu());
                svedenyaObUchastnikovUlEntity.setReg_date(mvUlFUl.getRegDate());
                if (mvUlFUl.isCurrent()) {
                    svedenyaObUchastnikovUlEntity.setIdentificator("Учредитель ЮЛ");
                } else {
                    svedenyaObUchastnikovUlEntity.setIdentificator("Учредитель ЮЛ (исторический)");
                }
                svedenyaObUchastnikovUlEntities.add(svedenyaObUchastnikovUlEntity);
            }
            List<MvUlLeaderEntity> mvUlLeaderEntities = mvUlLeaderEntityRepo.getUsersByLike(BIN);
            for (MvUlLeaderEntity mvUlFUl : mvUlLeaderEntities) {
                SvedenyaObUchastnikovUlEntity svedenyaObUchastnikovUlEntity = new SvedenyaObUchastnikovUlEntity();
                svedenyaObUchastnikovUlEntity.setIin_bin(mvUlFUl.getIin());
                svedenyaObUchastnikovUlEntity.setFIOorUlName(mvUlFUl.getLastName() + " " + mvUlFUl.getFirstName() + " " + mvUlFUl.getPatronymic());
                svedenyaObUchastnikovUlEntity.setReg_date(mvUlFUl.getRegDate());
                if (mvUlFUl.getCurrent() == true) {
                    svedenyaObUchastnikovUlEntity.setIdentificator("Директор");
                } else {
                    svedenyaObUchastnikovUlEntity.setIdentificator("Директор (исторический)");
                }
                svedenyaObUchastnikovUlEntities.add(svedenyaObUchastnikovUlEntity);

            }
            List<Map<String, Object>> r = flPensionContrRepo.findAmountOfEmployeesOfEveryYear(BIN);
            myNode.setPensionYearAndEmpNum(r);
            myNode.setSvedenyaObUchastnikovUlEntities(svedenyaObUchastnikovUlEntities);
            try {
                if (myNode.getOmns().size() == 0
                        & myNode.getBankrots().size() == 0
                        & myNode.getAdms().size() == 0
                        & myNode.getOpgEntities().size() == 0
                        & myNode.getCriminals().size() == 0
                        & myNode.getBlockEsfs().size() == 0
                        & myNode.getFpgTempEntities().size() == 0) {
                    myNode.setPerson_with_risk(false);
                } else {
                    myNode.setPerson_with_risk(true);
                }
            }catch (Exception e){
                System.out.println("ne poluchiolos");
            }

//         List<FL_PENSION_FINAL> flPensionFinals = new ArrayList<>();
//         FL_PENSION_FINAL flPensionFinal = new FL_PENSION_FINAL();
//         flPensionFinal.setNakoplenya(flPensionContrRepo.findAmountOfEmployeesOfEveryYear(BIN));
//         flPensionFinals.add(flPensionFinal);
//         myNode.setFlPensionContrs(flPensionFinals);

//         for(String flPension : flPensionContrs){
//             List<flPensionMini> fl_pension_contrss = new ArrayList<>();
//             fl_pension_contrss = flPensionMiniRepo.getAllByCompanies(IIN,flPension);
//             List<String> fff = flPensionMiniRepo.getAllByCompaniesYear(IIN,flPension);
////            System.out.println(flPensionContrRepo.findAmountOfAmountByKNP(IIN,flPension));
////            Object findAmountOfAmountByKNPf = flPensionContrRepo.findAmountOfAmountByKNP(IIN,flPension);
////            System.out.printf(String.valueOf(findAmountOfAmountByKNPf.getClass().getName()));
//             flPensionFinal.setFlPensionMinis(fl_pension_contrss);
//             flPensionFinal.setNakoplenya(r);
//             flPensionFinal.setYears(fff);
//             flPensionFinal.setCompanyBin(flPension);
//             flPensionFinals.add(flPensionFinal);
////            System.out.println(findAmountOfAmountByKNPf);
//         }
//         myNode.setFlPensionContrs(flPensionFinals);
//         List<TaxOutEntity> taxOutEntities = taxOutEntityRepo.getUsersByLike(BIN);
            //     myNode.setTaxOutEntities(taxOutEntities);
//         List<FL_PENSION_FINAL> flPensionFinals = new ArrayList<>();
//         List<Integer> adad = flPensionContrRepo.amountOfYears(BIN);
//         for(Integer add : adad){
//             FL_PENSION_FINAL flPensionFinal = new FL_PENSION_FINAL();
//             System.out.println(add);
//             flPensionFinal.setAmountOfEmp(flPensionContrRepo.amountOfEmp(BIN,add));

//             flPensionFinal.setNakoplenya(r);
//             flPensionFinal.setYear(add);
//             flPensionFinals.add(flPensionFinal);
//         }
//         myNode.setFlPensionContrs(flPensionFinals);
        try {
            Integer number = taxOutEntityRepo.getTaxAmount(BIN);
            myNode.setTaxCount(number);
        } catch (Exception e) {
            System.out.println("Tax error: " + e);
        }
            return myNode;
        }


        public FlFirstRowDto getFlFirstRow(String IIN){
            FlFirstRowDto flFirstRowDto = new FlFirstRowDto();
            try {
                List<MvFl> myMv_fl =  mv_FlRepo.getUsersByLike(IIN);
                try {
                    flFirstRowDto.setMvFls(myMv_fl);
                } catch (Exception e) {
                    System.out.println("mv_fl Error: " + e);
                }
            } catch (Exception e){
                System.out.println("mv_fl WRAP Error:" + e);
            }try {
                List<MvIinDoc> mvIinDocs =  mvIinDocRepo.getByIIN(IIN);
                try {
                    flFirstRowDto.setMvIinDocList(mvIinDocs);
                } catch (Exception e) {
                    System.out.println("mvIinDocs Error: " + e);
                }
            } catch (Exception e){
                System.out.println("mvIinDocs WRAP Error:" + e);
            }try {
                FLRiskDto flRiskDto =  flRiskService.findFlRiskByIin(IIN);
                try {
                    flFirstRowDto.setRiskPercentage(flRiskDto.getPercentage());
                } catch (Exception e) {
                    System.out.println("mvIinDocs Error: " + e);
                }
            } catch (Exception e){
                System.out.println("mvIinDocs WRAP Error:" + e);
            }try {
                List<MvFlAddress> mvFlAddresses = mvFlAddressRepository.getMvFlAddressByIIN(IIN);
                flFirstRowDto.setMvFlAddresses(mvFlAddresses);
            } catch (Exception e){
                logger.error("Error: ", e);
            }try {
                List<RegistrationTemp> registrationTemps = registrationTempRepository.getRegAddressByIIN(IIN);
                flFirstRowDto.setRegistrationTemps(registrationTemps);
            } catch (Exception e){
                logger.error("Error: ", e);
            }
            flFirstRowDto = tryAddPhoto(flFirstRowDto,IIN);

            return flFirstRowDto;
        }

    }

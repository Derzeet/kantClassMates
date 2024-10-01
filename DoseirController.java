package kz.dossier.controller;


import com.lowagie.text.*;

import kz.dossier.aspect.ElkAspect;
import kz.dossier.aspect.LoggingAspect;
import jakarta.servlet.http.HttpServletResponse;
//import kz.dossier.elastic.service.LogServiceELastic;
import kz.dossier.aspect.ResultBasedLoggingAspect;
import kz.dossier.dto.dossier.*;
import kz.dossier.models.dossier.*;
import kz.dossier.security.models.Log;
import kz.dossier.service.*;
import kz.dossier.tools.*;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3000)
@RestController
@RequestMapping("/api/pandora/dossier")
public class DoseirController {
    private final MyService myService;
    private final FlRiskServiceImpl flRiskService;
    private final PdfGenerator pdfGenerator;
    private final DocxGenerator docxGenerator;
    private final RnService rnService;
    private final FlService flService;
    private final TransportService transportService;
    private final ULService ulService;
    private final ExcelGenerator excelGenerator;
    private final TicketService ticketService;
    private final ClassMatesService classMatesService;

    @GetMapping("/get-class-mates-school")
    public Page<SearchResultModelFL> getClassMatesSchool(@RequestParam String school_code, @RequestParam String end_date, @RequestParam String grade, @RequestParam(required = false,defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size) throws ParseException{
        return classMatesService.getSchoolMates(school_code,end_date,grade,PageRequest.of(page,size));
    }
    
    @GetMapping("/get-class-mates-uni-with-start-date")
    public Page<SearchResultModelFL> getClassMatesUniS(@RequestParam String study_code, @RequestParam String date, @RequestParam String spec_name, @RequestParam(required = false,defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size) throws ParseException{
        return classMatesService.getUniMatesStartDate(study_code,date,spec_name,PageRequest.of(page,size));
    }
    @GetMapping("/get-class-mates-uni-with-end-date")
    public Page<SearchResultModelFL> getClassMatesUniE(@RequestParam String study_code, @RequestParam String date, @RequestParam String spec_name, @RequestParam(required = false,defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size) throws ParseException{
        return classMatesService.getUniMatesEndDate(study_code,date,spec_name,PageRequest.of(page,size));
    }

    @GetMapping("/get-train-tickets")
    public List<RailwayTicketDTO> getTicketsById(@RequestParam String iin){
        return ticketService.getAllTickets(iin);
    }

    @GetMapping("/get-train-detail")
    public List<RailwayTicketDetailDTO> getTicketsById(@RequestParam String departure_ru,@RequestParam
    String departure_date,@RequestParam String arrival_ru,@RequestParam String arrival_date) throws ParseException {
        return ticketService.getDetailedTicketView(departure_ru, departure_date, arrival_ru, arrival_date);
    }


    @GetMapping("/get-avia-tickets")
    public List<FlightBookingTableDTO> getAllFlightBookings(@RequestParam String iin) {
        return ticketService.getAllFlightBookings(iin);
    }

    @GetMapping("/get-avia-detail")
    public List<FlightBookingDetailDTO> getFlightBookingDetails(@RequestParam String airport_depart_value,@RequestParam String departure_location_code,@RequestParam String departure_date_time,@RequestParam String airport_arrival_value) {
        return ticketService.getFlightBookingDetails(airport_depart_value, departure_location_code, departure_date_time, airport_arrival_value);
    }

    @GetMapping("/fl/get-commodity-producers-by-iinBin")
    public ResponseEntity<List<CommodityProducersDTO>> ulCommodProducersByBin(@RequestParam String iin) {
        return ResponseEntity.ok(ulService.getComProducersByBin(iin));
    }
    @GetMapping("/fl/get-fl-auto-transport")
    public ResponseEntity<List<AutoTransportDto>> getAutoTransportByBinfl(@RequestParam String iin) {
        return ResponseEntity.ok(transportService.getAutoTransportByBin(iin));
    }

    @GetMapping("/fl/get-trains")
    public ResponseEntity<List<Trains>> getTrainsfl(@RequestParam String iin) {
        return ResponseEntity.ok(transportService.getTrains(iin));
    }

    @GetMapping("/fl/get-avia-transport")
    public ResponseEntity<List<AviaTransport>> getAviaTransportfl(@RequestParam String iin) {
        List<AviaTransport> dto = transportService.getAviaTransport(iin);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/fl/get-water-transport")
    public ResponseEntity<List<WaterTransport>> getWaterTransportfl(@RequestParam String iin) {
        List<WaterTransport> dto = transportService.getWaterTransport(iin);
        return ResponseEntity.ok(dto);
    }
    @LoggingAspect.SearchHistory
    @GetMapping("/get-fl-by-iin")
    @ResultBasedLoggingAspect.ParamLogging(action = "Просмотр вкладки ФЛ по ИИН: ${iin} ФИО: ${result.fio}", module = "Поиск объектов", chapter = "Поиск по ИИН", trackResult = true)
    public ResponseEntity<MvFlWithPhotoDto> getMvFl(Principal principal,@RequestParam String iin,  @RequestParam(required = true, defaultValue = "") String type){
        MvFlWithPhotoDto dto = flService.getMvFl(iin);
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/get-fl-doc-iin")
    public ResponseEntity<List<MvIinDoc>> getMvFlDoc(@RequestParam String iin){
        List<MvIinDoc> dto = flService.getMvDocs(iin);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/get-fl-address-iin")
    public ResponseEntity<List<MvFlAddress>> getMvAddress(@RequestParam String iin){
        List<MvFlAddress> dto = flService.getMvFlAddressByIIN(iin);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/get-fl-reg-address-iin")
    public ResponseEntity<List<RegistrationTemp>> getRegAddressFl(@RequestParam String iin){
        List<RegistrationTemp> dto = flService.getRegAddressByIIN(iin);
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/get-fl-historicity-fio")
    public ResponseEntity<List<ChangeFio>> getChangeFio(@RequestParam String iin){
        List<ChangeFio> dto = flService.getChangeFioByIIN(iin);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/get-contacts-details")
    public ResponseEntity<List<FlContacts>> getFlContacts(@RequestParam String iin){
        List<FlContacts> dto = flService.getContactsByIIN(iin);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/fl-lawyers")
    public ResponseEntity<List<AdvocateListEntity>> getAdvocate(@RequestParam String iin){
        List<AdvocateListEntity> dto = flService.getAdvocateListEntity(iin);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/get-accountant")
    public ResponseEntity<List<AccountantListEntity>> getAccountant(@RequestParam String iin){
        List<AccountantListEntity> dto = flService.getAccountantListEntity(iin);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/get-fl-bailiff-iin")
    public ResponseEntity<List<BailiffListEntity>> getBailiffListEntity(@RequestParam String iin){
        List<BailiffListEntity> dto = flService.getBailiffListEntity(iin);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/get-fl-auditor-iin")
    public ResponseEntity<List<AuditorsListEntity>> getAuditorsListEntity(@RequestParam String iin){
        List<AuditorsListEntity> dto = flService.getAuditorsListEntity(iin);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/get-fl-ipgo-iin")
    public ResponseEntity<List<IpgoEmailEntity>> getIpgoEmailEntity(@RequestParam String iin){
        List<IpgoEmailEntity> dto = flService.getIpgoEmailEntity(iin);
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/get-fl-ip-and-kx-iin")
    public ResponseEntity<IpKxDto> getIpKxDto(@RequestParam String iin){
        IpKxDto dto = flService.getIpKxDto(iin);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/get-equipments")
    public ResponseEntity<List<Equipment>> getEquipment(@RequestParam String iin){
        List<Equipment> dto = transportService.getEquipmentByIin(iin);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/get-education")
    public ResponseEntity<EduDto> getEduDto(@RequestParam String iin){
        EduDto dto = flService.getEduDto(iin);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/get-pension")
    public ResponseEntity<PensionAllDto> getPensionAllDto(@RequestParam String iin){
        PensionAllDto dto = flService.getPensionAllDto(iin);
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/get-fl-military")
    public ResponseEntity<List<MilitaryAccountingDTO>> getMilitaryAccountingDTO(@RequestParam String iin){
        List<MilitaryAccountingDTO> dto = flService.getMilitaryAccountingDTO(iin);
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/get-ul-participants")
    public ResponseEntity<UlParticipantsDto> getUlParticipantsDto(@RequestParam String iin){
        UlParticipantsDto dto = flService.getUlParticipantsDto(iin);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/get-fl-rn")
    public ResponseEntity<List<MvRnOld>> getMvRnOld(@RequestParam String iin){
        List<MvRnOld> dto = flService.getMvRnOlds(iin);
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/get-percentagesFl")
    public ResponseEntity<Double> getFlPercentages(@RequestParam String iin) {
        Double ulDto = flService.getSvedenyaPercentageFl(iin);
        return ResponseEntity.ok(ulDto);
    }
    @GetMapping("/get-fl-pdls")
    public ResponseEntity<List<PdlDto>> getPdlByIin(@RequestParam String iin) {
        List<PdlDto> dto = flService.getPdlByIin(iin);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/get-fl-transports")
    public ResponseEntity<List<MvAutoDto>> getTransportByFl(@RequestParam String iin) {
        List<MvAutoDto> dto = transportService.getTransportByBin(iin);
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/sameAddressFl")
    @ResultBasedLoggingAspect.ParamLogging(action = "Переход по кнопке \"Регистрация ФЛ на одном адресе\" в коллапсе \"Адрес прописки\" по ИИН: ${iin}", module = "Поиск объектов", chapter = "Поиск по ИИН", trackResult = true)
    public ResponseEntity<RnFlSameAddressDto> sameAddressFls(@RequestParam String iin) {
        RnFlSameAddressDto dto = myService.getByAddressUsingIin(iin);
        return ResponseEntity.ok(dto);
    }
    @LoggingAspect.SearchHistory
    @GetMapping("/sameAddressUl")
    @ResultBasedLoggingAspect.ParamLogging(action = "Переход по кнопке \"Регистрация ФЛ на одном адресе\" в коллапсе \"Адрес прописки\" по ИИН: ${iin}", module = "Поиск объектов", chapter = "Поиск по ИИН", trackResult = true)

    public List<SearchResultModelUl> sameAddressUls(@RequestParam String bin) {
        return myService.getByAddress(bin);
    }
    @LoggingAspect.SearchHistory

    @GetMapping("/rnDetails")
    @ResultBasedLoggingAspect.ParamLogging(action = "Переход по кнопке \"Детальный просмотр\" в коллапсе \"Сведения по РН\" по ИИН: ${iin}", module = "Поиск объектов", chapter = "Поиск по ИИН", trackResult = true)

    public List<RnDTO> getMethodName(@RequestParam String cadastral, @RequestParam String address) {
        return rnService.getDetailedRnView(cadastral, address);
    }


    @GetMapping("/additionalInfo")
    public AdditionalInfoDTO getAdditionalInfo(@RequestParam String iin) {
        return myService.additionalInfoByIin(iin);
    }
    @LoggingAspect.SearchHistory
    @GetMapping("/getRelativesInfo")
    @ResultBasedLoggingAspect.ParamLogging(action = "Переход по кнопке \"Сведения о родственных связях\" по ИИН: ${iin}", module = "Поиск объектов", chapter = "Поиск по ИИН", trackResult = true)
    public ResponseEntity<List<FlRelativiesDTO>> getRelInfo(@RequestParam String iin){
        List<FlRelativiesDTO> ff = myService.getRelativesInfo(iin);
        return ResponseEntity.ok(ff);
    }
    @GetMapping("/pensionDetails")
    public List<PensionListDTO> getPesionDetails(
            @RequestParam String iin,
            @RequestParam String bin,
            @RequestParam String year,
            @RequestHeader("Authorization") String authorizationHeader) {
        return myService.getPensionDetails(iin, bin, year);
    }
    @GetMapping("/pensionDetails010")
    public List<PensionListDTO> getPesionDetails010(@RequestParam String iin, @RequestParam String bin, @RequestParam String year) {
        return myService.getPensionDetails(iin, bin, year);
    }


    @GetMapping("/profile")
    public NodesFL getProfile(@RequestParam String iin) {
        return myService.getNode(iin);
    }
    @LoggingAspect.SearchHistory
    @GetMapping("/getRiskByIin")
    @ResultBasedLoggingAspect.ParamLogging(action = "Детальный просмотр вкладки 'Риски' по ИИН: ${iin}", module = "Поиск объектов", chapter = "Поиск по ИИН", trackResult = true)
    public FLRiskDto getRisk(@RequestParam String iin){
        return flRiskService.findFlRiskByIin(iin);
    }
    @GetMapping("/getFirstRowByIin")
    public FlFirstRowDto getFirstRow(@RequestParam String iin){
        return myService.getFlFirstRow(iin);
    }

    @GetMapping("/taxpage")
    public List<TaxOutEntity> getTax(
            @RequestParam String bin,
            @RequestParam(required = false,defaultValue = "0") int page,
            @RequestParam(required = false,defaultValue = "10") int size) {
        return myService.taxOutEntities(bin,PageRequest.of(page,size));
    }

    @GetMapping("/pensionUl")
    public List<Map<String, Object>> pensionUl(@RequestParam String bin, @RequestParam String year, @RequestParam(required = false,defaultValue = "0") int page, @RequestParam(required = false,defaultValue = "10") int size) {
        return myService.pensionEntityUl(bin, year, PageRequest.of(page,size));
    }

    @GetMapping("/pensionsbyyear")
    public List<Map<String,Object>> pensionUl1(@RequestParam String bin, @RequestParam Double year, @RequestParam Integer page) {
        return myService.pensionEntityUl1(bin, year, page);
    }
    @LoggingAspect.SearchHistory
    @GetMapping("/hierarchy")
    @ResultBasedLoggingAspect.ParamLogging(action = "Переход по кнопке \"Сведения о иерархий родственных связях\" по ИИН: ${iin}", module = "Поиск объектов", chapter = "Поиск по ИИН", trackResult = true)
    public FlRelativesLevelDto hierarchy(@RequestParam String iin) throws SQLException {
        return myService.createHierarchyObject(iin);
    }

    @LoggingAspect.SearchHistory
    @GetMapping("/iin")
    @ResultBasedLoggingAspect.ParamLogging(action = "Поиск ФЛ по ИИН: ${iin} ФИО: ${result.fio}", module = "Поиск объектов", chapter = "Поиск по ИИН", trackResult = true)
    public Page<SearchResultModelFL> getByIIN(Principal principal, @RequestParam String iin, @RequestParam(required = false, defaultValue = "") String approvement,@RequestParam(required = false,defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size) throws IOException {
        Page<SearchResultModelFL> fl;
        try {
            fl = myService.getByIIN_photo(iin,PageRequest.of(page, size));
        } catch (Exception e) {
            return null;
        }
        return fl;
    }

    @LoggingAspect.SearchHistory
    @GetMapping("/nomer_doc")
    @ResultBasedLoggingAspect.ParamLogging(action = "Поиск ФЛ по номеру документа: ${doc} ИИН: ${result.iin} ФИО: ${result.fio}", module = "Поиск объектов", chapter = "Поиск по ИИН", trackResult = true)
    public List<SearchResultModelFL> getByDoc(Principal principal, @RequestParam String doc, @RequestParam(required = false) String approvement) {
        return myService.getByDoc_photo(doc);
    }

    @LoggingAspect.SearchHistory
    @GetMapping("/additionalfio")
    public List<SearchResultModelFL> getByAdditions(Principal principal, @RequestParam HashMap<String, String> req,@RequestParam(required = false,defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size) {
        return myService.getWIthAddFields(req);
    }

    @LoggingAspect.SearchHistory
    @GetMapping("/byphone")
    public Page<SearchResultModelFL> getByPhone(Principal principal, @RequestParam String phone, @RequestParam(required = false) String approvement,@RequestParam(required = false,defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size) {
        return myService.getByPhone(phone,PageRequest.of(page,size));
    }

    @LoggingAspect.SearchHistory
    @GetMapping("/byemail")
    public Page<SearchResultModelFL> getByEmail(Principal principal, @RequestParam String email, @RequestParam(required = false) String approvement,@RequestParam(required = false,defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size) {
        return myService.getByEmail(email,PageRequest.of(page,size));
    }

    @LoggingAspect.SearchHistory
    @GetMapping("/byvinkuzov")
    public Page<SearchResultModelFL> getByVinKuzov(Principal principal, @RequestParam String vin,@RequestParam(required = false,defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size) {
        return myService.getByVinFl(vin.toUpperCase(), PageRequest.of(page,size));
    }

    @GetMapping("/fio")
    @ResultBasedLoggingAspect.ParamLogging(action = "Поиск ФЛ по ${lastName} ${firstName} ${patronymic}", module = "Поиск объектов", chapter = "Поиск по ФИО")
    public Page<SearchResultModelFL> findByFIO(@RequestParam String i, @RequestParam String o, @RequestParam String f, @RequestParam(required = false) String approvement, @RequestParam(required = false,defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size) {
        return myService.getByFIO_photo(i.replace('$', '%'), o.replace('$', '%'), f.replace('$', '%'), PageRequest.of(page,size));
    }
    @ResultBasedLoggingAspect.ParamLogging(action = "Скачать документ PDF ФЛ по ИИН: ${iin}", module = "Поиск объектов", chapter = "Поиск по ИИН")
    @GetMapping(value = "/downloadFlPdf/{iin}", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] generatePdfFile(HttpServletResponse response, @PathVariable("iin")String iin,Principal principal)throws IOException, DocumentException {
        response.setContentType("application/pdf");
        String headerkey = "Content-Disposition";
        String headervalue = "attachment; filename=doc" + ".pdf";
        response.setHeader(headerkey, headervalue);
        NodesFL r =  myService.getNode(iin);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pdfGenerator.generate(r, baos,principal, iin);
        return baos.toByteArray();
    }
    @ResultBasedLoggingAspect.ParamLogging(action = "Скачать документ Excel ФЛ по ИИН: ${iin}", module = "Поиск объектов", chapter = "Поиск по ИИН")
    @GetMapping("/downloadFlDoc/{iin}")
    public byte[] generateDoc(@PathVariable String iin, HttpServletResponse response,Principal principal) throws IOException, InvalidFormatException {
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        String headerkey = "Content-Disposition";
        String headervalue = "attachment; filename=document.docx";
        response.setHeader(headerkey,headervalue);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        NodesFL result =  myService.getNode(iin);
        docxGenerator.generateDoc(result,baos,principal, iin);
        return baos.toByteArray();
    }
    @GetMapping("/export-to-excel")
    public void exportIntoExcelFile(HttpServletResponse response,Principal principal) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=student" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        excelGenerator.generateExcelFileForUsers(response,principal);
    }
    @GetMapping(value = "/downloadUsersPdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] generatePdfFileUsers(HttpServletResponse response,Principal principal)throws IOException, DocumentException {
        response.setContentType("application/pdf");
        String headerkey = "Content-Disposition";
        String headervalue = "attachment; filename=doc" + ".pdf";
        response.setHeader(headerkey, headervalue);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pdfGenerator.generateForAdminUsers(baos, principal);
        return baos.toByteArray();
    }

    @GetMapping("/getСrossingToDossierIin")
    @ElkAspect.Loggable(action = "Переход", module = "Поиск объектов", chapter = "Поиск по ИИН")
    public String getPerehodToDossierIin(Principal principal){
        return "perehod to dossier iin";
    }
    @GetMapping("/getСrossingToDossierBin")
    @ElkAspect.Loggable(action = "Переход", module = "Поиск объектов", chapter = "Поиск по БИН")
    public String getPerehodToDossierBin(Principal principal){
        return "perehod to dossier bin";
    }
    @GetMapping("/getСrossingToDossierFio")
    @ElkAspect.Loggable(action = "Переход", module = "Поиск объектов", chapter = "Поиск по ФИО")
    public String getPerehodToDossierFio(Principal principal){
        return "perehod to dossier fio";
    }
    @GetMapping("/getСrossingToDossierUlName")
    @ElkAspect.Loggable(action = "Переход", module = "Поиск объектов", chapter = "Поиск по наименованию ЮЛ")
    public String getPerehodToDossierUlName(Principal principal){
        return "perehod to dossier UlName";
    }

}

package anb52.jobhunter.controller;


import anb52.jobhunter.domain.Company;
import anb52.jobhunter.domain.response.ResultPaginationDTO;
import anb52.jobhunter.service.CompanyService;
import anb52.jobhunter.util.annotation.ApiMessage;
import anb52.jobhunter.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
@Autowired
private CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    @ApiMessage("create a company")
    public ResponseEntity<Company> createNewCompany(@Valid @RequestBody Company company){
       Company newCompany = companyService.handleCreateCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }
    @GetMapping("/companies")
    @ApiMessage("Get company with pagination")
    public ResponseEntity<ResultPaginationDTO> fetchAllCompany(
            @Filter Specification spec, Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(
                this.companyService.fetchAllCompany(spec, pageable));
    }

    @GetMapping("/companies/{id}")
    @ApiMessage(("fetch company by id"))
    public ResponseEntity<Company> fetchCompanyById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Company> company = this.companyService.findById(id);
        if(!company.isPresent()){
            throw new IdInvalidException("id truyền lên không tồn tại");
        }
        Company companyNew = company.get();
        return ResponseEntity.status(HttpStatus.OK).body(companyNew);
    }
    @PutMapping("/companies")
    @ApiMessage("Update a company")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company company){
        Company newCompany = companyService.updateCompany(company);
        return ResponseEntity.status(HttpStatus.OK).body(newCompany);
    }
    @DeleteMapping("/companies/{id}")
    @ApiMessage("delete company by id")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id){
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}

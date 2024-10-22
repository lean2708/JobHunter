package anb52.jobhunter.service;


import anb52.jobhunter.domain.Company;
import anb52.jobhunter.domain.User;
import anb52.jobhunter.domain.response.ResultPaginationDTO;
import anb52.jobhunter.repository.CompanyRepository;
import anb52.jobhunter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {
    @Autowired
    private final CompanyRepository companyRepository;
    @Autowired
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company handleCreateCompany(@RequestBody Company company){
        return companyRepository.save(company);
    }

    public ResultPaginationDTO fetchAllCompany(Specification spec, Pageable pageable){
        Page pageCompany = this.companyRepository.findAll(spec, pageable);

        ResultPaginationDTO rp = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber()+ 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageCompany.getTotalPages());
        mt.setTotal(pageCompany.getTotalElements());

        rp.setMeta(mt);
        rp.setResult(pageCompany.getContent());

        return rp;
    }
    public Company fetchCompany(long id){
        Optional<Company> companyOptional = companyRepository.findById(id);
        if(companyOptional.isPresent()){
            return companyOptional.get();
        }
        return null;
    }
    public Company updateCompany(Company newCompany){
        Company company = fetchCompany(newCompany.getId());
        if(company != null){
            company.setName(newCompany.getName());
            company.setDescription(newCompany.getDescription());
            company.setAddress(newCompany.getAddress());
            company.setLogo(newCompany.getLogo());
            this.companyRepository.save(company);
        }
        return company;
    }

    public void deleteCompany(long id){
        Optional<Company> companyOptional = this.findById(id);
        if(companyOptional.isPresent()){
            Company deleteCompany = companyOptional.get();
            List<User> listUser = userRepository.findByCompany(deleteCompany);
            userRepository.deleteAll(listUser);
        }
        companyRepository.deleteById(id);
    }

    public Optional<Company> findById(long id) {
        return companyRepository.findById(id);
    }
}

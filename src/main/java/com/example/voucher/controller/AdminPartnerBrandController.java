package com.example.voucher.controller;

import com.example.voucher.entity.PartnerBrand;
import com.example.voucher.service.PartnerBrandService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/partner-brands")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPartnerBrandController {

    private final PartnerBrandService service;

    public AdminPartnerBrandController(PartnerBrandService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PartnerBrand>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<PartnerBrand> create(@RequestBody PartnerBrand brand) {
        return ResponseEntity.ok(service.create(brand));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartnerBrand> update(@PathVariable Long id, @RequestBody PartnerBrand brand) {
        return ResponseEntity.ok(service.update(id, brand));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        service.activate(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.ok().build();
    }
}

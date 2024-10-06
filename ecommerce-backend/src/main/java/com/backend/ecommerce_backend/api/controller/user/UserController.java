package com.backend.ecommerce_backend.api.controller.user;

import com.backend.ecommerce_backend.model.Address;
import com.backend.ecommerce_backend.model.LocalUser;
import com.backend.ecommerce_backend.model.dao.AddressRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    private AddressRepo addressRepo;

    public UserController(AddressRepo addressRepo) {
        this.addressRepo = addressRepo;
    }

    @GetMapping("/{user_id}/address")
    public ResponseEntity<List<Address>> getAddress(@AuthenticationPrincipal LocalUser user, @PathVariable long user_id) {
        if(!userHasPermission(user,user_id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(addressRepo.findByUser_Id(user_id));
    }

    @PutMapping("/{user_id}/address")
    public ResponseEntity<Address> putAddress(@AuthenticationPrincipal LocalUser user,
                                              @PathVariable long user_id,
                                              @RequestBody Address address) {

        if(!userHasPermission(user,user_id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        address.setId(null);                 // prevent injecting IDs
        LocalUser refUser = new LocalUser(); // so admin can edit sb else's address
        refUser.setId(user_id);
        address.setUser(refUser);
        return ResponseEntity.ok(addressRepo.save(address));
    }
    @PatchMapping("/{user_id}/address/{address_id}")
    public ResponseEntity<Address> patchAddress(@AuthenticationPrincipal LocalUser user,
                                                @PathVariable long user_id,
                                                @PathVariable long address_id,
                                                @RequestBody Address address) {
        if(!userHasPermission(user,user_id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if(address.getId() == address_id) {
            Optional<Address> oldAddress = addressRepo.findById(address_id);
            if(oldAddress.isPresent()) {
                if(oldAddress.get().getUser().getId() != user_id) {
                    address.setUser(oldAddress.get().getUser());
                    return ResponseEntity.ok(addressRepo.save(address));
                }
            }
        }
        return ResponseEntity.badRequest().build();
    }
    private boolean userHasPermission(LocalUser user, long id) {
        return user.getId() == id;
    }
}

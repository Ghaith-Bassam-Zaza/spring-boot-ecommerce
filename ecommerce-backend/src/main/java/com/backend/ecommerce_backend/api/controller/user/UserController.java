package com.backend.ecommerce_backend.api.controller.user;

import com.backend.ecommerce_backend.api.model.DataChange;
import com.backend.ecommerce_backend.model.Address;
import com.backend.ecommerce_backend.model.LocalUser;
import com.backend.ecommerce_backend.model.dao.AddressRepo;
import com.backend.ecommerce_backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    private AddressRepo addressRepo;
    private SimpMessagingTemplate messagingTemplate;
    private UserService userService;

    public UserController(AddressRepo addressRepo, SimpMessagingTemplate messagingTemplate, UserService userService) {
        this.addressRepo = addressRepo;
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
    }



    @GetMapping("/{user_id}/address")
    public ResponseEntity<List<Address>> getAddress(@AuthenticationPrincipal LocalUser user, @PathVariable long user_id) {
        if(!userService.userHasPermissionToUser(user,user_id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(addressRepo.findByUser_Id(user_id));
    }

    @PutMapping("/{user_id}/address")
    public ResponseEntity<Address> putAddress(@AuthenticationPrincipal LocalUser user,
                                              @PathVariable long user_id,
                                              @RequestBody Address address) {

        if(!userService.userHasPermissionToUser(user,user_id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        address.setId(null);                 // prevent injecting IDs
        LocalUser refUser = new LocalUser(); // so admin can edit sb else's address
        refUser.setId(user_id);
        address.setUser(refUser);
        Address savedAddress = addressRepo.save(address);
        messagingTemplate.convertAndSend("/topic/user/" + user_id + "/address", new DataChange<>(address,DataChange.ChangeType.INSERT));
        return ResponseEntity.ok(savedAddress);
    }
    @PatchMapping("/{user_id}/address/{address_id}")
    public ResponseEntity<Address> patchAddress(@AuthenticationPrincipal LocalUser user,
                                                @PathVariable long user_id,
                                                @PathVariable long address_id,
                                                @RequestBody Address address) {
        if(!userService.userHasPermissionToUser(user,user_id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if(address.getId() == address_id) {
            Optional<Address> oldAddress = addressRepo.findById(address_id);
            if(oldAddress.isPresent()) {
                if(oldAddress.get().getUser().getId() != user_id) {
                    address.setUser(oldAddress.get().getUser());
                    Address savedAddress = addressRepo.save(address);
                    messagingTemplate.convertAndSend("/topic/user/" + user_id + "/address", new DataChange<>(address,DataChange.ChangeType.UPDATE));
                    return ResponseEntity.ok(savedAddress);
                }
            }
        }
        return ResponseEntity.badRequest().build();
    }

}

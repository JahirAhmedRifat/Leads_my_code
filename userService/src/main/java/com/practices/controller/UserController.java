package com.practices.controller;

import com.practices.common.ApiResponse;
import com.practices.common.ApiResponseBuilder;
import com.practices.dto.UserRequest;
import com.practices.dto.response.UserResponse;
import com.practices.services.IUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    // url : http://localhost:8090/test/user/save

    private final IUserService userService;
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    // ---------------🔹 Create -------------
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody UserRequest userObj) {
        UserResponse user = userService.saveUser(userObj);
        return ApiResponseBuilder.build("User created successfully", true, HttpStatus.CREATED, user);
    }
    // --------- for @Builder type --------
//    @PostMapping("/save")
//    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody UserRequest userObj){
//        UserRequest user=this.userService.saveUser(userObj);
//        ApiResponse response= ApiResponse.builder()
//                .message("User created successfully")
//                .success(true)
//                .status(HttpStatus.CREATED)
//                .payload(user)
//                .build();
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
////        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
////        return ResponseEntity.ok(response);
//
//        //------- 3 ways-----
////        new ResponseEntity<>(body, status)
////        ResponseEntity.status(status).body(body)
////        ResponseEntity.ok(body)
//
//    }
    // --------- for @Accessors(chain = true) type in create user --------
//    @PostMapping("/save")
//    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody UserRequest userObj) {
//        UserRequest user = userService.saveUser(userObj);
//        ApiResponse response = new ApiResponse()
//                .setMessage("User created successfully")
//                .setSuccess(true)
//                .setStatus(HttpStatus.CREATED)
//                .setPayload(user);
//
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }

    // ----------🔹 get all users --------------
    @GetMapping("/show")
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<UserResponse> users = userService.getAllUser();
        return ApiResponseBuilder.build("All users fetched successfully", true, HttpStatus.OK, users);
    }

//    @GetMapping("/show")
//    public ResponseEntity<ApiResponse> getAllUsers(){
//        List<UserRequest> allUser=this.userService.getAllUser();
//        ApiResponse response= ApiResponse.builder()
//                .message("All users fetched successfully")
//                .success(true)
//                .status(HttpStatus.OK)
//                .payload(allUser)
//                .build();
//        return ResponseEntity.ok(response);
//    }

    // ---------------- 🔹 get user by userId --------------
    @GetMapping("/getUser/{userId}")
    public ResponseEntity<ApiResponse> getUserByUserId(@PathVariable String userId) {
        UserResponse user = userService.getUserByUserId(userId); // Suppose this method throws if not found
        return ApiResponseBuilder.build("User fetched successfully", true, HttpStatus.OK, user);
    }

    // --------------🔹 Update --------------
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateUser(@Valid @RequestBody UserRequest request){
        UserResponse updateUser= userService.updateUser(request);
        return ApiResponseBuilder.build("User updated successfully", true, HttpStatus.OK, updateUser);
    }

//    @PutMapping("/update/{id}")
//    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest updatedUser){
//        UserRequest user = userService.updateUser(id, updatedUser);
//        ApiResponse response = ApiResponse.builder()
//                .message("User updated successfully")
//                .success(true)
//                .status(HttpStatus.OK)
//                .payload(user)
//                .build();
//        return ResponseEntity.ok(response);
//    }

        // --------- for Soft-delete ------
    @GetMapping("/delete/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable String userId) {
        UserResponse user = userService.deleteUserByUserId(userId);
        return ApiResponseBuilder.build("User deleted successfully (soft delete)", true, HttpStatus.OK, user);
    }


//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id){
//        userService.deleteUser(id);
//        ApiResponse response = ApiResponse.builder()
//                .message("User deleted successfully")
//                .success(true)
//                .status(HttpStatus.OK)
//                .payload(null)
//                .build();
//        return ResponseEntity.ok(response);
//    }

}

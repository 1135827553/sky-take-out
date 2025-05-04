package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "C端-地址簿接口")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @GetMapping("/list")
    @ApiOperation("查询当前登录用户的所有地址信息")
    public Result<List<AddressBook>> list(){
        log.info("查询当前登录用户的所有地址信息");
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(addressBook);
        return Result.success(list);
    }



    @PostMapping()
    @ApiOperation("新增地址")
    public Result save(@RequestBody AddressBook addressBook) {
        log.info("新增地址:{}",addressBook);
        addressBookService.save(addressBook);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("查询指定id地址")
    public Result<AddressBook> getById(@PathVariable Long id) {
        log.info("查询指定id:{}",id);
        return Result.success(addressBookService.getById(id));
    }

    @PutMapping
    @ApiOperation("修改地址")
    public Result updateAddress(@RequestBody AddressBook addressBook) {
        log.info("修改地址:{}",addressBook);
        addressBookService.updateAddressBook(addressBook);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("删除地址")
    public Result deleteAddress(Long id){
        log.info("删除地址:{}",id);
        addressBookService.deleteAddressBook(id);
        return Result.success();
    }

    @GetMapping("/default")
    @ApiOperation("查询默认地址")
    public Result<AddressBook> getDefault() {
        log.info("查询默认地址");
        AddressBook addressBook = new AddressBook();
        addressBook.setIsDefault(1);
        addressBook.setUserId(BaseContext.getCurrentId());

        List<AddressBook> list = addressBookService.list(addressBook);
        if (list != null && list.size() == 1) {
            return Result.success(list.get(0));
        }
        return Result.error("没有查询默认地址");
    }

    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result setDefault(@RequestBody AddressBook addressBook){
        log.info("设置默认地址:{}",addressBook);
        addressBookService.setDefault(addressBook);
        return Result.success();
    }


}

package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;

    @Override
    public List<AddressBook> list(AddressBook addressBook) {
        return addressBookMapper.list(addressBook);
    }

    @Override
    public void save(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.saveAddressBookMapper(addressBook);
    }

    @Override
    public void updateAddressBook(AddressBook addressBook) {
        addressBookMapper.updateAddressBook(addressBook);
    }

    @Override
    public void deleteAddressBook(Long id) {
        addressBookMapper.deleteAddressBook(id);
    }

    @Override
    public void setDefault(AddressBook addressBook) {
        //1、将当前用户的所有地址修改为非默认地址
        addressBook.setIsDefault(0);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.updateIsDefaultByUserId(addressBook);
        //2、将当前地址改为默认地址 update address_book set is_default = ? where id = ?
        addressBook.setIsDefault(1);
        addressBookMapper.updateAddressBook(addressBook);
    }

    @Override
    public AddressBook getById(Long id) {
        return addressBookMapper.getById(id);
    }



}



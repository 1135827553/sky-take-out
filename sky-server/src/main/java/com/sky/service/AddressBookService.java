package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    List<AddressBook> list(AddressBook addressBook);

    void save(AddressBook addressBook);

    AddressBook getById(Long id);

    void updateAddressBook(AddressBook addressBook);

    void deleteAddressBook(Long id);

    void setDefault(AddressBook addressBook);

}

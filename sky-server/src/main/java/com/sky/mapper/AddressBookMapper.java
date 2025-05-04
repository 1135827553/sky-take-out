package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressBookMapper {



    @Insert("INSERT INTO address_book(user_id, consignee, sex, phone, province_code, province_name, " +
            "city_code, city_name, district_code, district_name, detail, label, is_default)VALUES " +
            "(#{userId},#{consignee},#{sex},#{phone},#{provinceCode},#{provinceName}," +
            "#{cityCode},#{cityName},#{districtCode},#{districtName},#{detail},#{label},#{isDefault})")
    void saveAddressBookMapper(AddressBook addressBook);

    @Select("SELECT * FROM address_book where id =#{id}")
    AddressBook getById(Long id);

    void updateAddressBook(AddressBook addressBook);

    @Delete("DELETE from address_book where id=#{id}")
    void deleteAddressBook(Long id);

    @Update("UPDATE address_book set is_default=#{isDefault} where user_id=#{id}")
    void updateIsDefaultByUserId(AddressBook addressBook);

    List<AddressBook> list(AddressBook addressBook);
}

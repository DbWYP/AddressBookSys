package com.swpuwyp.saexperiment332.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swpuwyp.saexperiment332.entity.Contact;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContactMapper extends BaseMapper<Contact> {
    void addContact(String name, String phone);

    List<Contact> queryContactByName(String name);

    List<Contact> queryContactByPhone(String phone);

    List<Contact> queryAllContacts();

    boolean deleteContact(String name, String phone);

    List<Contact> queryContactByNameAndPhone(String name, String phone);

    boolean modifyContact(String oldInfo, String newName, String newPhoneNum);

}

package com.swpuwyp.saexperiment332.service;

import com.swpuwyp.saexperiment332.entity.Contact;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

// 服务层
@Service
public interface ContactService {
//    private final DatabaseManager dbManager;
//
//    // 通过构造函数注入 DatabaseManager 依赖
//    public ContactService(DatabaseManager dbManager) {
//        this.dbManager = dbManager;
//    }

    // 处理添加信息
    String addContact(String name, String phone);

    // 处理删除信息
    String deleteContact(String name, String phone);

    // 查询联系人信息
    List<Contact> queryContacts(String name, String phone) throws SQLException;

    // 修改联系人信息
    String modifyContact(String oldInfo, String newName, String newPhone);

}

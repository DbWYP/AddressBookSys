package com.swpuwyp.saexperiment332.service.impl;

import com.swpuwyp.saexperiment332.mapper.ContactMapper;
import com.swpuwyp.saexperiment332.service.ContactService;
import com.swpuwyp.saexperiment332.entity.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class ContactServiceImpl implements ContactService {
    @Autowired
    private ContactMapper contactMapper;

    @Override
    public String addContact(String name, String phone) {
        if (name.isEmpty() || phone.isEmpty()) {
            return "请填写完整信息！";
        }

        if (!isValidPhoneNumber(phone)) {
            return "电话号码格式不正确！";
        }

        try {
            contactMapper.addContact(name, phone);
            System.out.println(name + phone);
            return "联系人添加成功！\n姓名：" + name + "\n电话：" + phone;
        } catch (Exception e) {
            System.out.println("失败了哦" );
            e.printStackTrace();
            return "添加联系人失败：" + e.getMessage();
        }
    }

    // 处理删除信息
    @Override
    public String deleteContact(String name, String phone) {
        if (name.isEmpty() && phone.isEmpty()) {
            return "请填写完整信息！";
        }

        if (phone.isEmpty() || !isValidPhoneNumber(phone)) {
            return "请输入正确的手机号！";
        }

        try {
            boolean success = contactMapper.deleteContact(name, phone);
            if (success) {
                return "信息删除成功！\n姓名：" + name + "\n电话：" + phone;
            } else {
                return "查无此人！请检查后重新输入";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "删除联系人失败：" + e.getMessage();
        }
    }

    // 查询联系人信息
    @Override
    public List<Contact> queryContacts(String name, String phone){
        if (!name.isEmpty() && !phone.isEmpty()) {
            return contactMapper.queryContactByNameAndPhone(name, phone);
        } else if (!name.isEmpty() && phone.isEmpty()) {
            return contactMapper.queryContactByName(name);
        } else if(name.isEmpty() && !phone.isEmpty()) {
            return contactMapper.queryContactByPhone(phone);
        } else if(name.isEmpty() && phone.isEmpty()){
            return contactMapper.queryAllContacts();
        }else{
            return null;
        }
    }

    // 修改联系人信息
    @Override
    public String modifyContact(String oldInfo, String newName, String newPhone) {
        if (newName.isEmpty() || newPhone.isEmpty() || !isValidPhoneNumber(newPhone)) {
            return "请输入有效的姓名和电话号码！";
        }

        try {
            System.out.println("我进来啦！");
            oldInfo = oldInfo.trim();
            boolean success = contactMapper.modifyContact(oldInfo, newName, newPhone);
            return success ? "联系人信息修改成功！" : "修改失败！";
        } catch (Exception e) {
            e.printStackTrace();
            return "修改联系人失败：" + e.getMessage();
        }
    }

    // 验证手机号是否有效
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("\\d{11}");
    }
}

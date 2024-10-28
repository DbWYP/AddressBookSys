package 软件体系结构实验三.三层CS模式;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContactService {
    private DatabaseManager dbManager;

    public ContactService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    // 处理添加信息
    public String addContact(String name, String phone) {
        if (!phone.isEmpty() && !name.isEmpty()) {
            if (isValidPhoneNumber(phone)){
                dbManager.addContact(name, phone);
                return "联系人添加成功！\n姓名：" + name + "\n电话：" + phone;
            }else {
                return "电话号码格式不正确！";
            }
        } else {
            return "请填写完整信息！";
        }
    }

    // 处理删除信息
    public String deleteContact(String name, String phone) {
        if (!phone.isEmpty() && !name.isEmpty()) {
            if(!isValidPhoneNumber(phone)){
                return "请输入正确的手机号！";
            }else {
                if (dbManager.deleteContact(name, phone)){
                    return "信息删除成功！\n姓名：" + name + "\n电话：" + phone;
                }else{
                    return "查无此人！请检查后重新输入";
                }
            }
        }else{
            return "请填写完整信息！";
        }
    }

    public ResultSet queryContacts(String name, String phone) {
        // 输入名字按照名字模糊查询
        if (!name.isEmpty()) {
            return dbManager.queryContactByName(name);
        }

        // 输入手机号按照手机号查询
        else if (!phone.isEmpty()) {
            if(isValidPhoneNumber(phone)){
                return dbManager.queryContactByPhone(phone);
            }else{
                return null;
            }
        }

        // 什么都没输显示全部记录
        else{
            return dbManager.queryAllContacts();
        }
    }

    /*public ResultSet queryContactByName(String name) {
        return dbManager.queryContactByName(name);
    }

    public ResultSet queryContactByPhone(String phone) {
        return dbManager.queryContactByPhone(phone);
    }

    public ResultSet queryAllContacts() {
        return dbManager.queryAllContacts();
    }*/

    public ResultSet queryContactByNameOrPhone(String oldInfo) {
        return dbManager.queryContactByNameOrPhone(oldInfo);
    }

    public String modifyContact(String oldInfo, String newName, String newPhone) {
        if (newName.isEmpty() || !isValidPhoneNumber(newPhone)) {
            return "请输入有效的姓名和电话号码！";
        }

        boolean success = dbManager.modifyContact(oldInfo, newName, newPhone);
        if (success) {
            return "联系人信息修改成功！";
        } else {
            return "修改失败，未找到联系人。";
        }
    }

    public void close(){
        dbManager.close();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{11}");
    }
}